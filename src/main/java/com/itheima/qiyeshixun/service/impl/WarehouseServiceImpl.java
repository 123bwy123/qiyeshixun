package com.itheima.qiyeshixun.service.impl;

import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.mapper.InventoryMapper;
import com.itheima.qiyeshixun.mapper.WarehouseMapper;
import com.itheima.qiyeshixun.po.InventoryExample;
import com.itheima.qiyeshixun.po.Warehouse;
import com.itheima.qiyeshixun.po.WarehouseExample;
import com.itheima.qiyeshixun.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class WarehouseServiceImpl implements WarehouseService {

    @Autowired
    private WarehouseMapper warehouseMapper;

    @Autowired
    private InventoryMapper inventoryMapper;

    @Override
    public Result<List<Warehouse>> getWarehouseList() {
        WarehouseExample example = new WarehouseExample();
        example.createCriteria().andDelFlagEqualTo((byte) 0);
        return Result.success(warehouseMapper.selectByExample(example));
    }

    @Override
    public Result<String> saveWarehouse(Warehouse warehouse) {
        // 约束：中心库房只能有一个
        if (warehouse.getType() != null && warehouse.getType() == 1) {
             if (countCenterWarehouse() > 0) return Result.error("系统中已存在中心库房，无法重复创建！");
        }
        warehouse.setCreateTime(new Date());
        warehouse.setUpdateTime(new Date());
        warehouse.setDelFlag((byte) 0);
        warehouseMapper.insertSelective(warehouse);
        return Result.success("新增成功");
    }

    @Override
    public Result<String> updateWarehouse(Warehouse warehouse) {
        warehouse.setUpdateTime(new Date());
        warehouseMapper.updateByPrimaryKeySelective(warehouse);
        return Result.success("修改成功");
    }

    @Override
    public Result<String> deleteWarehouse(Long id) {
        Warehouse warehouse = warehouseMapper.selectByPrimaryKey(id);
        if (warehouse == null) return Result.error("仓库不存在");

        // 核心约束 1：中心库房不可删除
        if (warehouse.getType() == 1) return Result.error("中心库房是系统核心节点，绝对不可删除！");

        // 核心约束 2：有库存不可删除
        InventoryExample example = new InventoryExample();
        example.createCriteria().andWarehouseIdEqualTo(id).andDelFlagEqualTo((byte) 0).andStockQuantityGreaterThan(0);
        if (inventoryMapper.countByExample(example) > 0) {
            return Result.error("该库房内部仍有商品储备，请先移库后再删除！");
        }

        warehouse.setDelFlag((byte) 1);
        warehouse.setUpdateTime(new Date());
        warehouseMapper.updateByPrimaryKeySelective(warehouse);
        return Result.success("删除成功");
    }

    @Override
    public Result<List<Map<String, Object>>> getCenterInventory() {
        return Result.success(inventoryMapper.selectCenterInventory());
    }

    @Override
    public Result<String> updateReservation(Long id, Integer warningLevel, Integer maxLevel) {
        int rows = inventoryMapper.updateReservation(id, warningLevel, maxLevel);
        return rows > 0 ? Result.success("储备预警设置已生效") : Result.error("设置失败");
    }

    private long countCenterWarehouse() {
        WarehouseExample ex = new WarehouseExample();
        ex.createCriteria().andDelFlagEqualTo((byte) 0).andTypeEqualTo((byte) 1);
        return warehouseMapper.countByExample(ex);
    }
}