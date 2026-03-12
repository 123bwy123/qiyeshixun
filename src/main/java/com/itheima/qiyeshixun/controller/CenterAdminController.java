package com.itheima.qiyeshixun.controller;

import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.dto.PurchaseOrderEntity;
import com.itheima.qiyeshixun.mapper.InventoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/center")
@CrossOrigin
public class CenterAdminController {

    @Autowired
    private InventoryMapper inventoryMapper;

    // 获取配送中心的库存监控大屏数据
    @GetMapping("/inventoryAlerts")
    public Result getInventoryAlerts() {
        return Result.success(inventoryMapper.selectInventoryWithWarning());
    }

    @Autowired
    private com.itheima.qiyeshixun.mapper.PurchaseOrderMapper purchaseOrderMapper;

    @PostMapping("/createPurchase")
    @Transactional
    public Result createPurchase(@RequestBody Map<String, Object> params) {
        Long productId = Long.valueOf(params.get("productId").toString());
        Double unitPrice = Double.valueOf(params.get("unitPrice").toString());
        Long adminId = Long.valueOf(params.get("adminId").toString());

        // 1. 找到该商品对应的供应商
        Long supplierId = purchaseOrderMapper.getSupplierIdByProduct(productId);

        // 2. 模拟采购数量（比如一次性补货 50 件）
        int quantity = 50;
        Double totalAmount = unitPrice * quantity;

        // 3. 生成采购单号 (CG + 时间戳)
        String purchaseNo = "CG" + System.currentTimeMillis();

        // 4. 执行插入
        PurchaseOrderEntity order = new PurchaseOrderEntity();
        order.purchaseNo = purchaseNo;
        order.supplierId = supplierId; // 注意这里换成驼峰命名 supplierId，和你新建的类保持一致
        order.deliveryAdminId = adminId;
        order.totalAmount = totalAmount;

        purchaseOrderMapper.insertPurchaseOrder(order);

        // 【核心修复】：主订单生成后，立刻把50台洗衣机写进明细表！死死绑定！
        // 注意：因为上面配置了 useGeneratedKeys = true，所以此时 order.id 已经拿到了数据库自动生成的 ID
        purchaseOrderMapper.insertPurchaseItem(order.id, productId, quantity, unitPrice, totalAmount);

        return Result.success("采购单 [" + purchaseNo + "] 已下达到供应商，待对方发货！");
    }
}