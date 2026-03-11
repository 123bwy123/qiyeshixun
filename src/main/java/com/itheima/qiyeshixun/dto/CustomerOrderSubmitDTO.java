package com.itheima.qiyeshixun.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class CustomerOrderSubmitDTO {
    // 模拟当前登录的客户ID (等上了 JWT 咱们再从 Token 取)
    private Long customerId;

    // 收货详细地址 (可以把收件人姓名电话一并填在这里，比如"张三 13800138000 北京市...")
    private String receiveAddress;

    // 客户要求的送达日期 (前端传类似 "2026-05-01" 的字符串)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date requireDate;
    // 订单包含的具体商品明细列表
    private List<OrderItemDTO> itemList;

    public List<OrderItemDTO> getItemList() { return itemList; }
    public void setItemList(List<OrderItemDTO> itemList) { this.itemList = itemList; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getReceiveAddress() { return receiveAddress; }
    public void setReceiveAddress(String receiveAddress) { this.receiveAddress = receiveAddress; }
    public Date getRequireDate() { return requireDate; }
    public void setRequireDate(Date requireDate) { this.requireDate = requireDate; }
}