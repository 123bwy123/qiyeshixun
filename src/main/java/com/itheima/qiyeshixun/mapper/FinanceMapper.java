package com.itheima.qiyeshixun.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

@Mapper
public interface FinanceMapper {

    // 1. 查出大仓“已入库”等待财务打款的采购单 (status = 3)
    @Select("SELECT po.id as orderId, po.purchase_no as purchaseNo, s.supplier_name as supplierName, " +
            "po.total_amount as totalAmount, po.update_time as receiveDate " +
            "FROM purchase_order po " +
            "LEFT JOIN supplier s ON po.supplier_id = s.id " +
            "WHERE po.status = 3 AND po.del_flag = 0 " +
            "ORDER BY po.update_time DESC")
    List<Map<String, Object>> selectPendingSupplierSettlements();

    // 2. 打款成功，采购单状态变 4 (已结清)
    @Update("UPDATE purchase_order SET status = 4, update_time = NOW() WHERE id = #{orderId}")
    int updatePurchaseOrderToSettled(Long orderId);

    // 3. 核心：录入发票！(严格按照你的字段：invoice_no, amount, register_date, status, finance_admin_id)
    @Insert("INSERT INTO invoice (invoice_no, amount, register_date, status, finance_admin_id, del_flag) " +
            "VALUES (#{invoiceNo}, #{amount}, NOW(), 1, #{adminId}, 0)")
    int insertInvoice(@Param("invoiceNo") String invoiceNo,
                      @Param("amount") Double amount,
                      @Param("adminId") Long adminId);

    // ================= 以下为内部分润结算专用 =================

    // 1. 查出已签收 (状态 7) 等待财务分账的客户订单
    @Select("SELECT co.id as orderId, co.order_no as orderNo, co.total_amount as totalAmount, " +
            "co.finish_time as finishTime, t.task_no as taskNo, t.station_id as stationId " +
            "FROM customer_order co " +
            "LEFT JOIN task_order t ON co.id = t.order_id " +
            "WHERE co.order_status = 7 AND co.del_flag = 0")
    List<Map<String, Object>> selectPendingInternalSettlements();

    // 2. 将分站的提成分润，写入 task_order 的 settlement_amount 字段中！
    @Update("UPDATE task_order SET settlement_amount = #{amount}, update_time = NOW() WHERE order_id = #{orderId}")
    int updateTaskSettlementAmount(@org.apache.ibatis.annotations.Param("orderId") Long orderId, @org.apache.ibatis.annotations.Param("amount") Double amount);

    // 3. 客户主订单彻底封账归档 (状态变为 8)
    @Update("UPDATE customer_order SET order_status = 8, update_time = NOW() WHERE id = #{orderId}")
    int updateCustomerOrderToArchived(Long orderId);
}