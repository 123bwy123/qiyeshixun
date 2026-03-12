package com.itheima.qiyeshixun.service;

import com.itheima.qiyeshixun.common.Result;

public interface DispatchService {

    /** 获取所有【可分配】的订单（状态=2），用于手工调度展示 */
    Result getAssignableOrders();

    /** 获取所有【可分配 且 已带默认分站ID】的订单，用于自动调度 */
    Result getAutoDispatchableOrders();

    /** 获取所有【缺货】的订单（状态=1） */
    Result getOutOfStockOrders();

    /** 获取系统中所有分站管理员列表（供调度员选择分站） */
    Result getStationAdmins();

    /**
     * 手工调度（核心大事务）：
     * 1. customer_order 状态 → 9（已调度待出库）
     * 2. 插入 task_order（任务单）
     * 3. 插入 transfer_order（调拨单）
     */
    Result manualDispatch(Long orderId, Long stationAdminId, Long dispatchAdminId);

    /**
     * 自动批量调度：
     * 对所有"可分配+已有分站"的订单批量执行手工调度逻辑
     */
    Result autoDispatch(Long dispatchAdminId);

    /**
     * 缺货订单激活：先校验库存，满足则将状态从 1(缺货) 改为 2(可分配)
     */
    Result activateOutOfStockOrder(Long orderId);

    /**
     * 任务单综合查询（联表 customer_order + customer）
     */
    Result searchTaskOrders(String taskNo, Byte taskStatus, Long stationId,
            String customerName, String mobile,
            String requireDateStart, String requireDateEnd);
}