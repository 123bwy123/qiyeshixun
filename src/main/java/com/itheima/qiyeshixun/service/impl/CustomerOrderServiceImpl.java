package com.itheima.qiyeshixun.service.impl;

import cn.hutool.core.util.IdUtil;
import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.dto.CustomerOrderSubmitDTO;
import com.itheima.qiyeshixun.dto.OrderItemDTO;
import com.itheima.qiyeshixun.mapper.CustomerOrderMapper;
import com.itheima.qiyeshixun.mapper.InventoryMapper;
import com.itheima.qiyeshixun.mapper.OrderItemMapper;
import com.itheima.qiyeshixun.mapper.ProductMapper;
import com.itheima.qiyeshixun.po.CustomerOrder;
import com.itheima.qiyeshixun.po.Product;
import com.itheima.qiyeshixun.service.CustomerOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CustomerOrderServiceImpl implements CustomerOrderService {

    @Autowired
    private CustomerOrderMapper customerOrderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private InventoryMapper inventoryMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    @Transactional
    public Result submitOrder(CustomerOrderSubmitDTO dto) {
        // 0. 严谨校验：客户不能下空单
        List<OrderItemDTO> itemList = dto.getItemList();
        if (itemList == null || itemList.isEmpty()) {
            return Result.error("订单必须包含至少一件真实商品！");
        }

        // 1. 组装主表数据
        CustomerOrder order = new CustomerOrder();
        order.setCustomerId(dto.getCustomerId());
        order.setReceiveAddress(dto.getReceiveAddress());
        order.setRequireDate(dto.getRequireDate());
        String orderNo = "DD" + IdUtil.getSnowflakeNextIdStr();
        order.setOrderNo(orderNo);
        order.setOperatorId(0L);
        order.setOrderStatus((byte) 0);
        order.setOrderType((byte) 1);
        order.setTotalAmount(new BigDecimal("0.00"));

        // 2. 插入主表
        customerOrderMapper.insertOrder(order);
        Long newOrderId = order.getId();

        // 3. 循环插入订单明细
        for (OrderItemDTO item : itemList) {
            orderItemMapper.insertOrderItem(newOrderId, item.getProductId(), item.getQuantity());
        }

        return Result.success("下单成功！您的客户订单号为：" + orderNo + "，请耐心等待客服人员审核确认。");
    }

    @Override
    public Result getPendingOrders() {
        List<CustomerOrder> list = customerOrderMapper.selectPendingOrders();
        return Result.success(list);
    }

    @Override
    public Result approveOrder(Long orderId, Long operatorId, BigDecimal totalAmount) {
        int rows = customerOrderMapper.approveOrder(orderId, operatorId, totalAmount);
        if (rows > 0) {
            return Result.success("订单审核通过！已流转至调度中心等待分配任务单。");
        }
        return Result.error("审核失败，订单可能不存在");
    }

    // ==================== 客服代客下单（带库存校验）====================

    @Override
    @Transactional
    public Result submitNewOrder(CustomerOrderSubmitDTO dto) {
        // 0. 校验
        List<OrderItemDTO> itemList = dto.getItemList();
        if (itemList == null || itemList.isEmpty()) {
            return Result.error("订单必须包含至少一件商品！");
        }
        if (dto.getCustomerId() == null) {
            return Result.error("请先选择客户！");
        }

        // 1. 遍历商品，查单价 + 检查库存
        BigDecimal totalAmount = BigDecimal.ZERO;
        boolean allInStock = true; // 是否全部有货
        List<String> outOfStockItems = new ArrayList<>(); // 记录缺货商品名

        // 用于保存每个商品的单价信息（避免二次查询）
        BigDecimal[] unitPrices = new BigDecimal[itemList.size()];
        BigDecimal[] amounts = new BigDecimal[itemList.size()];

        for (int i = 0; i < itemList.size(); i++) {
            OrderItemDTO item = itemList.get(i);

            // 查询商品信息获取单价
            Product product = productMapper.selectByPrimaryKey(item.getProductId());
            if (product == null || product.getDelFlag() != 0) {
                return Result.error("商品ID=" + item.getProductId() + " 不存在或已下架！");
            }

            BigDecimal unitPrice = product.getPrice();
            BigDecimal itemAmount = unitPrice.multiply(new BigDecimal(item.getQuantity()));
            unitPrices[i] = unitPrice;
            amounts[i] = itemAmount;
            totalAmount = totalAmount.add(itemAmount);

            // 查询库存总量
            int totalStock = inventoryMapper.selectTotalStockByProductId(item.getProductId());
            if (totalStock < item.getQuantity()) {
                allInStock = false;
                outOfStockItems.add(product.getProductName() + "(需" + item.getQuantity() + ",库存" + totalStock + ")");
            }
        }

        // 2. 组装主表数据
        CustomerOrder order = new CustomerOrder();
        order.setCustomerId(dto.getCustomerId());
        order.setOperatorId(dto.getOperatorId() != null ? dto.getOperatorId() : 0L);
        order.setReceiveAddress(dto.getReceiveAddress());
        order.setRequireDate(dto.getRequireDate());
        String orderNo = "DD" + IdUtil.getSnowflakeNextIdStr();
        order.setOrderNo(orderNo);
        order.setOrderType(dto.getOrderType() != null ? dto.getOrderType() : (byte) 1); // 默认新订
        order.setTotalAmount(totalAmount);

        if (allInStock) {
            // ✅ 全部有货 → 状态 = 2（可分配）
            order.setOrderStatus((byte) 2);
        } else {
            // ⚠️ 有缺货 → 状态 = 1（缺货）
            order.setOrderStatus((byte) 1);
        }

        // 3. 插入主表
        customerOrderMapper.insertOrder(order);
        Long newOrderId = order.getId();

        // 4. 插入明细表（含单价和小计）
        for (int i = 0; i < itemList.size(); i++) {
            OrderItemDTO item = itemList.get(i);
            orderItemMapper.insertOrderItemFull(
                    newOrderId, item.getProductId(), item.getQuantity(),
                    unitPrices[i], amounts[i]);
        }

        // 5. 如果全部有货，扣减库存
        if (allInStock) {
            for (OrderItemDTO item : itemList) {
                inventoryMapper.deductStockByProductId(item.getProductId(), item.getQuantity());
            }
            return Result.success("订单创建成功！订单号：" + orderNo + "，状态：可分配，总金额：¥" + totalAmount);
        } else {
            // 缺货情况 - 不扣库存，返回缺货信息
            return Result.success("订单创建成功（缺货状态）！订单号：" + orderNo
                    + "，以下商品库存不足：" + String.join("、", outOfStockItems)
                    + "。系统将自动发起采购流程。");
        }
    }

    // ==================== 查询可售商品列表 ====================

    @Override
    public List<Map<String, Object>> getAvailableProducts(String keyword) {
        if (StringUtils.hasText(keyword)) {
            return productMapper.searchAvailableProducts(keyword);
        }
        return productMapper.selectAvailableProducts();
    }

    // ==================== 订单管理：查询类 ====================

    @Override
    public Result getOrdersByCustomerId(Long customerId) {
        List<Map<String, Object>> orders = customerOrderMapper.selectOrdersByCustomerId(customerId);
        return Result.success(orders);
    }

    @Override
    public Result getOrderItems(Long orderId) {
        List<Map<String, Object>> items = customerOrderMapper.selectOrderItems(orderId);
        return Result.success(items);
    }

    // ==================== 订单管理：退订 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result cancelOrder(Long orderId, String cancelReason, Long operatorId) {
        // 1. 查询订单当前状态
        CustomerOrder order = customerOrderMapper.selectByPrimaryKey(orderId);
        if (order == null || order.getDelFlag() == 1) {
            return Result.error("订单不存在或已被删除！");
        }

        byte status = order.getOrderStatus();

        // 2. 前置拦截：只允许退订【未被调度】的订单（状态 0/1/2）
        // 状态 3+ 表示已出库、配送中、已完成等，不可退订
        if (status > 2) {
            String statusMsg = getStatusLabel(status);
            return Result.error("当前订单状态为【" + statusMsg + "】，已进入物流环节，无法退订！");
        }

        // 3. 执行退订（软删除，状态改为 8=取消）
        int rows = customerOrderMapper.cancelOrder(orderId, cancelReason);
        if (rows > 0) {
            // 4. 如果原订单是【可分配/已分配库存】，需要归还库存
            if (status == 2) {
                List<Map<String, Object>> items = customerOrderMapper.selectOrderItems(orderId);
                for (Map<String, Object> item : items) {
                    Long productId = ((Number) item.get("product_id")).longValue();
                    Integer qty = ((Number) item.get("quantity")).intValue();
                    // 归还库存：将扣减的库存加回来
                    inventoryMapper.deductStockByProductId(productId, -qty); // 负数 = 增加
                }
            }
            return Result.success("退订成功！订单号：" + order.getOrderNo() + " 已取消。");
        }
        return Result.error("退订失败，请重试！");
    }

    // ==================== 订单管理：换货 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result exchangeOrder(Long originalOrderId, Long productId, Integer quantity,
            String reason, String requireDate, Long operatorId) {
        // 1. 验证原订单状态必须是 7（已完成）
        CustomerOrder original = customerOrderMapper.selectByPrimaryKey(originalOrderId);
        if (original == null || original.getDelFlag() == 1) {
            return Result.error("原始订单不存在！");
        }
        if (original.getOrderStatus() != 7) {
            return Result.error("只有【已完成】的订单才能申请换货，当前状态：【"
                    + getStatusLabel(original.getOrderStatus()) + "】");
        }

        // 2. 验证换货数量不能大于原订购数量
        List<Map<String, Object>> items = customerOrderMapper.selectOrderItems(originalOrderId);
        int originalQty = 0;
        for (Map<String, Object> item : items) {
            if (item.get("product_id") != null &&
                    ((Number) item.get("product_id")).longValue() == productId) {
                originalQty = ((Number) item.get("quantity")).intValue();
                break;
            }
        }
        if (originalQty == 0) {
            return Result.error("该订单中未找到指定商品！");
        }
        if (quantity > originalQty) {
            return Result.error("换货数量（" + quantity + "）不能大于原订购数量（" + originalQty + "）！");
        }

        // 3. 检查库存是否充足
        int totalStock = inventoryMapper.selectTotalStockByProductId(productId);
        if (totalStock < quantity) {
            return Result.error("当前库存不足（库存：" + totalStock + "，换货需求：" + quantity + "），无法换货！");
        }

        // 4. 查商品信息，计算金额
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null)
            return Result.error("商品不存在！");
        BigDecimal unitPrice = product.getPrice();
        BigDecimal totalAmount = unitPrice.multiply(new BigDecimal(quantity));

        // 5. 生成换货订单
        CustomerOrder exchangeOrder = new CustomerOrder();
        exchangeOrder.setCustomerId(original.getCustomerId());
        exchangeOrder.setOperatorId(operatorId != null ? operatorId : 0L);
        exchangeOrder.setOrderNo("HH" + IdUtil.getSnowflakeNextIdStr());
        exchangeOrder.setOrderType((byte) 3); // 3=换货
        exchangeOrder.setOrderStatus((byte) 2); // 直接可分配
        exchangeOrder.setTotalAmount(totalAmount);
        exchangeOrder.setReceiveAddress(original.getReceiveAddress());
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            exchangeOrder.setRequireDate(sdf.parse(requireDate));
        } catch (Exception ignored) {
        }

        customerOrderMapper.insertOrder(exchangeOrder);
        Long newOrderId = exchangeOrder.getId();

        // 6. 插入换货明细
        orderItemMapper.insertOrderItemFull(newOrderId, productId, quantity, unitPrice, totalAmount);

        // 7. 扣减库存
        inventoryMapper.deductStockByProductId(productId, quantity);

        return Result.success("换货申请成功！换货订单号：" + exchangeOrder.getOrderNo());
    }

    // ==================== 订单管理：退货 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result returnOrder(Long originalOrderId, Long productId, Integer quantity,
            String reason, String requireDate, Long operatorId) {
        // 1. 验证原订单状态必须是 7（已完成）
        CustomerOrder original = customerOrderMapper.selectByPrimaryKey(originalOrderId);
        if (original == null || original.getDelFlag() == 1) {
            return Result.error("原始订单不存在！");
        }
        if (original.getOrderStatus() != 7) {
            return Result.error("只有【已完成】的订单才能申请退货，当前状态：【"
                    + getStatusLabel(original.getOrderStatus()) + "】");
        }

        // 2. 验证退货数量不能大于原订购数量
        List<Map<String, Object>> items = customerOrderMapper.selectOrderItems(originalOrderId);
        int originalQty = 0;
        for (Map<String, Object> item : items) {
            if (item.get("product_id") != null &&
                    ((Number) item.get("product_id")).longValue() == productId) {
                originalQty = ((Number) item.get("quantity")).intValue();
                break;
            }
        }
        if (originalQty == 0) {
            return Result.error("该订单中未找到指定商品！");
        }
        if (quantity > originalQty) {
            return Result.error("退货数量（" + quantity + "）不能大于原订购数量（" + originalQty + "）！");
        }

        // 3. 查商品价格（用于金额计算）
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null)
            return Result.error("商品不存在！");
        BigDecimal unitPrice = product.getPrice();
        BigDecimal totalAmount = unitPrice.multiply(new BigDecimal(quantity));

        // 4. 生成退货订单（退货属于负向流程，金额为负）
        CustomerOrder returnOrd = new CustomerOrder();
        returnOrd.setCustomerId(original.getCustomerId());
        returnOrd.setOperatorId(operatorId != null ? operatorId : 0L);
        returnOrd.setOrderNo("TH" + IdUtil.getSnowflakeNextIdStr());
        returnOrd.setOrderType((byte) 4); // 4=退货
        returnOrd.setOrderStatus((byte) 2); // 可分配（等待库房确认收货）
        returnOrd.setTotalAmount(totalAmount.negate()); // 负金额
        returnOrd.setReceiveAddress(original.getReceiveAddress());
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            returnOrd.setRequireDate(sdf.parse(requireDate));
        } catch (Exception ignored) {
        }

        customerOrderMapper.insertOrder(returnOrd);
        Long newOrderId = returnOrd.getId();

        // 5. 插入退货明细（单价为负告知系统这是退款）
        orderItemMapper.insertOrderItemFull(newOrderId, productId, quantity, unitPrice, totalAmount);

        return Result.success("退货申请成功！退货订单号：" + returnOrd.getOrderNo() + "，请等待库房确认收货。");
    }

    // ==================== 辅助方法 ====================

    private String getStatusLabel(byte status) {
        switch (status) {
            case 0:
                return "待审核";
            case 1:
                return "缺货";
            case 2:
                return "可分配";
            case 3:
                return "中心库已出库";
            case 4:
                return "分站已到货";
            case 5:
                return "任务已分配";
            case 6:
                return "已领货";
            case 7:
                return "已完成";
            case 8:
                return "已取消";
            case 9:
                return "已调度待出库";
            default:
                return "未知状态(" + status + ")";
        }
    }
}