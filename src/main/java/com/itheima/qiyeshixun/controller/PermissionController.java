package com.itheima.qiyeshixun.controller;

import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.po.SysPermission;
import com.itheima.qiyeshixun.service.SysPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/sys/permission")
@CrossOrigin
public class PermissionController {

    @Autowired
    private SysPermissionService sysPermissionService;

    @GetMapping("/tree")
    public Result<List<SysPermission>> tree() {
        return sysPermissionService.getPermissionTree();
    }

    @GetMapping("/role/{roleId}")
    public Result<List<Long>> getPermIdsByRoleId(@PathVariable Long roleId) {
        return sysPermissionService.getPermIdsByRoleId(roleId);
    }
}
