package com.itheima.qiyeshixun.dto;

import java.math.BigDecimal;
import java.util.Date;

public class CustomerOrderDetailDTO {
    private String orderNo;
    private Date createTime;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount; // 此处可能是订单总额，或者是该项总额。通常列表展示时展示该项总额更合理，但根据需求描述"Total Amount"可能指订单总额。我会两个都放或者注明。这里放 Item Amount (quantity * unitPrice) 比较适合明细。但如果需求是指订单列表，那就是订单总额。
    // 需求原文："查询该客户订购的商品记录... 订单号、日期、商品名称、数量、单价、总额、状态"。
    // 这听起来像是一个扁平的列表。
    // 我将包含 itemAmount 和 orderTotalAmount 以防万一。
    private BigDecimal itemAmount;
    private Byte orderStatus; // 状态码
    private String orderStatusName; // 状态名称（可选，前端处理也可）

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getItemAmount() {
        return itemAmount;
    }

    public void setItemAmount(BigDecimal itemAmount) {
        this.itemAmount = itemAmount;
    }

    public Byte getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Byte orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderStatusName() {
        return orderStatusName;
    }

    public void setOrderStatusName(String orderStatusName) {
        this.orderStatusName = orderStatusName;
    }
}
