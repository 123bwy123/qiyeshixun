package com.itheima.qiyeshixun.controller;

import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.dto.CustomerOrderSubmitDTO;
import com.itheima.qiyeshixun.service.CustomerOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/customer/order")
@CrossOrigin // 允许跨域
public class CustomerOrderController {

    @Autowired
    private CustomerOrderService customerOrderService;

    /**
     * 客户自主下单接口
     */
    @PostMapping("/submit")
    public Result submit(@RequestBody CustomerOrderSubmitDTO dto) {
        if (dto.getCustomerId() == null || dto.getReceiveAddress() == null) {
            return Result.error("客户ID和收货地址不能为空！");
        }
        return customerOrderService.submitOrder(dto);
    }
    // 1. 获取待审核列表
    @GetMapping("/pendingList")
    public Result getPendingList() {
        return customerOrderService.getPendingOrders();
    }

    // 2. 客服审核通过接口
    // 真实开发中 operatorId 应该从 Token 里取，这里咱们前端先传过来模拟
    @PostMapping("/approve")
    public Result approve(@RequestParam Long orderId, @RequestParam Long operatorId, @RequestParam BigDecimal totalAmount) {
        return customerOrderService.approveOrder(orderId, operatorId, totalAmount);
    }
}