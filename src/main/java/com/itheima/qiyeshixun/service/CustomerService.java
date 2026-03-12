package com.itheima.qiyeshixun.service;

import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.dto.CustomerOrderDetailDTO;
import com.itheima.qiyeshixun.dto.CustomerRegisterDTO;
import com.itheima.qiyeshixun.po.Customer;
import java.util.List;
import com.github.pagehelper.PageInfo;

public interface CustomerService {
    // 获取所有客户列表 (Existing)
    List<Customer> getAllCustomers();
    
    // 注册 (Existing)
    Result registerCustomer(CustomerRegisterDTO dto);

    // 分页查询
    PageInfo<Customer> findPage(Integer page, Integer size, String name, String idCard, String mobile);

    // 新增
    Result add(Customer customer);

    // 修改
    Result update(Customer customer);

    // 删除
    Result delete(Long id);

    // 获取订单详情
    List<CustomerOrderDetailDTO> getOrders(Long customerId);
}
