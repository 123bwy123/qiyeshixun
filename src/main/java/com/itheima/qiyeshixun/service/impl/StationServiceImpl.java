package com.itheima.qiyeshixun.service.impl;

import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.mapper.CustomerOrderMapper;
import com.itheima.qiyeshixun.mapper.SystemUserMapper;
import com.itheima.qiyeshixun.mapper.TaskOrderMapper;
import com.itheima.qiyeshixun.po.CustomerOrder;
import com.itheima.qiyeshixun.service.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StationServiceImpl implements StationService {

    @Autowired
    private TaskOrderMapper taskOrderMapper;
    @Autowired
    private CustomerOrderMapper customerOrderMapper;
    @Autowired
    private SystemUserMapper systemUserMapper;

    // ==================== 1. 查询未分配任务单 ====================

    @Override
    public Result getPendingTasks(Long stationId, Byte taskType, String dateStart, String dateEnd) {
        List<Map<String, Object>> list = taskOrderMapper.selectStationPendingTasks(
                stationId, taskType, dateStart, dateEnd);
        return Result.success(list);
    }

    // ==================== 2. 任务分配（事务） ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result assignCourier(Long taskId, Long orderId, Long courierId, Long adminId) {
        // 更新任务单：绑定配送员，状态改为 1（已分配/派送中）
        taskOrderMapper.assignCourier(taskId, courierId, adminId);

        // 同步更新客户主订单状态为 3（配送中）
        customerOrderMapper.updateStatusToDelivering(orderId);

        return Result.success("任务已分配！包裹进入【派送中】状态。");
    }

    // ==================== 3. 查询已分配任务单（打印、回执） ====================

    @Override
    public Result getAssignedTasks(Long stationId) {
        List<Map<String, Object>> list = taskOrderMapper.selectStationAssignedTasks(stationId);
        return Result.success(list);
    }

    // ==================== 4. 获取打印数据 ====================

    @Override
    public Result getPrintData(Long taskId) {
        Map<String, Object> printData = taskOrderMapper.selectTaskPrintData(taskId);
        if (printData == null) {
            return Result.error("未找到任务单！");
        }
        List<Map<String, Object>> items = taskOrderMapper.selectTaskItems(taskId);
        printData.put("items", items);
        return Result.success(printData);
    }

    // ==================== 5. 获取配送员列表 ====================

    @Override
    public Result getCouriers() {
        List<Map<String, Object>> couriers = systemUserMapper.selectAllCouriers();
        return Result.success(couriers);
    }

    // ==================== 6. 回执录入结单（核心大事务） ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result submitReceipt(Long taskId, Long orderId, Byte completeStatus,
            BigDecimal actualAmount, String remark) {
        // completeStatus 语义：3=全部完成, 4=部分完成, 5=失败/拒收

        // 前置校验
        if (completeStatus == null || actualAmount == null) {
            return Result.error("请填写完成状态和实收金额！");
        }
        if (!List.of((byte) 3, (byte) 4, (byte) 5).contains(completeStatus)) {
            return Result.error("无效的完成状态！");
        }

        // 1. 更新任务单（状态、实收金额、备注、完成时间）
        taskOrderMapper.updateTaskReceipt(taskId, completeStatus, actualAmount, remark);

        // 2. 更新客户主订单：
        // 全部完成(3) → 已完成(7)
        // 部分完成(4) → 已完成(7)但有备注
        // 失败(5) → 已取消/失败(8)
        CustomerOrder co = new CustomerOrder();
        co.setId(orderId);
        if (completeStatus == 5) {
            co.setOrderStatus((byte) 8); // 失败
        } else {
            co.setOrderStatus((byte) 7); // 完成
            customerOrderMapper.updateStatusToFinished(orderId);
            return Result.success("结单成功！实收金额：¥" + actualAmount + "，订单已完成！");
        }
        customerOrderMapper.updateByPrimaryKeySelective(co);

        String msg = completeStatus == 5
                ? "已记录失败/拒收！订单状态更新为【失败】，请联系库房安排退货入库。"
                : "结单成功！实收金额：¥" + actualAmount;
        return Result.success(msg);
    }

    // ==================== 7. 缴款统计查询 ====================

    @Override
    public Result getPaymentStats(Long stationId, String dateStart, String dateEnd) {
        List<Map<String, Object>> stats = taskOrderMapper.selectPaymentStats(stationId, dateStart, dateEnd);

        // 计算汇总行
        BigDecimal totalReceived = BigDecimal.ZERO;
        long totalDelivered = 0;
        long totalFailed = 0;
        BigDecimal totalRefund = BigDecimal.ZERO;
        for (Map<String, Object> row : stats) {
            totalDelivered += toLong(row.get("delivered_count"));
            totalFailed += toLong(row.get("failed_count"));
            totalReceived = totalReceived.add(toBigDecimal(row.get("total_received")));
            totalRefund = totalRefund.add(toBigDecimal(row.get("total_refund")));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("details", stats);
        result.put("summary", Map.of(
                "totalDelivered", totalDelivered,
                "totalFailed", totalFailed,
                "totalReceived", totalReceived,
                "totalRefund", totalRefund));
        return Result.success(result);
    }

    // ——— 内部工具方法 ———
    private long toLong(Object obj) {
        return obj == null ? 0L : ((Number) obj).longValue();
    }

    private BigDecimal toBigDecimal(Object obj) {
        if (obj == null)
            return BigDecimal.ZERO;
        if (obj instanceof BigDecimal)
            return (BigDecimal) obj;
        return new BigDecimal(obj.toString());
    }
}