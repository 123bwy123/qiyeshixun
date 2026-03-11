package com.itheima.qiyeshixun.service.impl;

import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.mapper.CustomerOrderMapper;
import com.itheima.qiyeshixun.mapper.TaskOrderMapper;
import com.itheima.qiyeshixun.service.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StationServiceImpl implements StationService {

    @Autowired
    private TaskOrderMapper taskOrderMapper;
    @Autowired
    private CustomerOrderMapper customerOrderMapper;

    @Override
    public Result getPendingTasks() {
        return Result.success(taskOrderMapper.selectPendingTasks());
    }

    @Override
    @Transactional // 核心操作：改任务单和小哥关联，同时改客户主单状态，必须加事务！
    public Result assignCourier(Long taskId, Long orderId, Long courierId, Long adminId) {
        // 1. 更新任务单，绑定配送员
        taskOrderMapper.assignCourier(taskId, courierId, adminId);

        // 2. 更新原客户订单状态为 3 (配送中)
        customerOrderMapper.updateStatusToDelivering(orderId);

        return Result.success("任务已下发至配送员终端！包裹进入【派送中】状态。");
    }
}