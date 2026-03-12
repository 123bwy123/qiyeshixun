package com.itheima.qiyeshixun.service.impl;

import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.mapper.SysPermissionMapper;
import com.itheima.qiyeshixun.mapper.SysRolePermissionMapper;
import com.itheima.qiyeshixun.po.SysPermission;
import com.itheima.qiyeshixun.service.SysPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysPermissionServiceImpl implements SysPermissionService {

    @Autowired
    private SysPermissionMapper sysPermissionMapper;

    @Autowired
    private SysRolePermissionMapper sysRolePermissionMapper;

    @Override
    public Result<List<SysPermission>> getPermissionTree() {
        List<SysPermission> allData = sysPermissionMapper.selectAll();
        return Result.success(buildTree(allData, 0L));
    }

    @Override
    public Result<List<Long>> getPermIdsByRoleId(Long roleId) {
        return Result.success(sysRolePermissionMapper.selectPermIdsByRoleId(roleId));
    }

    /**
     * 递归构建树形结构
     */
    private List<SysPermission> buildTree(List<SysPermission> list, Long parentId) {
        return list.stream()
                .filter(item -> item.getParentId().equals(parentId))
                .map(item -> {
                    item.setChildren(buildTree(list, item.getId()));
                    return item;
                })
                .collect(Collectors.toList());
    }
}
