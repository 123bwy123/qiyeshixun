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

    // 辅助方法：将前端的英文字符串角色转换为数据库的数字类型
    private Byte convertRoleStrToByte(String roleStr) {
        switch (roleStr) {
            case "admin": return 0;            // 系统管理员
            case "service": return 1;          // 客服人员
            case "dispatcher": return 2;       // 调度中心管理员
            // 👇 --- 【核心修复：一分为二的库房字典】 --- 👇
            case "center_warehouse": return 3; // 中心库房管理员
            case "station_warehouse": return 8; // 分站库房管理员
            // 👆 ------------------------------------- 👆
            case "courier": return 4;          // 配送员
            case "station_admin": return 5;    // 分站管理员
            case "center_admin": return 6;     // 配送中心管理员
            case "finance_admin": return 7;    // 财务中心管理员
            default: return -1;
        }
    }

    @Override
    public Result login(LoginDTO loginDTO) {
        String role = loginDTO.getRole();
        String username = loginDTO.getUsername();
        String encryptedPassword = loginDTO.getPassword();

        // 1. 客户登录逻辑
        if ("customer".equals(role)) {
            Customer customer = customerMapper.selectByMobile(username);
            if (customer == null) {
                return Result.error("客户账号(手机号)不存在");
            }
            if (!BCrypt.checkpw(encryptedPassword, customer.getPassword())) {
                return Result.error("密码错误");
            }
            return Result.success(customer.getId());
        }

        // 2. 内部员工登录逻辑
        else {
            SystemUser sysUser = systemUserMapper.selectByUsername(username);
            if (sysUser == null) {
                return Result.error("员工账号不存在");
            }
            if (!BCrypt.checkpw(encryptedPassword, sysUser.getPassword())) {
                return Result.error("密码错误");
            }

            // 【校验角色是否匹配！】
            Byte expectedRole = convertRoleStrToByte(role);
            if (sysUser.getRole() == null || !sysUser.getRole().equals(expectedRole)) {
                return Result.error("角色选择与实际职务不符，请确认您的角色！");
            }

            return Result.success(sysUser.getId());
        }
    }
}