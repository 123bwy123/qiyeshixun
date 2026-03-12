package com.itheima.qiyeshixun.service;

import com.itheima.qiyeshixun.common.Result;
import java.util.List;
import java.util.Map;

public interface StationWarehouseService {

    // 获取待入库验货单（含商品明细）
    Result<List<Map<String, Object>>> getPendingInspections(Long stationId);

    // 确认调拨入库 (修改库存、订单状态、记录流水)
    Result<String> confirmInbound(Long inspectionId, Long stationId, Integer actualQty, String remark);

    // 获取待配送员领货的任务单
    Result<List<Map<String, Object>>> getPendingPickups(Long stationId, String taskNo);

    // 确认配送员领货 (扣减库存、更新任务状态、主订单状态)
    Result<String> confirmPickup(Long taskId, Long stationId, String courierName, String pickupDate);
}
