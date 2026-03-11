package com.itheima.qiyeshixun.service;
import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.dto.CustomerOrderSubmitDTO;

import java.math.BigDecimal;

public interface CustomerOrderService {
    Result submitOrder(CustomerOrderSubmitDTO dto);
    Result getPendingOrders();
    Result approveOrder(Long orderId, Long operatorId, BigDecimal totalAmount);
}