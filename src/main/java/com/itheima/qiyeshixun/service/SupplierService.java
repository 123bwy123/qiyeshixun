package com.itheima.qiyeshixun.service;

import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.po.Supplier;
import java.util.List;

public interface SupplierService {
    /**
     * 分页查询供应商列表
     */
    Result<List<Supplier>> getSupplierPage(Integer pageNum, Integer pageSize, String name);

    /**
     * 新增供应商
     */
    Result<String> saveSupplier(Supplier supplier);

    /**
     * 修改供应商
     */
    Result<String> updateSupplier(Supplier supplier);

    /**
     * 删除供应商
     */
    Result<String> deleteSupplier(Long id);

    /**
     * 【供应商专属】查询待发货订单
     */
    Result<List<java.util.Map<String, Object>>> getPendingDispatchOrders();

    /**
     * 【供应商专属】执行确认发货
     */
    Result<String> dispatchOrder(Long orderId);
}
