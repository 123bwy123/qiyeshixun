package com.itheima.qiyeshixun.service;
import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.po.SystemUser;

public interface SystemUserService {
    Result getUserList();
    Result addUser(SystemUser user);
    Result updateUser(SystemUser user);
    Result deleteUser(Integer id);
}