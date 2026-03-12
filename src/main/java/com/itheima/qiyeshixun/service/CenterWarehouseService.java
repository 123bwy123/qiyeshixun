package com.itheima.qiyeshixun.service;

import com.itheima.qiyeshixun.common.Result;

import java.util.List;
import java.util.Map;

public interface CenterWarehouseService {

    // ================= 购货入库 =================
    Result<List<Map<String, Object>>> getPurchaseOrders(Byte status);

    Result<List<Map<String, Object>>> getPurchaseItems(Long purchaseId);

    // 提交入库 (带事务：更新单据、增加库存、记录流水)
    Result<String> submitPurchaseInbound(Long purchaseId, List<Map<String, Object>> items, Long adminId, String remark);

    // ================= 调拨出库 =================
    Result<List<Map<String, Object>>> getPendingTransfers(String transferNo, String orderNo, String dateStart,
            String dateEnd);

    Result<List<Map<String, Object>>> getTransferItems(Long transferId);

    // 执行出库 (带事务：校验并扣减库存、更新调拨/客户/任务三单状态、生成验货单、记录流水)
    Result<String> executeOutbound(Long transferId, Long adminId, Long stationId);

    // ================= 记录与打印 =================
    Result<List<Map<String, Object>>> getOutboundPrintData(String dateVal, String productName);

    Result<List<Map<String, Object>>> getInspectionPrintData(Long stationId, String dateVal, String productName);

    Result<List<Map<String, Object>>> getAllStations();
}
