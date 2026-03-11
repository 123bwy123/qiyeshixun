package com.itheima.qiyeshixun.service.impl;

import cn.hutool.core.util.IdUtil;
import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.dto.CustomerOrderSubmitDTO;
import com.itheima.qiyeshixun.dto.OrderItemDTO;
import com.itheima.qiyeshixun.mapper.CustomerOrderMapper;
import com.itheima.qiyeshixun.mapper.OrderItemMapper;
import com.itheima.qiyeshixun.po.CustomerOrder;
import com.itheima.qiyeshixun.service.CustomerOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CustomerOrderServiceImpl implements CustomerOrderService {

    @Autowired
    private CustomerOrderMapper customerOrderMapper;

    // 【新增】注入明细表 Mapper
    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    @Transactional // 【核心】：加上事务注解！如果存明细报错了，主表订单也会自动回滚取消，绝不产生脏数据！
    public Result submitOrder(CustomerOrderSubmitDTO dto) {
        // 0. 严谨校验：客户不能下空单
        List<OrderItemDTO> itemList = dto.getItemList();
        if (itemList == null || itemList.isEmpty()) {
            return Result.error("订单必须包含至少一件真实商品！");
        }

        // 1. 组装主表数据
        CustomerOrder order = new CustomerOrder();
        order.setCustomerId(dto.getCustomerId());
        order.setReceiveAddress(dto.getReceiveAddress());
        order.setRequireDate(dto.getRequireDate());
        String orderNo = "DD" + IdUtil.getSnowflakeNextIdStr();
        order.setOrderNo(orderNo);
        order.setOperatorId(0L);
        order.setOrderStatus((byte) 0);
        order.setOrderType((byte) 1);
        order.setTotalAmount(new BigDecimal("0.00")); // （真实项目中这里应该去查 product 表算出总价，咱们暂时先放 0）

        // 2. 插入主表 (执行完这句后，因为加了 @Options 注解，order.getId() 就有值了)
        customerOrderMapper.insertOrder(order);
        Long newOrderId = order.getId();

        // 3. 循环插入订单明细 (将客户选的真实商品入库)
        for (OrderItemDTO item : itemList) {
            orderItemMapper.insertOrderItem(newOrderId, item.getProductId(), item.getQuantity());
        }

        return Result.success("下单成功！您的客户订单号为：" + orderNo + "，请耐心等待客服人员审核确认。");
    }

    @Override
    public Result getPendingOrders() {
        List<CustomerOrder> list = customerOrderMapper.selectPendingOrders();
        return Result.success(list);
    }

    @Override
    public Result approveOrder(Long orderId, Long operatorId, BigDecimal totalAmount) {
        // 核心逻辑：客服点击通过，状态变为 1，并记录是哪个客服处理的，以及算出的总运费
        int rows = customerOrderMapper.approveOrder(orderId, operatorId, totalAmount);
        if (rows > 0) {
            // （画外音：按照需求文档，如果这里要查库存缺不缺货，咱们以后再加这层逻辑，先把主流程跑通）
            return Result.success("订单审核通过！已流转至调度中心等待分配任务单。");
        }
        return Result.error("审核失败，订单可能不存在");
    }
}