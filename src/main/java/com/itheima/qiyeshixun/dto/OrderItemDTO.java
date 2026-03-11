package com.itheima.qiyeshixun.dto;

public class OrderItemDTO {
    private Long productId; // 客户选择的真实商品ID
    private Integer quantity; // 订购数量

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}