package com.itheima.qiyeshixun.controller;

import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.po.SysRole;
import com.itheima.qiyeshixun.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/sys/role")
@CrossOrigin
public class RoleController {

    @Autowired
    private SysRoleService sysRoleService;

    @GetMapping("/list")
    public Result<List<SysRole>> list() {
        return sysRoleService.getRoleList();
    }

    @PostMapping("/save")
    public Result<String> save(@RequestBody SysRole role) {
        return sysRoleService.saveRole(role);
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody SysRole role) {
        return sysRoleService.updateRole(role);
    }

    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id) {
        return sysRoleService.deleteRole(id);
    }

    @PostMapping("/assign")
    public Result<String> assignPermissions(@RequestParam Long roleId, @RequestBody List<Long> permIds) {
        return sysRoleService.assignPermissions(roleId, permIds);
    }
}
