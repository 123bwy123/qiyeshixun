package com.itheima.qiyeshixun.po;

import java.util.Date;

/**
 * 客户信息表
 */
public class Customer {
    /**
     * 客户ID（主键，自增）
     */
    private Long id;

    /**
     * 客户姓名（必填）
     */
    private String customerName;

    /**
     * 身份证号（必填）
     */
    private String idCard;

    /**
     * 工作单位
     */
    private String company;

    /**
     * 座机
     */
    private String phone;

    /**
     * 移动电话（与座机二选一必填）
     */
    private String mobile;

    /**
     * 联系地址（必填）
     */
    private String address;

    /**
     * 邮编
     */
    private String zipCode;

    /**
     * 逻辑删除标识（0=正常，1=删除）
     */
    private Integer delFlag;

    /**
     * 登录密码（BCrypt 加密存储，C 端客户门户使用）
     */
    private String password;

    private Date createTime;
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
