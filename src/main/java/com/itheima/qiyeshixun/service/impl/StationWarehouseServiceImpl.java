package com.itheima.qiyeshixun.service.impl;

import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.mapper.*;
import com.itheima.qiyeshixun.po.SystemUser;
import com.itheima.qiyeshixun.service.StationWarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class StationWarehouseServiceImpl implements StationWarehouseService {

    @Autowired
    private InspectionOrderMapper inspectionOrderMapper;

    @Autowired
    private InventoryMapper inventoryMapper;

    @Autowired
    private WarehouseFlowMapper warehouseFlowMapper;

    @Autowired
    private CustomerOrderMapper customerOrderMapper;

    @Autowired
    private TaskOrderMapper taskOrderMapper;

    @Autowired
    private SystemUserMapper systemUserMapper;

    /**
     * 解析前端传来的操作人ID，如果他是分站库管(角色=8)，则返回他挂靠的分站ID (dept_id)。
     * 否则，假设传来的已经是正确的分站ID。
     */
    private Long resolveStationId(Long inputId) {
        if (inputId == null) return null;
        SystemUser user = systemUserMapper.selectByPrimaryKey(inputId);
        if (user != null && user.getRole() != null && user.getRole() == 8) {
            // 是库管员，取他绑定的分站ID，如果没绑则返回兜底值5L预防崩溃。
            return user.getDeptId() != null ? user.getDeptId() : 5L;
        }
        return inputId;
    }

    @Override
    public Result<List<Map<String, Object>>> getPendingInspections(Long stationId) {
        stationId = resolveStationId(stationId);
        if (stationId == null) return Result.error("未获取到当前分站ID");
        return Result.success(inspectionOrderMapper.selectPendingInspections(stationId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> confirmInbound(Long inspectionId, Long stationId, Integer actualQty, String remark) {
        stationId = resolveStationId(stationId);
        Map<String, Object> detail = inspectionOrderMapper.selectInspectionDetail(inspectionId);
        if (detail == null) {
            return Result.error("未找到有效的验货单明细！");
        }

        Long productId = Long.valueOf(detail.get("productId").toString());
        Long orderId = Long.valueOf(detail.get("orderId").toString());
        Integer expectedQty = Integer.valueOf(detail.get("expectedQty").toString());
        
        int inQty = (actualQty != null) ? actualQty : expectedQty;

        // 1. 增加库存，如果影响0行说明该分站该商品无库存记录，进行 insert
        int updated = inventoryMapper.addStock(productId, stationId, inQty);
        if (updated == 0) {
            inventoryMapper.insertStock(productId, stationId, inQty);
        }

        // 2. 修改主订单状态为 4 (分站已到货)
        customerOrderMapper.updateStatusToStationArrived(orderId);

        // 3. 修改验货单状态为 1 (已验货)
        inspectionOrderMapper.updateInspectionStatus(inspectionId, (byte) 1);

        // 4. 记录库房流水 (业务上记录发件人为操作人)
        Long adminId = 1L; // TODO: 后续可从前端传的操作人ID获取
        String inspectionNo = "INSP-" + inspectionId;
        warehouseFlowMapper.insertFlow(stationId, productId, (byte) 1, inQty, adminId, inspectionNo, remark);

        return Result.success("入库成功！");
    }

    @Override
    public Result<List<Map<String, Object>>> getPendingPickups(Long stationId, String taskNo) {
        stationId = resolveStationId(stationId);
        if (stationId == null) return Result.error("未获取到当前分站ID");
        return Result.success(taskOrderMapper.selectTasksForPickup(stationId, taskNo));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> confirmPickup(Long taskId, Long stationId, String courierName, String pickupDate) {
        stationId = resolveStationId(stationId);
        Long orderId = taskOrderMapper.getOrderIdByTaskId(taskId);
        if (orderId == null) {
            return Result.error("任务单对应的订单不存在！");
        }

        List<Map<String, Object>> items = taskOrderMapper.selectTaskProductItems(taskId);
        if (items.isEmpty()) {
            return Result.error("任务单无商品明细！");
        }

        for (Map<String, Object> item : items) {
            Long productId = Long.valueOf(item.get("productId").toString());
            Integer quantity = Integer.valueOf(item.get("quantity").toString());
            String productName = (String) item.get("productName");

            // 1. 独立扣减分站库房库存
            int deducted = inventoryMapper.deductStationWarehouseStock(productId, stationId, quantity);
            if (deducted == 0) {
                // 回滚并阻断
                throw new RuntimeException("领货失败：[" + productName + "] 在当前分站库房储量不足！(需要 " + quantity + " 件)");
            }

            // 2. 记录出库流水
            Long adminId = 1L; // TODO: 后续可获取真实操作人ID
            String taskNoStr = "TASK-" + taskId; 
            warehouseFlowMapper.insertFlow(stationId, productId, (byte) 2, quantity, adminId, taskNoStr, "配送员领货: " + courierName + " 日期: " + pickupDate);
        }

        // 3. 将主订单状态修改为 6 (已领货)
        customerOrderMapper.updateStatusToCourierPicked(orderId);

        return Result.success("发货完成，商品已由配送员领走！");
    }
}
