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

        // 【Bug1 修复】从前端接收真实采购数量，严禁硬编码兜底
        if (params.get("quantity") == null) {
            return Result.error("采购失败：采购数量不能为空！");
        }
        int quantity = Integer.parseInt(params.get("quantity").toString());
        if (quantity <= 0) {
            return Result.error("采购失败：采购数量必须大于 0！");
        }
        Double totalAmount = unitPrice * quantity;

        // 1. 找到该商品对应的供应商
        Long supplierId = purchaseOrderMapper.getSupplierIdByProduct(productId);
        if (supplierId == null) {
            return Result.error("采购失败：该商品未关联有效供应商！");
        }

        // 2. 生成采购单号 (CG + 时间戳)
        String purchaseNo = "CG" + System.currentTimeMillis();

        // 3. 执行插入主表 (初始 status=1，代表「待供应商发货」)
        PurchaseOrderEntity order = new PurchaseOrderEntity();
        order.purchaseNo = purchaseNo;
        order.supplierId = supplierId;
        order.deliveryAdminId = adminId;
        order.totalAmount = totalAmount;

        purchaseOrderMapper.insertPurchaseOrder(order);

        // 4. 插入采购明细，使用前端传入的真实数量
        // 注意：useGeneratedKeys=true，order.id 已获得数据库自增 ID
        purchaseOrderMapper.insertPurchaseItem(order.id, productId, quantity, unitPrice, totalAmount);

        return Result.success("采购单 [" + purchaseNo + "] 已下达，共采购 " + quantity + " 件，待供应商发货！");
    }
}