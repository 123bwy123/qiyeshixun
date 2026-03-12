package com.itheima.qiyeshixun.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

public interface InspectionOrderMapper {

    // 生成验货单（供分站验货使用）
    @Insert("INSERT INTO inspection_order(inspection_no, transfer_id, station_id, admin_id, inspection_status, create_time, update_time, del_flag) "
            +
            "VALUES(#{inspectionNo}, #{transferId}, #{stationId}, #{adminId}, 0, NOW(), NOW(), 0)")
    int insertInspectionOrder(
            @Param("inspectionNo") String inspectionNo,
            @Param("transferId") Long transferId,
            @Param("stationId") Long stationId,
            @Param("adminId") Long adminId);

    // 查询所有分站待入库的【验货单】(关联查询被调拨的商品信息及数量)
    @Select("<script>" +
            "SELECT i.id as inspectionId, i.inspection_no as inspectionNo, i.station_id as stationId, i.transfer_id as transferId, i.create_time as createTime, " +
            "t.transfer_no as transferNo, c.order_no as customerOrderNo, " +
            "oi.product_id as productId, p.product_name as productName, oi.quantity as expectedQty " +
            "FROM inspection_order i " +
            "JOIN transfer_order t ON i.transfer_id = t.id " +
            "JOIN customer_order c ON t.order_id = c.id " +
            "JOIN order_item oi ON c.id = oi.order_id " +
            "JOIN product p ON oi.product_id = p.id " +
            "WHERE i.inspection_status = 0 AND i.del_flag = 0 " +
            "<if test='stationId != null'>AND i.station_id = #{stationId}</if> " +
            "ORDER BY i.create_time DESC" +
            "</script>")
    List<Map<String, Object>> selectPendingInspections(@Param("stationId") Long stationId);

    // 更新验货单状态
    @Update("UPDATE inspection_order SET inspection_status = #{status}, update_time = NOW() WHERE id = #{id}")
    int updateInspectionStatus(@Param("id") Long id, @Param("status") Byte status);

    // 获取单条验货单明细（用于入库时的反查商品与订单号）
    @Select("SELECT i.id as inspectionId, i.station_id as stationId, i.transfer_id as transferId, " +
            "c.id as orderId, oi.product_id as productId, oi.quantity as expectedQty " +
            "FROM inspection_order i " +
            "JOIN transfer_order t ON i.transfer_id = t.id " +
            "JOIN customer_order c ON t.order_id = c.id " +
            "JOIN order_item oi ON c.id = oi.order_id " +
            "WHERE i.id = #{inspectionId} AND i.del_flag = 0 LIMIT 1")
    Map<String, Object> selectInspectionDetail(@Param("inspectionId") Long inspectionId);
}
