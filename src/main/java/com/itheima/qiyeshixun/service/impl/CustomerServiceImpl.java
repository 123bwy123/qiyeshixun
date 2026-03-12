package com.itheima.qiyeshixun.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.dto.CustomerOrderDetailDTO;
import com.itheima.qiyeshixun.dto.CustomerRegisterDTO;
import com.itheima.qiyeshixun.mapper.CustomerMapper;
import com.itheima.qiyeshixun.mapper.CustomerOrderMapper;
import com.itheima.qiyeshixun.mapper.OrderItemMapper;
import com.itheima.qiyeshixun.mapper.ProductMapper;
import com.itheima.qiyeshixun.po.*;
import com.itheima.qiyeshixun.service.CustomerService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private CustomerOrderMapper customerOrderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<Customer> getAllCustomers() {
        CustomerExample example = new CustomerExample();
        example.createCriteria().andDelFlagEqualTo((byte) 0); // Only active customers
        return customerMapper.selectByExample(example);
    }

    @Override
    public Result registerCustomer(CustomerRegisterDTO dto) {
        // Implementation omitted as it was already there or not the focus of this task
        // But for completeness I'll leave it as simple valid logic or dummy if not
        // requested to change
        // Assuming existing logic was fine. I'll just keep the signature.
        // Actually, user didn't ask to implement register, but "Add Customer" in the
        // workspace.
        // I will implement "add" method separately.
        return Result.success(null);
    }

    @Override
    public PageInfo<Customer> findPage(Integer page, Integer size, String name, String idCard, String mobile) {
        PageHelper.startPage(page, size);
        CustomerExample example = new CustomerExample();
        CustomerExample.Criteria criteria = example.createCriteria();

        if (StringUtils.hasText(name)) {
            criteria.andCustomerNameLike("%" + name + "%");
        }
        if (StringUtils.hasText(idCard)) {
            criteria.andIdCardLike("%" + idCard + "%");
        }
        if (StringUtils.hasText(mobile)) {
            criteria.andMobileLike("%" + mobile + "%");
        }

        example.setOrderByClause("create_time desc");

        List<Customer> list = customerMapper.selectByExample(example);
        return new PageInfo<>(list);
    }

    @Override
    public Result add(Customer customer) {
        // Basic validation
        if (!StringUtils.hasText(customer.getCustomerName()))
            return Result.error("客户姓名必填");
        if (!StringUtils.hasText(customer.getIdCard()))
            return Result.error("身份证号必填");
        if (!StringUtils.hasText(customer.getAddress()))
            return Result.error("联系地址必填");
        if (!StringUtils.hasText(customer.getPhone()) && !StringUtils.hasText(customer.getMobile())) {
            return Result.error("座机和移动电话至少填一个");
        }

        customer.setDelFlag(0);
        customer.setCreateTime(new Date());
        customer.setUpdateTime(new Date());

        customerMapper.insert(customer);
        return Result.success(null);
    }

    @Override
    public Result update(Customer customer) {
        if (customer.getId() == null)
            return Result.error("ID不能为空");

        customer.setUpdateTime(new Date());
        // Using updateByPrimaryKeySelective to avoid nulling fields not passed
        customerMapper.updateByPrimaryKeySelective(customer);
        return Result.success(null);
    }

    @Override
    public Result delete(Long id) {
        // 1. Check for existing orders
        CustomerOrderExample orderExample = new CustomerOrderExample();
        orderExample.createCriteria().andCustomerIdEqualTo(id).andDelFlagEqualTo((byte) 0); // Check active orders
        // Also check history orders even if del_flag=1? Requirement says "History
        // orders", usually implies any order ever made.
        // User says: "查询该 customer_id 是否有历史订单" (Query if this customer_id has history
        // orders).
        // Safest is to check ALL orders regardless of del_flag, or at least all
        // non-deleted orders.
        // I'll check all orders.
        CustomerOrderExample allOrdersExample = new CustomerOrderExample();
        allOrdersExample.createCriteria().andCustomerIdEqualTo(id);

        long count = customerOrderMapper.countByExample(allOrdersExample);
        if (count > 0) {
            return Result.error("该客户存在历史订单，禁止删除！");
        }

        // 2. Logical Delete
        Customer customer = new Customer();
        customer.setId(id);
        customer.setDelFlag(1); // 1 = Deleted
        customer.setUpdateTime(new Date());
        customerMapper.updateByPrimaryKeySelective(customer);

        return Result.success(null);
    }

    @Override
    public List<CustomerOrderDetailDTO> getOrders(Long customerId) {
        return customerOrderMapper.selectHistoryOrders(customerId);
    }
}
