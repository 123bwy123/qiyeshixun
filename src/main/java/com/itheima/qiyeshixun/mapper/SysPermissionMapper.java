package com.itheima.qiyeshixun.mapper;

import com.itheima.qiyeshixun.po.SysPermission;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface SysPermissionMapper {
        @Select("SELECT * FROM sys_permission WHERE del_flag = 0 ORDER BY order_num")
        List<SysPermission> selectAll();

        @Select("SELECT p.* FROM sys_permission p JOIN sys_role_permission rp ON p.id = rp.perm_id WHERE rp.role_id = #{roleId} AND p.del_flag = 0")
        List<SysPermission> selectPermissionsByRoleId(Long roleId);

        @Insert("INSERT INTO sys_permission(name, parent_id, type, perm_key, route_path, component_path, icon, order_num) "
                        +
                        "VALUES(#{name}, #{parentId}, #{type}, #{permKey}, #{routePath}, #{componentPath}, #{icon}, #{orderNum})")
        int insert(SysPermission permission);

        @Select("SELECT p.* FROM sys_permission p JOIN sys_role_permission rp ON p.id = rp.perm_id " +
                        "JOIN sys_user_role ur ON rp.role_id = ur.role_id WHERE ur.user_id = #{userId} AND p.del_flag = 0")
        List<SysPermission> selectPermissionsByUserId(Long userId);
}
