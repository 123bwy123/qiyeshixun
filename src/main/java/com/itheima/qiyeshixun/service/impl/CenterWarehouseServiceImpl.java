package com.itheima.qiyeshixun.service.impl;

import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.mapper.*;
import com.itheima.qiyeshixun.service.CenterWarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class CenterWarehouseServiceImpl implements CenterWarehouseService {

    @Autowired
    private PurchaseOrderMapper purchaseOrderMapper;

    @Autowired
    private TransferOrderMapper transferOrderMapper;

    @Autowired
    private CustomerOrderMapper customerOrderMapper;

    @Autowired
    private TaskOrderMapper taskOrderMapper;

    @Autowired
    private InventoryMapper inventoryMapper;

    @Autowired
    private WarehouseFlowMapper warehouseFlowMapper;

    @Autowired
    private InspectionOrderMapper inspectionOrderMapper;

    @Override
    public Result<List<Map<String, Object>>> getPurchaseOrders(Byte status) {
        return Result.success(purchaseOrderMapper.selectPurchaseOrders(status));
    }

    @Override
    public Result<List<Map<String, Object>>> getPurchaseItems(Long purchaseId) {
        return Result.success(purchaseOrderMapper.selectPurchaseItems(purchaseId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> submitPurchaseInbound(Long purchaseId, List<Map<String, Object>> items, Long adminId,
            String remark) {
        // 1. 遍历前台传来的商品明细，更新实际到货量并增加库存
        for (Map<String, Object> item : items) {
            Long itemId = Long.valueOf(item.get("id").toString());
            Long productId = Long.valueOf(item.get("product_id").toString());
            Integer actualQty = Integer.valueOf(item.get("actual_quantity").toString());

            // A. 更新采购单明细
            purchaseOrderMapper.updatePurchaseItemActualQty(itemId, actualQty);

            // B. 增加库存 (默认存到中心库房 warehouse_id = 1)
            Long centerInventoryId = inventoryMapper.selectCenterInventoryId(productId);
            if (centerInventoryId != null) {
                inventoryMapper.addStock(productId, 1L, actualQty);
            } else {
                inventoryMapper.insertStock(productId, 1L, actualQty);
            }

            // C. 记录流水
            warehouseFlowMapper.insertFlow(1L, productId, (byte) 1, actualQty, adminId, String.valueOf(purchaseId),
                    remark);
        }

        // 2. 更新采购单主表状态为 已入库 (2)
        purchaseOrderMapper.completePurchaseOrder(purchaseId);

        return Result.success("入库成功！商品已加入库存。");
    }

    @Override
    public Result<List<Map<String, Object>>> getPendingTransfers(String transferNo, String orderNo, String dateStart,
            String dateEnd) {
        return Result.success(transferOrderMapper.searchPendingTransfers(transferNo, orderNo, dateStart, dateEnd));
    }

    @Override
    public Result<List<Map<String, Object>>> getTransferItems(Long transferId) {
        return Result.success(transferOrderMapper.selectTransferItems(transferId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> executeOutbound(Long transferId, Long adminId, Long stationId) {
        // 1. 获取调拨单对应的商品列表
        List<Map<String, Object>> items = transferOrderMapper.selectTransferItems(transferId);
        if (items == null || items.isEmpty()) {
            return Result.error("出库失败：未找到该调拨单的商品明细");
        }

        // ================= 1.1 前置库存校验 (Pre-check) =================
        // 避免在循环扣减途中发现不足抛出异常引起 500
        for (Map<String, Object> item : items) {
            if (item.get("product_id") == null || item.get("quantity") == null) {
                return Result.error("调拨明细数据异常，请联系管理员");
            }
            Long productId = Long.valueOf(item.get("product_id").toString());
            Integer qty = Integer.valueOf(item.get("quantity").toString());
            String productName = item.get("product_name") != null ? item.get("product_name").toString() : "未知商品";

            int currentStock = inventoryMapper.selectStockByProductIdAndWarehouseId(productId, 1L);
            log.info("预校验出库 - productId: {}, 需要数量: {}, 中心库当前可用库存: {}", productId, qty, currentStock);

            if (currentStock < qty) {
                return Result.error("商品出库失败：[" + productName + "] 中心库房当前库存不足！（需要 " + qty + " 个，仅剩 " + currentStock + " 个）");
            }
        }

        // ================= 2. 实际扣减库存并记录流水 =================
        for (Map<String, Object> item : items) {
            Long productId = Long.valueOf(item.get("product_id").toString());
            Integer qty = Integer.valueOf(item.get("quantity").toString());

            int updated = inventoryMapper.deductCenterWarehouseStock(productId, qty);
            if (updated == 0) {
                throw new RuntimeException("并发扣减失败，库存已被其他操作占用");
            }
            warehouseFlowMapper.insertFlow(1L, productId, (byte) 2, qty, adminId, String.valueOf(transferId), "调拨出库");
        }

        // 3. 更新调拨单为已出库 (1)
        transferOrderMapper.confirmOutbound(transferId, adminId);

        // 4. 生成送往分站的【验货单】
        String inspectionNo = "IN" + System.currentTimeMillis()
                + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        inspectionOrderMapper.insertInspectionOrder(inspectionNo, transferId, stationId, adminId);

        // 5. 联动修改订单状态：因为货物已经发出，客户订单状态变为“中心库房已出库” (3)
        Long orderId = transferOrderMapper.getOrderIdById(transferId);
        if (orderId != null) {
            customerOrderMapper.updateStatusToOutbound(orderId);
            // 任务单状态也更新为 0-未分配 (代表它已经离开中心库，即将到达分站)
            // taskOrderMapper 之前写过一个更新状态的方法么？如果没有我们可以直接这里补,
            // (通常任务单状态0就是初始态，所以可能只改主订单状态即可，调度时就是0)。
        }

        return Result.success("调拨出库成功！已生成验货单。");
    }

    @Override
    public Result<List<Map<String, Object>>> getOutboundPrintData(String dateVal, String productName) {
        return Result.success(transferOrderMapper.selectOutboundPrintData(dateVal, productName));
    }

    @Override
    public Result<List<Map<String, Object>>> getInspectionPrintData(Long stationId, String dateVal,
            String productName) {
        return Result.success(transferOrderMapper.selectInspectionPrintData(stationId, dateVal, productName));
    }

    @Override
    public Result<List<Map<String, Object>>> getAllStations() {
        return Result.success(transferOrderMapper.selectAllStations());
    }
}
