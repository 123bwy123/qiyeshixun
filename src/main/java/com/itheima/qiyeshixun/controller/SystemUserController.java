package com.itheima.qiyeshixun.controller;

import com.itheima.qiyeshixun.common.annotation.Log;
import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.po.SystemUser;
import com.itheima.qiyeshixun.service.SystemUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/user")
@CrossOrigin // 允许跨域
public class SystemUserController {

    @Autowired
    private SystemUserService systemUserService;

    @GetMapping("/list")
    public Result<java.util.List<SystemUser>> getList() { return systemUserService.getUserList(); }

    @PostMapping("/add")
    @Log(module = "系统管理", operation = "新增员工")
    public Result<Object> add(@RequestBody SystemUser user) { return systemUserService.addUser(user); }

    @PutMapping("/update")
    @Log(module = "系统管理", operation = "修改员工信息")
    public Result<String> update(@RequestBody SystemUser user) { return systemUserService.updateUser(user); }

    @DeleteMapping("/delete/{id}")
    @Log(module = "系统管理", operation = "停用员工账号")
    public Result<String> delete(@PathVariable Long id) { return systemUserService.deleteUser(id); }

    @PostMapping("/assignRoles")
    @Log(module = "系统管理", operation = "分配角色权限")
    public Result<String> assignRoles(@RequestParam Long userId, @RequestBody java.util.List<Long> roleIds) {
        return systemUserService.assignRoles(userId, roleIds);
    }
}