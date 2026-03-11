package com.itheima.qiyeshixun.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.dto.LoginDTO;
import com.itheima.qiyeshixun.mapper.CustomerMapper;
import com.itheima.qiyeshixun.mapper.SystemUserMapper;
import com.itheima.qiyeshixun.po.Customer;
import com.itheima.qiyeshixun.po.SystemUser;
import com.itheima.qiyeshixun.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private SystemUserMapper systemUserMapper;

    @Override
    public Result login(LoginDTO loginDTO) {
        String role = loginDTO.getRole();
        String username = loginDTO.getUsername();
        String encryptedPassword = loginDTO.getPassword(); // 前端传来的 SHA-256 密文

        // 1. 客户登录逻辑
        if ("customer".equals(role)) {
            Customer customer = customerMapper.selectByMobile(username);
            if (customer == null) {
                return Result.error("客户账号(手机号)不存在");
            }
            // 【核心修改】：使用 BCrypt.checkpw 进行密码比对 (参数1是明文/前端密文，参数2是数据库里的BCrypt密文)
            if (!BCrypt.checkpw(encryptedPassword, customer.getPassword())) {
                return Result.error("密码错误");
            }
            return Result.success("客户身份验证成功！准备进入客户主页...");
        }
        // 2. 内部员工登录逻辑 (咱们暂时还没写新增员工的接口，以后用到再说)
        else {
            SystemUser sysUser = systemUserMapper.selectByUsername(username);
            if (sysUser == null) {
                return Result.error("员工账号不存在");
            }
            // 同样也要用 BCrypt 校验
            if (!BCrypt.checkpw(encryptedPassword, sysUser.getPassword())) {
                return Result.error("密码错误");
            }
            return Result.success("内部员工身份验证成功！准备进入工作台...");
        }
    }
}