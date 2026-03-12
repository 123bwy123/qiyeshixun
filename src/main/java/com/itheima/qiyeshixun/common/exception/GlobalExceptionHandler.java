package com.itheima.qiyeshixun.common.exception;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.json.JSONUtil;
import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.po.SysLog;
import com.itheima.qiyeshixun.service.SysLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private SysLogService sysLogService;

    /**
     * 系统 500 异常拦截并记录日志
     */
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e, HttpServletRequest request) {
        // 1. 打印日志到后台
        e.printStackTrace();

        // 2. 存入 sys_log 数据库 (属于 type=1 异常日志)
        SysLog sysLog = new SysLog();
        sysLog.setType(1);
        sysLog.setModule("系统异常");
        sysLog.setOperation("未捕获异常抛出");
        sysLog.setMethod(request.getMethod() + " " + request.getRequestURI());
        sysLog.setParams(JSONUtil.toJsonStr(request.getParameterMap()));
        sysLog.setIp(request.getRemoteAddr());
        sysLog.setOperator(request.getHeader("X-Operator-Name") != null ? request.getHeader("X-Operator-Name") : "system");
        
        // 关键：保留错误堆栈供审计
        String stackTrace = ExceptionUtil.stacktraceToString(e);
        sysLog.setResult(stackTrace.length() > 2000 ? stackTrace.substring(0, 2000) : stackTrace);
        
        sysLogService.saveLog(sysLog);

        // 3. 返回给前端友好提示
        return Result.error("服务器内部错误，请联系管理员或查看系统日志！详情：" + e.getMessage());
    }
}
