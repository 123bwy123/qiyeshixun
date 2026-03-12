package com.itheima.qiyeshixun.mapper;

import com.itheima.qiyeshixun.po.SysRole;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface SysRoleMapper {
    @Select("SELECT * FROM sys_role WHERE del_flag = 0")
    List<SysRole> selectAll();

    @Select("SELECT * FROM sys_role WHERE id = #{id} AND del_flag = 0")
    SysRole selectById(Long id);

    @Insert("INSERT INTO sys_role(role_name, role_key, is_builtin, create_time, update_time) " +
            "VALUES(#{roleName}, #{roleKey}, #{isBuiltin}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SysRole role);

    @Update("UPDATE sys_role SET role_name=#{roleName}, role_key=#{roleKey}, update_time=NOW() WHERE id=#{id}")
    int update(SysRole role);

    @Update("UPDATE sys_role SET del_flag = 1 WHERE id = #{id} AND is_builtin = 0")
    int deleteById(Long id);

    @Select("SELECT r.* FROM sys_role r JOIN sys_user_role ur ON r.id = ur.role_id WHERE ur.user_id = #{userId} AND r.del_flag = 0")
    List<SysRole> selectRolesByUserId(Long userId);
}
