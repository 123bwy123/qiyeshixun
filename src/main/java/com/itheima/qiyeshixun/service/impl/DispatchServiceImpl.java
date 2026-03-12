package com.itheima.qiyeshixun.service.impl;

import cn.hutool.core.util.IdUtil;
import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.mapper.*;
import com.itheima.qiyeshixun.po.CustomerOrder;
import com.itheima.qiyeshixun.po.SystemUser;
import com.itheima.qiyeshixun.service.DispatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class DispatchServiceImpl implements DispatchService {

    @Autowired
    private CustomerOrderMapper customerOrderMapper;
    @Autowired
    private TaskOrderMapper taskOrderMapper;
    @Autowired
    private TransferOrderMapper transferOrderMapper;
    @Autowired
    private SystemUserMapper systemUserMapper;
    @Autowired
    private InventoryMapper inventoryMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;

    // 中心总库房 ID（固定为 1，实际可配置化）
    private static final Long CENTRAL_WAREHOUSE_ID = 1L;

    // ==================== 查询类 ====================

    @Override
    public Result getAssignableOrders() {
        // 状态=2（可分配），且尚未生成任务单的订单
        List<Map<String, Object>> list = customerOrderMapper.selectAssignableOrdersWithCustomer();
        return Result.success(list);
    }

    @Override
    public Result getAutoDispatchableOrders() {
        // 状态=2（可分配），且 dispatch_admin_id 不为空（表示已有默认调度方案）
        List<Map<String, Object>> list = customerOrderMapper.selectAutoDispatchableOrders();
        return Result.success(list);
    }

    @Override
    public Result getOutOfStockOrders() {
        List<Map<String, Object>> list = customerOrderMapper.selectOutOfStockOrdersWithCustomer();
        return Result.success(list);
    }

    @Override
    public Result getStationAdmins() {
        // role=5 是分站管理员
        List<Map<String, Object>> list = systemUserMapper.selectStationAdmins();
        return Result.success(list);
    }

    // ==================== 手工调度（核心大事务） ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result manualDispatch(Long orderId, Long stationAdminId, Long dispatchAdminId) {
        // 1. 查出当前订单，做前置校验
        CustomerOrder order = customerOrderMapper.selectByPrimaryKey(orderId);
        if (order == null || order.getDelFlag() == 1) {
            return Result.error("订单不存在！");
        }
        if (order.getOrderStatus() != 2) {
            return Result.error("该订单当前状态不是【可分配】，无法调度！当前状态：" + order.getOrderStatus());
        }

        // 2. 查分站管理员所属的分站（分站ID就是分站管理员账号的 station_id 字段）
        SystemUser stationAdmin = systemUserMapper.selectByPrimaryKey(stationAdminId);
        if (stationAdmin == null) {
            return Result.error("所选分站管理员不存在！");
        }
        // 分站管理员的 ID 同时作为分站 ID（简化模型）
        Long stationId = stationAdminId;

        // 3. 生成任务单
        String taskNo = "RW" + IdUtil.getSnowflakeNextIdStr();
        BigDecimal settlement = order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO;
        taskOrderMapper.insertTaskOrder(taskNo, orderId, stationId, settlement);

        // 4. 生成调拨单（中心库 → 分站库）
        String transferNo = "DB" + IdUtil.getSnowflakeNextIdStr();
        transferOrderMapper.insertTransferOrder(transferNo, orderId, CENTRAL_WAREHOUSE_ID, stationId);

        // 5. 更新客户订单状态为 9（已调度待出库）
        customerOrderMapper.updateStatusToDispatched(orderId);

        return Result.success("调度成功！任务单：" + taskNo + " | 调拨单：" + transferNo);
    }

    // ==================== 自动批量调度 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result autoDispatch(Long dispatchAdminId) {
        List<Map<String, Object>> orders = customerOrderMapper.selectAutoDispatchableOrders();
        if (orders == null || orders.isEmpty()) {
            return Result.error("当前没有可自动调度的订单（需要状态=可分配 且 已有调度员记录）。");
        }

        int successCount = 0;
        StringBuilder sb = new StringBuilder();
        for (Map<String, Object> o : orders) {
            Long orderId = ((Number) o.get("id")).longValue();
            // 自动调度时分站 ID 取 dispatchAdminId（简化：可以做成从订单表读字段）
            Long stationAdminId = dispatchAdminId;
            Result r = manualDispatch(orderId, stationAdminId, dispatchAdminId);
            if (r.getCode() == 200) {
                successCount++;
                sb.append("订单#").append(orderId).append(" ✅ ");
            } else {
                sb.append("订单#").append(orderId).append(" ❌(").append(r.getMsg()).append(") ");
            }
        }
        return Result.success("自动调度完成，成功 " + successCount + "/" + orders.size() + " 单。\n" + sb);
    }

    // ==================== 缺货订单激活 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result activateOutOfStockOrder(Long orderId) {
        CustomerOrder order = customerOrderMapper.selectByPrimaryKey(orderId);
        if (order == null || order.getDelFlag() == 1) {
            return Result.error("订单不存在！");
        }
        if (order.getOrderStatus() != 1) {
            return Result.error("该订单并非【缺货】状态，无需激活！");
        }

        // 校验库存：查询该订单的所有明细，逐一检查库存
        List<Map<String, Object>> items = orderItemMapper.selectItemsByOrderId(orderId);
        if (items == null || items.isEmpty()) {
            return Result.error("未找到该订单的商品明细，无法校验库存！");
        }

        for (Map<String, Object> item : items) {
            Long productId = ((Number) item.get("product_id")).longValue();
            int needQty = ((Number) item.get("quantity")).intValue();
            int stockQty = inventoryMapper.selectTotalStockByProductId(productId);
            Object productName = item.get("product_name");
            if (stockQty < needQty) {
                return Result.error("商品【" + productName + "】库存不足（当前库存：" + stockQty
                        + "，订单需要：" + needQty + "），无法激活！");
            }
        }

        // 库存满足，将状态改为 2（可分配）
        CustomerOrder update = new CustomerOrder();
        update.setId(orderId);
        update.setOrderStatus((byte) 2);
        customerOrderMapper.updateByPrimaryKeySelective(update);

        return Result.success("已确认到货！订单状态已更新为【可分配】，请前往手工调度页面进行调度。");
    }

    // ==================== 任务单综合查询 ====================

    @Override
    public Result searchTaskOrders(String taskNo, Byte taskStatus, Long stationId,
            String customerName, String mobile,
            String requireDateStart, String requireDateEnd) {
        List<Map<String, Object>> list = taskOrderMapper.searchTaskOrders(
                taskNo, taskStatus, stationId, customerName, mobile, requireDateStart, requireDateEnd);
        return Result.success(list);
    }
}