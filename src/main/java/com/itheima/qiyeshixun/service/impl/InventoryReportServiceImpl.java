package com.itheima.qiyeshixun.service.impl;

import com.github.pagehelper.PageHelper;
import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.mapper.InventoryMapper;
import com.itheima.qiyeshixun.mapper.WarehouseFlowMapper;
import com.itheima.qiyeshixun.service.InventoryReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class InventoryReportServiceImpl implements InventoryReportService {

    @Autowired
    private InventoryMapper inventoryMapper;

    @Autowired
    private WarehouseFlowMapper warehouseFlowMapper;

    @Override
    public Result<List<Map<String, Object>>> getInventoryReport(Long warehouseId, Long categoryId, String productName) {
        return Result.success(inventoryMapper.selectInventoryReport(warehouseId, categoryId, productName));
    }

    @Override
    public Result<List<Map<String, Object>>> getStockFlowReport(Integer pageNum, Integer pageSize, Long warehouseId, Byte flowType, String relatedNo) {
        PageHelper.startPage(pageNum, pageSize);
        return Result.success(warehouseFlowMapper.selectStockFlowReport(warehouseId, flowType, relatedNo));
    }
}
