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
    public Result getList() { return systemUserService.getUserList(); }

    @PostMapping("/add")
    public Result add(@RequestBody SystemUser user) { return systemUserService.addUser(user); }

    @PutMapping("/update")
    public Result update(@RequestBody SystemUser user) { return systemUserService.updateUser(user); }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) { return systemUserService.deleteUser(id); }
}