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
}