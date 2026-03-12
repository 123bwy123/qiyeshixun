package com.itheima.qiyeshixun.controller;

import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.service.CourierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/courier")
@CrossOrigin
public class CourierController {

    @Autowired
    private CourierService courierService;

    @GetMapping("/myTasks")
    public Result myTasks(@RequestParam Long courierId) {
        return courierService.getMyTasks(courierId);
    }

    @PostMapping("/confirm")
    public Result confirm(@RequestParam Long taskId) {
        return courierService.confirmDelivery(taskId);
    }
}