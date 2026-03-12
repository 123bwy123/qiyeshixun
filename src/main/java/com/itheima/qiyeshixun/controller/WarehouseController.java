package com.itheima.qiyeshixun.controller;

import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.po.Warehouse;
import com.itheima.qiyeshixun.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/dc/warehouse")
@CrossOrigin
public class WarehouseController {

    @Autowired
    private WarehouseService warehouseService;

    // --- 库房设置 ---
    @GetMapping("/list")
    public Result<List<Warehouse>> list() {
        return warehouseService.getWarehouseList();
    }

    @PostMapping("/save")
    public Result<String> save(@RequestBody Warehouse warehouse) {
        return warehouseService.saveWarehouse(warehouse);
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody Warehouse warehouse) {
        return warehouseService.updateWarehouse(warehouse);
    }

    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id) {
        return warehouseService.deleteWarehouse(id);
    }

    // --- 储备设置 (仅针对中心库房) ---
    @GetMapping("/reserve/list")
    public Result<List<Map<String, Object>>> reserveList() {
        return warehouseService.getCenterInventory();
    }

    @PostMapping("/reserve/update")
    public Result<String> updateReserve(@RequestBody Map<String, Object> params) {
        Long id = Long.valueOf(params.get("id").toString());
        Integer warningLevel = (Integer) params.get("warningLevel");
        Integer maxLevel = (Integer) params.get("maxLevel");
        return warehouseService.updateReservation(id, warningLevel, maxLevel);
    }
}