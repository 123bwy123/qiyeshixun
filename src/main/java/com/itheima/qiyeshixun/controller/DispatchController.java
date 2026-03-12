package com.itheima.qiyeshixun.controller;

import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.service.DispatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/dispatch")
@CrossOrigin
public class DispatchController {

    @Autowired
    private DispatchService dispatchService;

    /** 获取【可分配】订单列表（手工调度用） */
    @GetMapping("/assignable")
    public Result getAssignable() {
        return dispatchService.getAssignableOrders();
    }

    /** 获取【可分配 且已有分站】订单列表（自动调度用） */
    @GetMapping("/auto-ready")
    public Result getAutoReady() {
        return dispatchService.getAutoDispatchableOrders();
    }

    /** 获取【缺货】订单列表 */
    @GetMapping("/out-of-stock")
    public Result getOutOfStock() {
        return dispatchService.getOutOfStockOrders();
    }

    /** 获取分站管理员列表（供调度员选择分站） */
    @GetMapping("/station-admins")
    public Result getStationAdmins() {
        return dispatchService.getStationAdmins();
    }

    /** 手工调度单个订单 */
    @PostMapping("/manual")
    public Result manualDispatch(@RequestBody Map<String, Object> body) {
        Long orderId = Long.valueOf(body.get("orderId").toString());
        Long stationAdminId = Long.valueOf(body.get("stationAdminId").toString());
        Long dispatchAdminId = body.get("dispatchAdminId") != null
                ? Long.valueOf(body.get("dispatchAdminId").toString())
                : 0L;
        return dispatchService.manualDispatch(orderId, stationAdminId, dispatchAdminId);
    }

    /** 一键自动批量调度 */
    @PostMapping("/auto")
    public Result autoDispatch(@RequestParam(required = false, defaultValue = "0") Long dispatchAdminId) {
        return dispatchService.autoDispatch(dispatchAdminId);
    }

    /** 缺货订单到货激活 */
    @PostMapping("/activate")
    public Result activateOutOfStock(@RequestParam Long orderId) {
        return dispatchService.activateOutOfStockOrder(orderId);
    }

    /** 任务单综合查询 */
    @GetMapping("/task-orders")
    public Result searchTaskOrders(
            @RequestParam(required = false) String taskNo,
            @RequestParam(required = false) Byte taskStatus,
            @RequestParam(required = false) Long stationId,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String mobile,
            @RequestParam(required = false) String requireDateStart,
            @RequestParam(required = false) String requireDateEnd) {
        return dispatchService.searchTaskOrders(taskNo, taskStatus, stationId,
                customerName, mobile, requireDateStart, requireDateEnd);
    }

    // 兼容旧接口（之前已有代码中可能调用了旧的 /list 和 /execute）
    @GetMapping("/list")
    public Result list() {
        return dispatchService.getAssignableOrders();
    }
}