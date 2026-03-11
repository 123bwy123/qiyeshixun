package com.itheima.qiyeshixun.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.dto.CustomerRegisterDTO;
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
    // 处理客户注册业务
    @Override
    public Result registerCustomer(CustomerRegisterDTO dto) {
        // 1. 业务校验：检查手机号是否已被注册
        Customer existCustomer = customerMapper.selectByMobile(dto.getMobile());
        if (existCustomer != null) {
            return Result.error("该手机号已注册，请直接前往登录！");
        }

        // 2. 将 DTO 的数据拷贝到 PO（数据库实体类）中
        Customer customer = new Customer();
        customer.setCustomerName(dto.getCustomerName());
        customer.setMobile(dto.getMobile());
        customer.setIdCard(dto.getIdCard());
        customer.setAddress(dto.getAddress());

        // 3. 【核心安全】生成随机盐，并使用 BCrypt 将前端传来的密文再次强加密！
        // 这样哪怕数据库泄露，黑客也拿不到密码的任何信息
        String bCryptPassword = BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt());
        customer.setPassword(bCryptPassword);

        // 4. 保存到数据库
        customerMapper.insertCustomer(customer);

        return Result.success("客户注册成功！");
    }
}