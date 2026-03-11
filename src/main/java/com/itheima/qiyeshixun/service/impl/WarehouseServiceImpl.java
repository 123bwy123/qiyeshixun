package com.itheima.qiyeshixun.service.impl;

import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.mapper.CustomerOrderMapper;
import com.itheima.qiyeshixun.mapper.TransferOrderMapper;
import com.itheima.qiyeshixun.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WarehouseServiceImpl implements WarehouseService {

    @Autowired
    private TransferOrderMapper transferOrderMapper;

    @Override
    public Result getPendingOutboundList() {
        return Result.success(transferOrderMapper.selectPendingOutbound());
    }

    @Autowired
    private CustomerOrderMapper customerOrderMapper;
    @Override
    public Result executeOutbound(Long transferId, Long adminId) {
        // 1. 调拨单出库确认
        int rows = transferOrderMapper.confirmOutbound(transferId, adminId);

        if (rows > 0) {
            // ================== 【真正的核心新增】 ==================
            // 2. 查出这个调拨单属于哪个客户订单
            Long orderId = transferOrderMapper.getOrderIdById(transferId);

            // 3. 把那个客户订单的状态，严丝合缝地改成 3 (中心库已出库)
            if (orderId != null) {
                customerOrderMapper.updateStatusToOutbound(orderId);
            }
            // =======================================================

            return Result.success("货物已成功打包装车，调拨出库完成！");
        }
        return Result.error("出库失败，单据异常");
    }
}