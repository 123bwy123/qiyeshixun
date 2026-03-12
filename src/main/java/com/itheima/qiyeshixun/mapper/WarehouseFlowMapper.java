package com.itheima.qiyeshixun.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface WarehouseFlowMapper {

    // 记录库房流水历史 (入库、出库)
    // flow_type: 1=入库(购货等), 2=出库(调拨发货等)
    @Insert("INSERT INTO warehouse_flow(warehouse_id, product_id, flow_type, quantity, operator_id, related_no, remark, create_time, update_time, del_flag) "
            +
            "VALUES(#{warehouseId}, #{productId}, #{flowType}, #{quantity}, #{operatorId}, #{relatedNo}, #{remark}, NOW(), NOW(), 0)")
    int insertFlow(
            @Param("warehouseId") Long warehouseId,
            @Param("productId") Long productId,
            @Param("flowType") Byte flowType,
            @Param("quantity") Integer quantity,
            @Param("operatorId") Long operatorId,
            @Param("relatedNo") String relatedNo,
            @Param("remark") String remark);

    // 【报表】查询进出库流水历史
    @Select("<script>" +
            "SELECT f.*, w.warehouse_name as warehouseName, p.product_name as productName, u.username as operatorName " +
            "FROM warehouse_flow f " +
            "LEFT JOIN warehouse w ON f.warehouse_id = w.id " +
            "LEFT JOIN product p ON f.product_id = p.id " +
            "LEFT JOIN system_user u ON f.operator_id = u.id " +
            "WHERE f.del_flag = 0 " +
            "<if test='warehouseId != null'> AND f.warehouse_id = #{warehouseId} </if> " +
            "<if test='flowType != null'> AND f.flow_type = #{flowType} </if> " +
            "<if test='relatedNo != null and relatedNo != \"\"'> AND f.related_no LIKE CONCAT('%', #{relatedNo}, '%') </if> " +
            "ORDER BY f.create_time DESC" +
            "</script>")
    List<Map<String, Object>> selectStockFlowReport(@Param("warehouseId") Long warehouseId, @Param("flowType") Byte flowType, @Param("relatedNo") String relatedNo);
}
