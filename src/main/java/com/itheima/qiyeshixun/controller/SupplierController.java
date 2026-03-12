package com.itheima.qiyeshixun.controller;

import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.mapper.SupplierMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/supplier")
@CrossOrigin // 解决你刚才报的 CORS 跨域问题
public class SupplierController {

    @Autowired
    private SupplierMapper supplierMapper;

    // 拉取待发货的采购单大屏数据
    @GetMapping("/pending")
    public Result getPending() {
        return Result.success(supplierMapper.selectPendingDispatchOrders());
    }

    // 确认发货（状态变为2）
    @PostMapping("/dispatch")
    public Result dispatch(@RequestParam Long orderId) {
        supplierMapper.dispatchOrder(orderId);
        return Result.success("发货成功！货车已在途，预计明日抵达中心库房。");
    }
}