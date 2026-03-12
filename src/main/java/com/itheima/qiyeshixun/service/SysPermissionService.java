package com.itheima.qiyeshixun.service;

import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.po.SysPermission;
import java.util.List;

public interface SysPermissionService {
    /**
     * 获取完整的权限树 (用于权限管理/分配)
     */
    Result<List<SysPermission>> getPermissionTree();

    /**
     * 获取某角色的权限ID列表
     */
    Result<List<Long>> getPermIdsByRoleId(Long roleId);
}
