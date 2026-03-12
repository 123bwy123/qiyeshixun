package com.itheima.qiyeshixun.service;

import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.po.SysRole;
import java.util.List;

public interface SysRoleService {
    Result<List<SysRole>> getRoleList();
    Result<String> saveRole(SysRole role);
    Result<String> updateRole(SysRole role);
    Result<String> deleteRole(Long id);
    
    /**
     * 为角色分配权限
     */
    Result<String> assignPermissions(Long roleId, List<Long> permIds);
}
