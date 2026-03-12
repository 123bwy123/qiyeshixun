package com.itheima.qiyeshixun.service;

import com.itheima.qiyeshixun.common.Result;

import java.math.BigDecimal;

public interface StationService {

    /** 查询本分站未分配任务单（支持日期+类型过滤） */
    Result getPendingTasks(Long stationId, Byte taskType, String dateStart, String dateEnd);

    /** 任务分配：分配配送员，同步更新主订单状态为配送中 */
    Result assignCourier(Long taskId, Long orderId, Long courierId, Long adminId);

    /** 查询本分站已分配的任务单（供打印签收单、回执录入使用） */
    Result getAssignedTasks(Long stationId);

    /** 获取打印数据：任务头部信息 + 商品明细列表 */
    Result getPrintData(Long taskId);

    /** 获取本站配送员列表 */
    Result getCouriers();

    /**
     * 回执录入并结单（核心大事务）：
     * 1. 更新 task_order（状态/实收金额/备注/完成时间）
     * 2. 更新 customer_order（根据完成状态：已完成=7，失败=8）
     */
    Result submitReceipt(Long taskId, Long orderId, Byte completeStatus,
            BigDecimal actualAmount, String remark);

    /** 缴款统计查询（按分站+日期段，聚合 GROUP BY） */
    Result getPaymentStats(Long stationId, String dateStart, String dateEnd);
}