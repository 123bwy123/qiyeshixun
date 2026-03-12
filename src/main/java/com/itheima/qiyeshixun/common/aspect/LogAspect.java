package com.itheima.qiyeshixun.common.aspect;

import cn.hutool.core.util.URLUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONUtil;
import com.itheima.qiyeshixun.common.annotation.Log;
import com.itheima.qiyeshixun.po.SysLog;
import com.itheima.qiyeshixun.service.SysLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

@Aspect
@Component
public class LogAspect {

    @Autowired
    private SysLogService sysLogService;

    @Pointcut("@annotation(com.itheima.qiyeshixun.common.annotation.Log)")
    public void logPointCut() {}

    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        Object result = null;
        Exception exc = null;
        try {
            result = point.proceed();
            return result;
        } catch (Exception e) {
            exc = e;
            throw e;
        } finally {
            long time = System.currentTimeMillis() - beginTime;
            saveLog(point, result, exc, time);
        }
    }

    private void saveLog(ProceedingJoinPoint joinPoint, Object result, Exception e, long time) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Log logAnnotation = method.getAnnotation(Log.class);

        SysLog sysLog = new SysLog();
        if (logAnnotation != null) {
            sysLog.setModule(logAnnotation.module());
            sysLog.setOperation(logAnnotation.operation());
        }

        // 方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        sysLog.setMethod(className + "." + methodName + "()");

        // 参数
        Object[] args = joinPoint.getArgs();
        try {
            String params = JSONUtil.toJsonStr(args);
            sysLog.setParams(params);
        } catch (Exception ex) {
            sysLog.setParams("解析参数失败");
        }

        // 设置 IP 等信息 (模拟获取)
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            sysLog.setIp(request.getRemoteAddr());
            // 假设从 Session/Header 获取操作人
            sysLog.setOperator(request.getHeader("X-Operator-Name") != null ? request.getHeader("X-Operator-Name") : "admin");
        }

        // 结果与状态
        if (e != null) {
            sysLog.setType(1); // 异常
            sysLog.setResult(e.getMessage());
        } else {
            sysLog.setType(0); // 正常
            if (result != null) {
                sysLog.setResult(JSONUtil.toJsonStr(result));
            }
        }
        
        sysLog.setTime(time);
        
        // 异步保存 (这里简单演示直接保存，实际项目可用线程池)
        sysLogService.saveLog(sysLog);
    }
}
