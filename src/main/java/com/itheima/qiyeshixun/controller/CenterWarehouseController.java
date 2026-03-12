package com.itheima.qiyeshixun.controller;

import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.mapper.CenterWarehouseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/centerWarehouse")
@CrossOrigin
public class CenterWarehouseController {

    @Autowired
    private CenterWarehouseMapper centerWarehouseMapper;

    // 获取月台待接车的订单
    @GetMapping("/pending")
    public Result getPendingReceipts() {
        return Result.success(centerWarehouseMapper.selectPendingReceipts());
    }

    // 🛡️ 核心入库接口（加了事务，同生共死，绝不缺胳膊少腿）
    @PostMapping("/receive")
    @Transactional(rollbackFor = Exception.class)
    public Result receiveOrder(@RequestParam Long orderId) {

        // 1. 先查出货车里到底装了啥
        List<Map<String, Object>> items = centerWarehouseMapper.selectItemsByOrderId(orderId);
        if (items == null || items.isEmpty()) {
            return Result.error("严重拦截：该订单缺乏明细数据，拒绝入库，防止数据断层！");
        }

        // 2. 遍历货品，挨个给中心大仓加真实库存！(洗衣机从 5 变成 55！)
        for (Map<String, Object> item : items) {
            Long productId = Long.valueOf(item.get("productId").toString());
            Integer quantity = Integer.valueOf(item.get("quantity").toString());
            centerWarehouseMapper.addInventoryStock(productId, quantity);
        }

        // 3. 更新明细表，记录实收数量
        centerWarehouseMapper.updateItemActualQuantity(orderId);

        // 4. 完结主订单，状态改 3
        centerWarehouseMapper.updateOrderStatusToReceived(orderId);

        return Result.success("🚛 验货完美通过！库存数据已全线暴涨，缺货警报正式解除！");
    }
}