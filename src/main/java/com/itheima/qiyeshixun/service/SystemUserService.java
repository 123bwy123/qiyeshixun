package com.itheima.qiyeshixun.service;
import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.po.SystemUser;

public interface SystemUserService {
    Result<java.util.List<SystemUser>> getUserList();
    Result<Object> addUser(SystemUser user);
    Result<String> updateUser(SystemUser user);
    Result<String> deleteUser(Long id);
    Result<String> assignRoles(Long userId, java.util.List<Long> roleIds);
}