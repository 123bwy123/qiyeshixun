package com.itheima.qiyeshixun.service.impl;

import com.itheima.qiyeshixun.mapper.CustomerMapper;
import com.itheima.qiyeshixun.po.Customer;
import com.itheima.qiyeshixun.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service // 告诉 Spring Boot 这是一个业务类，把它交给你管理
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerMapper customerMapper; // 注入刚才的 Mapper

    @Override
    public List<Customer> getAllCustomers() {
        // 直接调用 Mapper 查数据库
        return customerMapper.selectAllCustomers();
    }
}