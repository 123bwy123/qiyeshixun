package com.itheima.qiyeshixun.controller;

import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.mapper.CustomerOrderMapper;
import com.itheima.qiyeshixun.service.StationWarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/stationWarehouse")
@CrossOrigin
public class StationWarehouseController {

    @Autowired
    private StationWarehouseService stationWarehouseService;

    // 1. 查待入库 (分站库房管理员 - 验货单)
    @GetMapping("/pending-inspections")
    public Result getPendingInspections(@RequestParam Long stationId) {
        return stationWarehouseService.getPendingInspections(stationId);
    }

    // 2. 确认调拨入库 (修改库存、订单状态、记录流水)
    @PostMapping("/inbound")
    public Result confirmInbound(@RequestBody Map<String, Object> body) {
        if (body.get("inspectionId") == null || body.get("stationId") == null) {
            return Result.error("缺少必要参数！");
        }
        Long inspectionId = Long.valueOf(body.get("inspectionId").toString());
        Long stationId = Long.valueOf(body.get("stationId").toString());
        Integer actualQty = body.get("actualQty") != null ? Integer.valueOf(body.get("actualQty").toString()) : null;
        String remark = (String) body.get("remark");

        try {
            return stationWarehouseService.confirmInbound(inspectionId, stationId, actualQty, remark);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("入库失败: " + e.getMessage());
        }
    }

    // 3. 查待配送员领货的任务单
    @GetMapping("/pending-pickups")
    public Result getPendingPickups(
            @RequestParam Long stationId,
            @RequestParam(required = false) String taskNo) {
        return stationWarehouseService.getPendingPickups(stationId, taskNo);
    }

    // 4. 确认发货给配送员 (扣减库存、更新任务状态、主订单状态)
    @PostMapping("/pickup")
    public Result confirmPickup(@RequestBody Map<String, Object> body) {
        if (body.get("taskId") == null || body.get("stationId") == null) {
            return Result.error("缺少必要参数！");
        }
        Long taskId = Long.valueOf(body.get("taskId").toString());
        Long stationId = Long.valueOf(body.get("stationId").toString());
        String courierName = (String) body.get("courierName");
        String pickupDate = (String) body.get("pickupDate");

        try {
            return stationWarehouseService.confirmPickup(taskId, stationId, courierName, pickupDate);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("发货失败: " + e.getMessage());
        }
    }
}