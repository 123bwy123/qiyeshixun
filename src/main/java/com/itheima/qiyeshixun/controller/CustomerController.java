package com.itheima.qiyeshixun.controller;

import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.po.Customer;
import com.itheima.qiyeshixun.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController // 代表这个类下面所有接口返回的都是 JSON 格式数据
@RequestMapping("/customer") // 这个 Controller 的统一路由前缀
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    /**
     * 查询所有客户接口
     * 浏览器访问地址: http://localhost:8080/customer/list
     */
    @GetMapping("/list")
    public Result<List<Customer>> list() {
        // 1. 调 Service 拿数据
        List<Customer> list = customerService.getAllCustomers();
        // 2. 用我们之前写的 Result 包装一下返回给前端
        return Result.success(list);
    }
}