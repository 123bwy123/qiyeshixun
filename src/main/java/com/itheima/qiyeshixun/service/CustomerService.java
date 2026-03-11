package com.itheima.qiyeshixun.service;

import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.dto.CustomerRegisterDTO;
import com.itheima.qiyeshixun.po.Customer;
import java.util.List;

public interface CustomerService {
    // 获取所有客户列表
    List<Customer> getAllCustomers();
    Result registerCustomer(CustomerRegisterDTO dto);
}