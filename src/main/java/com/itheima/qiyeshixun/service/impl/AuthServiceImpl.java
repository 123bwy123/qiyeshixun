package com.itheima.qiyeshixun.service.impl;

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
        // 1. 获取前端发来的信息
        String role = loginDTO.getRole();
        String username = loginDTO.getUsername();
        String encryptedPassword = loginDTO.getPassword(); // 这是前端SHA-256后的密文

        // 2. 双表分流策略：如果是客户角色，查 customer 表
        if ("customer".equals(role)) {
            Customer customer = customerMapper.selectByMobile(username);

            if (customer == null) {
                return Result.error("客户账号(手机号)不存在");
            }
            if (!customer.getPassword().equals(encryptedPassword)) {
                return Result.error("密码错误");
            }
            return Result.success("客户身份验证成功！(暂未颁发Token)");
        }

        // 3. 否则，都是内部角色，查 system_user 表
        else {
            SystemUser sysUser = systemUserMapper.selectByUsername(username);
            if (sysUser == null) {
                return Result.error("员工账号不存在");
            }
            if (!sysUser.getPassword().equals(encryptedPassword)) {
                return Result.error("密码错误");
            }
            // 这里可以加一段校验：前端选的角色，和数据库存的员工真实 role 是否匹配
            // if(sysUser.getRole() != 匹配的枚举值) return error("角色不匹配");

            return Result.success("内部员工身份验证成功！(暂未颁发Token)");
        }
    }
}