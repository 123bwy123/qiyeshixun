package com.itheima.qiyeshixun.service.impl;

import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.mapper.SysRoleMapper;
import com.itheima.qiyeshixun.mapper.SysRolePermissionMapper;
import com.itheima.qiyeshixun.po.SysRole;
import com.itheima.qiyeshixun.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SysRoleServiceImpl implements SysRoleService {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysRolePermissionMapper sysRolePermissionMapper;

    @Override
    public Result<List<SysRole>> getRoleList() {
        return Result.success(sysRoleMapper.selectAll());
    }

    @Override
    public Result<String> saveRole(SysRole role) {
        role.setIsBuiltin(0); // 自定义角色
        sysRoleMapper.insert(role);
        return Result.success("新增角色成功");
    }

    @Override
    public Result<String> updateRole(SysRole role) {
        SysRole exist = sysRoleMapper.selectById(role.getId());
        if (exist != null && exist.getIsBuiltin() == 1) {
            // 内置角色不允许修改关键信息，或者只允许修改特定属性
            return Result.error("内置核心角色禁止修改信息！");
        }
        sysRoleMapper.update(role);
        return Result.success("更新角色成功");
    }

    @Override
    public Result<String> deleteRole(Long id) {
        SysRole exist = sysRoleMapper.selectById(id);
        if (exist != null && exist.getIsBuiltin() == 1) {
            return Result.error("内置核心角色禁止删除！");
        }
        sysRoleMapper.deleteById(id);
        return Result.success("删除角色成功");
    }

    @Override
    @Transactional
    public Result<String> assignPermissions(Long roleId, List<Long> permIds) {
        // 先清空原有关联
        sysRolePermissionMapper.deleteByRoleId(roleId);
        // 重新添加新关联
        if (permIds != null && !permIds.isEmpty()) {
            for (Long permId : permIds) {
                sysRolePermissionMapper.insert(roleId, permId);
            }
        }
        return Result.success("权限分配成功");
    }
}
