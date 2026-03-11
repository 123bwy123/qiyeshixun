package com.itheima.qiyeshixun.service;
import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.dto.LoginDTO;

public interface AuthService {
    Result login(LoginDTO loginDTO);
}