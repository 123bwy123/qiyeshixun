package com.itheima.qiyeshixun.service.impl;

import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.mapper.TaskOrderMapper;
import com.itheima.qiyeshixun.service.CourierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourierServiceImpl implements CourierService {

    @Autowired
    private TaskOrderMapper taskOrderMapper;

    @Override
    public Result getMyTasks(Long courierId) {
        return Result.success(taskOrderMapper.selectMyDeliveringTasks(courierId));
    }

    @Override
    public Result confirmDelivery(Long taskId) {
        int rows = taskOrderMapper.completeDeliveryTask(taskId);
        if (rows > 0) {
            return Result.success("送达确认成功！辛苦了，请保管好货款及回执单，交回网点站长。");
        }
        return Result.error("确认送达失败，请检查任务单状态");
    }
}