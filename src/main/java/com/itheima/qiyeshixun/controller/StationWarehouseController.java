package com.itheima.qiyeshixun.controller;

import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.mapper.CustomerOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/stationWarehouse")
@CrossOrigin
public class StationWarehouseController {

    @Autowired
    private CustomerOrderMapper customerOrderMapper;

    // 查待入库
    @GetMapping("/incoming")
    public Result incoming() { return Result.success(customerOrderMapper.selectIncomingToStation()); }

    // 确认入库 (3->4)
    @PostMapping("/arrive")
    public Result arrive(@RequestParam Long orderId) {
        customerOrderMapper.updateStatusToStationArrived(orderId);
        return Result.success("验货完成，分站入库成功！");
    }

    // 查待交接给小哥
    @GetMapping("/handover")
    public Result handover() { return Result.success(customerOrderMapper.selectPendingHandoverToCourier()); }

    // 确认交接给小哥 (5->6)
    @PostMapping("/pickup")
    public Result pickup(@RequestParam Long orderId) {
        customerOrderMapper.updateStatusToCourierPicked(orderId);
        return Result.success("交接完成，包裹已移交快递员！");
    }
}