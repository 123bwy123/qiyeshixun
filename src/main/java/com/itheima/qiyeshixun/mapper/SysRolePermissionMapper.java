package com.itheima.qiyeshixun.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface SysRolePermissionMapper {
    @Insert("INSERT INTO sys_role_permission(role_id, perm_id) VALUES(#{roleId}, #{permId})")
    int insert(@Param("roleId") Long roleId, @Param("permId") Long permId);

    @Delete("DELETE FROM sys_role_permission WHERE role_id = #{roleId}")
    int deleteByRoleId(Long roleId);

    @Select("SELECT perm_id FROM sys_role_permission WHERE role_id = #{roleId}")
    List<Long> selectPermIdsByRoleId(Long roleId);
}
