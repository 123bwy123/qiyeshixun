package com.itheima.qiyeshixun.controller;

import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.po.SysLog;
import com.itheima.qiyeshixun.service.SysLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/admin/sys/log")
@CrossOrigin
public class SysLogController {

    @Autowired
    private SysLogService sysLogService;

    @GetMapping("/list")
    public Result<List<SysLog>> list(@RequestParam(required = false) String operator,
                                     @RequestParam(required = false) Integer type) {
        return sysLogService.getLogList(operator, type);
    }
}
