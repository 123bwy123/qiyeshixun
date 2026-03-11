package com.itheima.qiyeshixun.service.impl;

import cn.hutool.core.util.IdUtil;
import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.mapper.CustomerOrderMapper;
import com.itheima.qiyeshixun.mapper.TaskOrderMapper;
import com.itheima.qiyeshixun.mapper.TransferOrderMapper;
import com.itheima.qiyeshixun.po.CustomerOrder;
import com.itheima.qiyeshixun.service.DispatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DispatchServiceImpl implements DispatchService {

    @Autowired
    private CustomerOrderMapper customerOrderMapper;
    @Autowired
    private TaskOrderMapper taskOrderMapper;
    @Autowired
    private TransferOrderMapper transferOrderMapper;

    @Override
    public Result getAssignableOrders() {
        // 这里需要你在 CustomerOrderMapper 里加一个查 status=1 的 SQL (类似查 pendingList)
        // 为了省事，你可以直接复用之前写好的，或者新建一个 selectAssignableOrders
        return Result.success("该接口后续在 Controller 里调用");
    }

    @Override
    @Transactional // 【生死攸关的注解】必须保证 一生二 要么全成功，要么全失败！
    public Result executeDispatch(Long orderId, Long stationId) {

        // 1. 生成发给配送站的【任务单 TaskOrder】
        String taskNo = "RW" + IdUtil.getSnowflakeNextIdStr();
        // 假设当前这单给配送员的结算提成是 15.00 元
        taskOrderMapper.insertTaskOrder(taskNo, orderId, stationId, new BigDecimal("15.00"));

        // 2. 生成发给中心库房的【调拨单 TransferOrder】
        String transferNo = "DB" + IdUtil.getSnowflakeNextIdStr();
        Long centralWarehouseId = 1L; // 假设中心总库房的 ID 永远是 1
        // 简化业务：假设分站的 ID (stationId) 就是分站库房的 ID (inWarehouseId)
        transferOrderMapper.insertTransferOrder(transferNo, orderId, centralWarehouseId, stationId);

        // 3. 更新原【客户订单】的状态为 2 (已调度)
        customerOrderMapper.updateStatusToDispatched(orderId);

        return Result.success("调度成功！已自动生成任务单[" + taskNo + "]与调拨单[" + transferNo + "]");
    }
}