package com.itheima.qiyeshixun.service;
import com.itheima.qiyeshixun.common.Result;

public interface StationService {
    Result getPendingTasks();
    Result assignCourier(Long taskId, Long orderId, Long courierId, Long adminId);
    Result getPendingCloseTasks();
    Result closeOrder(Long taskId, Long orderId);
}