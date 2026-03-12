package com.itheima.qiyeshixun.controller;

import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.service.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/station")
@CrossOrigin
public class StationController {

    @Autowired
    private StationService stationService;

    @GetMapping("/pendingTasks")
    public Result pendingTasks() {
        return stationService.getPendingTasks();
    }

    @PostMapping("/assign")
    public Result assign(@RequestParam Long taskId, @RequestParam Long orderId, @RequestParam Long courierId, @RequestParam Long adminId) {
        return stationService.assignCourier(taskId, orderId, courierId, adminId);
    }
    // 顶部别忘了注入 SystemUserMapper
    @Autowired
    private com.itheima.qiyeshixun.mapper.SystemUserMapper systemUserMapper;

    // 获取真实快递小哥列表
    @GetMapping("/courierList")
    public Result getCourierList() {
        return Result.success(systemUserMapper.selectAllCouriers());
    }

    // 查询待结单列表
    @GetMapping("/pendingClose")
    public Result pendingClose() {
        return stationService.getPendingCloseTasks();
    }

    // 确认结单收钱
    @PostMapping("/close")
    public Result closeOrder(@RequestParam Long taskId, @RequestParam Long orderId) {
        return stationService.closeOrder(taskId, orderId);
    }
}