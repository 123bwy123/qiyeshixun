package com.itheima.qiyeshixun.service.impl;

import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.mapper.SysLogMapper;
import com.itheima.qiyeshixun.po.SysLog;
import com.itheima.qiyeshixun.service.SysLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class SysLogServiceImpl implements SysLogService {

    @Autowired
    private SysLogMapper sysLogMapper;

    @Override
    public void saveLog(SysLog sysLog) {
        sysLogMapper.insert(sysLog);
    }

    @Override
    public Result<List<SysLog>> getLogList(String operator, Integer type) {
        return Result.success(sysLogMapper.selectLogs(operator, type));
    }

    @Override
    @Transactional
    public void backupLogs() {
        sysLogMapper.backupLogs();
        sysLogMapper.deleteOldLogs();
    }
}
