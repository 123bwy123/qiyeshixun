package com.itheima.qiyeshixun.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.mapper.SystemUserMapper;
import com.itheima.qiyeshixun.po.SystemUser;
import com.itheima.qiyeshixun.service.SystemUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SystemUserServiceImpl implements SystemUserService {

    @Autowired
    private SystemUserMapper systemUserMapper;

    @Override
    public Result getUserList() {
        List<SystemUser> list = systemUserMapper.selectAllUsers();
        // 出于安全考虑，传给前端的列表里，把密码抹除掉
        list.forEach(user -> user.setPassword(null));
        return Result.success(list);
    }

    @Override
    public Result addUser(SystemUser user) {
        // 1. 检查账号是否重复
        if (systemUserMapper.selectByUsername(user.getUsername()) != null) {
            return Result.error("该员工账号已存在！");
        }
        // 2. BCrypt 加密 (默认密码暂定为 123456 的 SHA-256 密文，然后再加盐存入)
        // 8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92 是 123456
        String defaultPwd = user.getPassword() != null ? user.getPassword() : "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92";
        user.setPassword(BCrypt.hashpw(defaultPwd, BCrypt.gensalt()));

        systemUserMapper.insertUser(user);
        return Result.success("新增员工成功！");
    }

    @Override
    public Result updateUser(SystemUser user) {
        systemUserMapper.updateUser(user);
        return Result.success("修改信息成功！");
    }

    @Override
    public Result deleteUser(Integer id) {
        systemUserMapper.deleteUserById(id);
        return Result.success("员工账号已停用！");
    }
}