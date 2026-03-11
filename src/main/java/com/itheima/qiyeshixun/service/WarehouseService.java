package com.itheima.qiyeshixun.service;
import com.itheima.qiyeshixun.common.Result;

public interface WarehouseService {
    Result getPendingOutboundList();
    Result executeOutbound(Long transferId, Long adminId);
}