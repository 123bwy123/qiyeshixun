package com.itheima.qiyeshixun.controller;

import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.mapper.FinanceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/finance")
@CrossOrigin // 绝不让你报跨域错误！
public class FinanceController {

    @Autowired
    private FinanceMapper financeMapper;

    @GetMapping("/pendingSupplier")
    public Result getPendingSupplier() {
        return Result.success(financeMapper.selectPendingSupplierSettlements());
    }

    // 💰 终极结算接口 (打款 + 录发票 同生共死)
    @PostMapping("/settleSupplier")
    @Transactional(rollbackFor = Exception.class)
    public Result settleSupplier(@RequestBody Map<String, Object> params) {
        Long orderId = Long.valueOf(params.get("orderId").toString());
        String invoiceNo = params.get("invoiceNo").toString();
        Double amount = Double.valueOf(params.get("amount").toString());
        Long adminId = Long.valueOf(params.get("adminId").toString()); // 谁录入的发票

        // 1. 采购单状态变为已结清
        financeMapper.updatePurchaseOrderToSettled(orderId);

        // 2. 发票物理落库 (状态设为 1: 已登记)
        financeMapper.insertInvoice(invoiceNo, amount, adminId);

        return Result.success("✅ 货款已安全打入供应商对公账户！发票 [" + invoiceNo + "] 录入归档成功！");
    }

    // ================= 以下为内部分润结算接口 =================

    // 获取等待内部结算的订单列表
    @GetMapping("/pendingInternal")
    public Result getPendingInternal() {
        return Result.success(financeMapper.selectPendingInternalSettlements());
    }

    // 💸 终极内部结算接口 (计算提成 + 封账归档)
    @PostMapping("/settleInternal")
    @Transactional(rollbackFor = Exception.class) // 同样必须加事务！绝不允许钱分了但单子没关！
    public Result settleInternal(@RequestParam Long orderId, @RequestParam Double totalAmount) {

        // 核心商业逻辑：分站拿 20% 作为落地配送服务费，剩下 80% 归中心库房和总公司
        Double stationCut = totalAmount * 0.20;

        // 1. 记录分站应得分润到 task_order
        financeMapper.updateTaskSettlementAmount(orderId, stationCut);

        // 2. 主订单彻底封账归档 (状态 8)
        financeMapper.updateCustomerOrderToArchived(orderId);

        return Result.success("💰 内部结算完毕！分站配送费 (¥" + stationCut + ") 已入账，主订单彻底封账归档！");
    }
}