package com.itheima.qiyeshixun.service;
import com.itheima.qiyeshixun.common.Result;

public interface DispatchService {
    // 查询所有可分配(状态为1)的订单
    Result getAssignableOrders();

    // 执行自动调度/手动调度
    Result executeDispatch(Long orderId, Long stationId);
}