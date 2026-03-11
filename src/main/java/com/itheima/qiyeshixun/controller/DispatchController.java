package com.itheima.qiyeshixun.controller;

import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.mapper.CustomerOrderMapper;
import com.itheima.qiyeshixun.service.DispatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/dispatch")
@CrossOrigin
public class DispatchController {

    @Autowired
    private DispatchService dispatchService;

    @Autowired
    private CustomerOrderMapper customerOrderMapper;

    // 1. 获取待调度列表 (状态为 1 的订单)
    @GetMapping("/list")
    public Result getList() {
        // 直接调 Mapper 查出状态为 1 的订单
        return Result.success(customerOrderMapper.selectAssignableOrders());
    }

    // 2. 执行调度分发
    @PostMapping("/execute")
    public Result execute(@RequestParam Long orderId, @RequestParam Long stationId) {
        return dispatchService.executeDispatch(orderId, stationId);
    }
}