package com.itheima.qiyeshixun.service;

import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.po.Warehouse;
import java.util.List;
import java.util.Map;

public interface WarehouseService {
    // 仓库设置
    Result<List<Warehouse>> getWarehouseList();
    Result<String> saveWarehouse(Warehouse warehouse);
    Result<String> updateWarehouse(Warehouse warehouse);
    Result<String> deleteWarehouse(Long id);

    // 储备设置
    Result<List<Map<String, Object>>> getCenterInventory();
    Result<String> updateReservation(Long id, Integer warningLevel, Integer maxLevel);
}