package com.itheima.qiyeshixun.dto;

public class LoginDTO {
    private String role;       // 前端传来的角色: customer, service, dispatcher 等
    private String username;   // 账号（客户填手机号，员工填工号）
    private String password;   // 密码密文
    private Boolean rememberMe;// 记住我


    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Boolean getRememberMe() { return rememberMe; }
    public void setRememberMe(Boolean rememberMe) { this.rememberMe = rememberMe; }
}