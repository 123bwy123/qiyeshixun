package com.itheima.qiyeshixun.service;
import com.itheima.qiyeshixun.common.Result;

public interface CourierService {
    Result getMyTasks(Long courierId);
    Result confirmDelivery(Long taskId);
}