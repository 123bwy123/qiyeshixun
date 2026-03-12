package com.itheima.qiyeshixun.controller;

import com.github.pagehelper.PageInfo;
import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.dto.CustomerOrderDetailDTO;
import com.itheima.qiyeshixun.dto.CustomerRegisterDTO;
import com.itheima.qiyeshixun.po.Customer;
import com.itheima.qiyeshixun.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer")
@CrossOrigin
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    /**
     * 分页查询客户列表
     */
    @GetMapping("/list")
    public Result<PageInfo<Customer>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String idCard,
            @RequestParam(required = false) String mobile) {
        PageInfo<Customer> pageInfo = customerService.findPage(page, size, name, idCard, mobile);
        return Result.success(pageInfo);
    }

    /**
     * 新增客户
     */
    @PostMapping("/add")
    public Result add(@RequestBody Customer customer) {
        return customerService.add(customer);
    }

    /**
     * 修改客户
     */
    @PutMapping("/update")
    public Result update(@RequestBody Customer customer) {
        return customerService.update(customer);
    }

    /**
     * 删除客户
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        return customerService.delete(id);
    }

    /**
     * 查询客户订单明细
     */
    @GetMapping("/orders/{customerId}")
    public Result<List<CustomerOrderDetailDTO>> getOrders(@PathVariable Long customerId) {
        List<CustomerOrderDetailDTO> list = customerService.getOrders(customerId);
        return Result.success(list);
    }

    /**
     * 客户注册接口 (保留)
     */
    @PostMapping("/register")
    public Result register(@RequestBody CustomerRegisterDTO registerDTO) {
        return customerService.registerCustomer(registerDTO);
    }
}
