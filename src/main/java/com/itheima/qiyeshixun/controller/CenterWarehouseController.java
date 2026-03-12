package com.itheima.qiyeshixun.controller;

import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.service.CenterWarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/center-warehouse")
@CrossOrigin
public class CenterWarehouseController {

    @Autowired
    private CenterWarehouseService centerWarehouseService;

    // 1. 查询所有待入库/已入库的采购单
    @GetMapping("/purchase-orders")
    public Result getPurchaseOrders(@RequestParam(required = false) Byte status) {
        return centerWarehouseService.getPurchaseOrders(status);
    }

    // 2. 查询单个采购单的明细（确认收货时展示）
    @GetMapping("/purchase-items")
    public Result getPurchaseItems(@RequestParam Long purchaseId) {
        return centerWarehouseService.getPurchaseItems(purchaseId);
    }

    // 3. 提交入库
    @PostMapping("/purchase-inbound")
    public Result submitPurchaseInbound(@RequestBody Map<String, Object> body) {
        Long purchaseId = Long.valueOf(body.get("purchaseId").toString());
        Long adminId = body.get("adminId") != null ? Long.valueOf(body.get("adminId").toString()) : 1L;
        String remark = body.get("remark") != null ? body.get("remark").toString() : "";
        List<Map<String, Object>> items = (List<Map<String, Object>>) body.get("items");

        return centerWarehouseService.submitPurchaseInbound(purchaseId, items, adminId, remark);
    }

    // 4. 查询待出库调拨单
    @GetMapping("/pending-transfers")
    public Result getPendingTransfers(
            @RequestParam(required = false) String transferNo,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String dateStart,
            @RequestParam(required = false) String dateEnd) {
        return centerWarehouseService.getPendingTransfers(transferNo, orderNo, dateStart, dateEnd);
    }

    // 5. 查调拨单里的商品详情
    @GetMapping("/transfer-items")
    public Result getTransferItems(@RequestParam Long transferId) {
        return centerWarehouseService.getTransferItems(transferId);
    }

    // 6. 执行出库
    @PostMapping("/execute-outbound")
    public Result executeOutbound(@RequestBody Map<String, Object> body) {
        if (body.get("transferId") == null || body.get("stationId") == null) {
            return Result.error("出库失败：调拨单ID或分站ID缺失！");
        }
        Long transferId = Long.valueOf(body.get("transferId").toString());
        Long adminId = body.get("adminId") != null ? Long.valueOf(body.get("adminId").toString()) : 1L;
        Long stationId = Long.valueOf(body.get("stationId").toString());

        try {
            return centerWarehouseService.executeOutbound(transferId, adminId, stationId);
        } catch (Exception e) {
            e.printStackTrace();
            String errDetail = e.getMessage();
            if (e.getCause() != null) {
                errDetail += " | Cause: " + e.getCause().getMessage();
            }
            return Result.error("执行出库失败: " + errDetail);
        }
    }

    // 7. 查询当天出库单统计 (打印用)
    @GetMapping("/outbound-print-data")
    public Result getOutboundPrintData(
            @RequestParam(required = false) String dateVal,
            @RequestParam(required = false) String productName) {
        return centerWarehouseService.getOutboundPrintData(dateVal, productName);
    }

    // 8. 查询发往某分站的验货单统计 (打印用)
    @GetMapping("/inspection-print-data")
    public Result getInspectionPrintData(
            @RequestParam(required = false) Long stationId,
            @RequestParam(required = false) String dateVal,
            @RequestParam(required = false) String productName) {
        return centerWarehouseService.getInspectionPrintData(stationId, dateVal, productName);
    }

    // 9. 获取所有分站列表 (下拉筛选用)
    @GetMapping("/all-stations")
    public Result getAllStations() {
        return centerWarehouseService.getAllStations();
    }
}