package com.itheima.qiyeshixun.service;

import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.po.SysLog;
import java.util.List;

public interface SysLogService {
    void saveLog(SysLog sysLog);
    Result<List<SysLog>> getLogList(String operator, Integer type);
    void backupLogs();
}
