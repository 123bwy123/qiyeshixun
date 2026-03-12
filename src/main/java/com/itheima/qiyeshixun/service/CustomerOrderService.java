package com.itheima.qiyeshixun.service;

import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.dto.CustomerOrderSubmitDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface CustomerOrderService {
    Result submitOrder(CustomerOrderSubmitDTO dto);

    Result getPendingOrders();

    Result approveOrder(Long orderId, Long operatorId, BigDecimal totalAmount);

    // 客服代客下单（带库存校验）
    Result submitNewOrder(CustomerOrderSubmitDTO dto);

    // 查询可售商品列表
    List<Map<String, Object>> getAvailableProducts(String keyword);

    // ===== 订单管理模块 =====

    // 按客户 ID 查询该客户的所有订单
    Result getOrdersByCustomerId(Long customerId);

    // 查询指定订单的商品明细
    Result getOrderItems(Long orderId);

    // 退订：只允许退订状态 ≤ 2（未被调度）的订单
    Result cancelOrder(Long orderId, String cancelReason, Long operatorId);

    // 换货：状态=7（已完成），且库存足够
    Result exchangeOrder(Long originalOrderId, Long productId, Integer quantity,
            String reason, String requireDate, Long operatorId);

    // 退货：状态=7（已完成），数量 ≤ 原订单数量
    Result returnOrder(Long originalOrderId, Long productId, Integer quantity,
            String reason, String requireDate, Long operatorId);
}