package com.itheima.qiyeshixun.controller;

import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.service.InventoryReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/dc/report")
@CrossOrigin
public class InventoryReportController {

    @Autowired
    private InventoryReportService inventoryReportService;

    @GetMapping("/inventory")
    public Result<List<Map<String, Object>>> getInventoryReport(
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String productName) {
        return inventoryReportService.getInventoryReport(warehouseId, categoryId, productName);
    }

    @GetMapping("/flow")
    public Result<List<Map<String, Object>>> getStockFlowReport(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) Byte flowType,
            @RequestParam(required = false) String relatedNo) {
        return inventoryReportService.getStockFlowReport(pageNum, pageSize, warehouseId, flowType, relatedNo);
    }
}
