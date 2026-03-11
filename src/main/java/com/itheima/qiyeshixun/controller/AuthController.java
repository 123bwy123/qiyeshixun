package com.itheima.qiyeshixun.controller;

import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.dto.LoginDTO;
import com.itheima.qiyeshixun.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin // 临时解决前后端分离开发时的跨域问题
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 统一登录接口
     * 请求路径: POST http://localhost:8080/auth/login
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginDTO loginDTO) {
        // 简单的参数判空拦截
        if (loginDTO.getUsername() == null || loginDTO.getPassword() == null) {
            return Result.error("账号或密码不能为空");
        }
        // 调用 Service 层处理
        return authService.login(loginDTO);
    }
}