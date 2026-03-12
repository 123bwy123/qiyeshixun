package com.itheima.qiyeshixun.service;

import com.itheima.qiyeshixun.common.Result;
import java.util.List;
import java.util.Map;

public interface InventoryReportService {
    /**
     * 综合库存查询：支持库房过滤、分类过滤、商品搜索
     */
    Result<List<Map<String, Object>>> getInventoryReport(Long warehouseId, Long categoryId, String productName);

    /**
     * 进出库流水报表：支持库房过滤、类型过滤（入库/出库）、时间范围
     */
    Result<List<Map<String, Object>>> getStockFlowReport(Integer pageNum, Integer pageSize, Long warehouseId, Byte flowType, String relatedNo);
}
