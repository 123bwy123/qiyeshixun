package com.itheima.qiyeshixun.controller;

import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.dto.CustomerOrderSubmitDTO;
import com.itheima.qiyeshixun.service.CustomerOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/customer/order")
@CrossOrigin // 允许跨域
public class CustomerOrderController {

    @Autowired
    private CustomerOrderService customerOrderService;

    /** 客户自主下单接口 */
    @PostMapping("/submit")
    public Result submit(@RequestBody CustomerOrderSubmitDTO dto) {
        if (dto.getCustomerId() == null || dto.getReceiveAddress() == null) {
            return Result.error("客户ID和收货地址不能为空！");
        }
        return customerOrderService.submitOrder(dto);
    }

    // 获取待审核列表
    @GetMapping("/pendingList")
    public Result getPendingList() {
        return customerOrderService.getPendingOrders();
    }

    // 客服审核通过接口
    @PostMapping("/approve")
    public Result approve(@RequestParam Long orderId, @RequestParam Long operatorId,
            @RequestParam BigDecimal totalAmount) {
        return customerOrderService.approveOrder(orderId, operatorId, totalAmount);
    }

    /** 查询可售商品列表（带库存信息） */
    @GetMapping("/products")
    public Result getProducts(@RequestParam(required = false) String keyword) {
        return Result.success(customerOrderService.getAvailableProducts(keyword));
    }

    /** 客服代客新订（带库存校验） */
    @PostMapping("/newOrder")
    public Result newOrder(@RequestBody CustomerOrderSubmitDTO dto) {
        if (dto.getCustomerId() == null || dto.getReceiveAddress() == null) {
            return Result.error("客户ID和收货地址不能为空！");
        }
        if (dto.getItemList() == null || dto.getItemList().isEmpty()) {
            return Result.error("请至少选择一件商品！");
        }
        return customerOrderService.submitNewOrder(dto);
    }

    // ==================== 订单管理接口 ====================

    /** 查询某客户的所有订单 */
    @GetMapping("/byCustomer")
    public Result getOrdersByCustomer(@RequestParam Long customerId) {
        return customerOrderService.getOrdersByCustomerId(customerId);
    }

    /** 查询某订单的商品明细 */
    @GetMapping("/items")
    public Result getOrderItems(@RequestParam Long orderId) {
        return customerOrderService.getOrderItems(orderId);
    }

    /** 退订（取消）订单 */
    @PostMapping("/cancel")
    public Result cancelOrder(@RequestBody Map<String, Object> body) {
        Long orderId = Long.valueOf(body.get("orderId").toString());
        String cancelReason = (String) body.getOrDefault("cancelReason", "");
        Long operatorId = body.get("operatorId") != null ? Long.valueOf(body.get("operatorId").toString()) : 0L;
        return customerOrderService.cancelOrder(orderId, cancelReason, operatorId);
    }

    /** 换货申请 */
    @PostMapping("/exchange")
    public Result exchangeOrder(@RequestBody Map<String, Object> body) {
        Long originalOrderId = Long.valueOf(body.get("originalOrderId").toString());
        Long productId = Long.valueOf(body.get("productId").toString());
        Integer quantity = Integer.valueOf(body.get("quantity").toString());
        String reason = (String) body.getOrDefault("reason", "");
        String requireDate = (String) body.get("requireDate");
        Long operatorId = body.get("operatorId") != null ? Long.valueOf(body.get("operatorId").toString()) : 0L;
        return customerOrderService.exchangeOrder(originalOrderId, productId, quantity, reason, requireDate,
                operatorId);
    }

    /** 退货申请 */
    @PostMapping("/return")
    public Result returnOrder(@RequestBody Map<String, Object> body) {
        Long originalOrderId = Long.valueOf(body.get("originalOrderId").toString());
        Long productId = Long.valueOf(body.get("productId").toString());
        Integer quantity = Integer.valueOf(body.get("quantity").toString());
        String reason = (String) body.getOrDefault("reason", "");
        String requireDate = (String) body.get("requireDate");
        Long operatorId = body.get("operatorId") != null ? Long.valueOf(body.get("operatorId").toString()) : 0L;
        return customerOrderService.returnOrder(originalOrderId, productId, quantity, reason, requireDate, operatorId);
    }
}