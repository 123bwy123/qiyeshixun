package com.itheima.qiyeshixun.controller;

import com.itheima.qiyeshixun.common.annotation.Log;
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

    @PostMapping("/add")
    @Log(module = "权限管理", operation = "新增角色")
    public Result<String> add(@RequestBody SysRole role) { return sysRoleService.saveRole(role); }

    @PutMapping("/update")
    @Log(module = "权限管理", operation = "修改角色")
    public Result<String> update(@RequestBody SysRole role) { return sysRoleService.updateRole(role); }

    @DeleteMapping("/delete/{id}")
    @Log(module = "权限管理", operation = "删除角色")
    public Result<String> delete(@PathVariable Long id) { return sysRoleService.deleteRole(id); }

    @PostMapping("/assignPermissions")
    @Log(module = "权限管理", operation = "分配角色菜单权限")
    public Result<String> assignPermissions(@RequestParam Long roleId, @RequestBody java.util.List<Long> permissionIds) {
        return sysRoleService.assignPermissions(roleId, permissionIds);
    }
}
