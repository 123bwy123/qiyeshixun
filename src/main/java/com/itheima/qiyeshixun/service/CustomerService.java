package com.itheima.qiyeshixun.service;

import com.itheima.qiyeshixun.po.Customer;
import java.util.List;

public interface CustomerService {
    // 获取所有客户列表
    List<Customer> getAllCustomers();
}