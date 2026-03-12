package com.itheima.qiyeshixun.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.List;
import java.util.Map;

@Mapper
public interface CenterWarehouseMapper {

    // 1. 查出大卡车在途的订单 (status = 2)
    @Select("SELECT po.id as orderId, po.purchase_no as purchaseNo, s.supplier_name as supplierName, po.total_amount as totalAmount " +
            "FROM purchase_order po " +
            "LEFT JOIN supplier s ON po.supplier_id = s.id " +
            "WHERE po.status = 2 AND po.del_flag = 0")
    List<Map<String, Object>> selectPendingReceipts();

    // 2. 防“缺胳膊少腿”核心：精准查出这笔订单到底买了什么，买了几件！
    @Select("SELECT product_id as productId, purchase_quantity as quantity FROM purchase_item WHERE purchase_id = #{orderId} AND del_flag = 0")
    List<Map<String, Object>> selectItemsByOrderId(Long orderId);

    // 3. 把采购明细里的“实收数量”填满 (actual_quantity = purchase_quantity)
    @Update("UPDATE purchase_item SET actual_quantity = purchase_quantity, update_time = NOW() WHERE purchase_id = #{orderId}")
    int updateItemActualQuantity(Long orderId);

    // 4. 终极奥义：给中心库房(假设 warehouse_id=1)对应的商品狂加库存！
    @Update("UPDATE inventory SET stock_quantity = stock_quantity + #{quantity}, update_time = NOW() " +
            "WHERE product_id = #{productId} AND warehouse_id = 1 AND del_flag = 0")
    int addInventoryStock(@org.apache.ibatis.annotations.Param("productId") Long productId, @org.apache.ibatis.annotations.Param("quantity") Integer quantity);

    // 5. 订单主表状态完结 (变成 3 已入库)
    @Update("UPDATE purchase_order SET status = 3, update_time = NOW() WHERE id = #{orderId}")
    int updateOrderStatusToReceived(Long orderId);
}