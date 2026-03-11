package com.itheima.qiyeshixun.controller;

import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/warehouse")
@CrossOrigin
public class WarehouseController {

    @Autowired
    private WarehouseService warehouseService;

    @GetMapping("/pendingOutbound")
    public Result pendingOutbound() {
        return warehouseService.getPendingOutboundList();
    }

    @PostMapping("/outbound")
    public Result outbound(@RequestParam Long transferId, @RequestParam Long adminId) {
        return warehouseService.executeOutbound(transferId, adminId);
    }
}