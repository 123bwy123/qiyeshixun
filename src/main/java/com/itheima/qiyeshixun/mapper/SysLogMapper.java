package com.itheima.qiyeshixun.mapper;

import com.itheima.qiyeshixun.po.SysLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SysLogMapper {
    
    @Insert("INSERT INTO sys_log(operator, module, operation, method, params, result, time, ip, type) " +
            "VALUES(#{operator}, #{module}, #{operation}, #{method}, #{params}, #{result}, #{time}, #{ip}, #{type})")
    int insert(SysLog sysLog);

    @Select("<script>" +
            "SELECT * FROM sys_log " +
            "WHERE 1=1 " +
            "<if test='operator != null and operator != \"\"'> AND operator LIKE CONCAT('%', #{operator}, '%') </if>" +
            "<if test='type != null'> AND type = #{type} </if>" +
            "ORDER BY create_time DESC" +
            "</script>")
    List<SysLog> selectLogs(@Param("operator") String operator, @Param("type") Integer type);

    @Insert("INSERT INTO sys_log_backup SELECT * FROM sys_log WHERE create_time < DATE_SUB(NOW(), INTERVAL 30 DAY)")
    int backupLogs();

    @org.apache.ibatis.annotations.Delete("DELETE FROM sys_log WHERE create_time < DATE_SUB(NOW(), INTERVAL 30 DAY)")
    int deleteOldLogs();
}
