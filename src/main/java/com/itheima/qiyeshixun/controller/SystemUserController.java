package com.itheima.qiyeshixun.controller;

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
    public Result<Object> add(@RequestBody SystemUser user) { return systemUserService.addUser(user); }

    @PutMapping("/update")
    public Result<String> update(@RequestBody SystemUser user) { return systemUserService.updateUser(user); }

    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id) { return systemUserService.deleteUser(id); }

    @PostMapping("/assignRoles")
    public Result<String> assignRoles(@RequestParam Long userId, @RequestBody java.util.List<Long> roleIds) {
        return systemUserService.assignRoles(userId, roleIds);
    }
}