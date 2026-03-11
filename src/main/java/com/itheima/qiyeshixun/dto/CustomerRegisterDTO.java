package com.itheima.qiyeshixun.dto;

/**
 * 专门用于接收前端客户注册数据的 DTO
 */
public class CustomerRegisterDTO {
    private String customerName;
    private String mobile;
    private String idCard;
    private String password; // 接收前端传来的 SHA-256 密文
    private String address;


    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public String getIdCard() { return idCard; }
    public void setIdCard(String idCard) { this.idCard = idCard; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}