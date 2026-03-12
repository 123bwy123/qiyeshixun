package com.itheima.qiyeshixun.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.mapper.SystemUserMapper;
import com.itheima.qiyeshixun.mapper.SysUserRoleMapper;
import com.itheima.qiyeshixun.po.SystemUser;
import com.itheima.qiyeshixun.service.SystemUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SystemUserServiceImpl implements SystemUserService {

    @Autowired
    private SystemUserMapper systemUserMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Override
    public Result<java.util.List<SystemUser>> getUserList() {
        List<SystemUser> list = systemUserMapper.selectAllUsers();
        list.forEach(user -> {
            user.setPassword(null);
            // 加载关联的角色ID
            user.setRoleIds(sysUserRoleMapper.selectRoleIdsByUserId(user.getId()));
        });
        return Result.success(list);
    }

    @Override
    public Result<Object> addUser(SystemUser user) {
        // 1. 检查账号是否重复
        if (systemUserMapper.selectByUsername(user.getUsername()) != null) {
            return Result.error("该员工账号已存在！");
        }
        // 2. BCrypt 加密
        String defaultPwd = user.getPassword() != null ? user.getPassword() : "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92";
        user.setPassword(BCrypt.hashpw(defaultPwd, BCrypt.gensalt()));

        // 如果分配了角色，同步第一个到旧 role 字段
        if (user.getRoleIds() != null && !user.getRoleIds().isEmpty()) {
            user.setRole(user.getRoleIds().get(0).byteValue());
        } else {
            user.setRole((byte) 0);
        }

        systemUserMapper.insertUser(user);
        return Result.success(user.getId());
    }

    @Override
    public Result<String> updateUser(SystemUser user) {
        // 同步角色到旧 role 字段，确保兼容性
        if (user.getRoleIds() != null && !user.getRoleIds().isEmpty()) {
            user.setRole(user.getRoleIds().get(0).byteValue());
        }
        systemUserMapper.updateUser(user);
        return Result.success("修改信息成功！");
    }

    @Override
    public Result<String> deleteUser(Long id) {
        systemUserMapper.deleteUserById(id);
        return Result.success("员工账号已停用！");
    }

    @Override
    @Transactional
    public Result<String> assignRoles(Long userId, List<Long> roleIds) {
        sysUserRoleMapper.deleteByUserId(userId);
        if (roleIds != null && !roleIds.isEmpty()) {
            roleIds.forEach(roleId -> sysUserRoleMapper.insert(userId, roleId));
            // 同步第一个角色到旧系统的 role 字段，确保兼容旧登录逻辑
            systemUserMapper.updateLegacyRole(userId, roleIds.get(0).byteValue());
        }
        return Result.success("角色分配成功");
    }
}