package com.itheima.qiyeshixun.common.scheduler;

import com.itheima.qiyeshixun.service.SysLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 日志自动备份任务
 */
@Component
public class LogBackupScheduler {

    private static final Logger log = LoggerFactory.getLogger(LogBackupScheduler.class);

    @Autowired
    private SysLogService sysLogService;

    /**
     * 每月 1 号凌晨 2 点执行：将 30 天前的日志移动到备份表并清理
     * Cron: 0 0 2 1 * ?
     */
    @Scheduled(cron = "0 0 2 1 * ?")
    public void autoBackupLogs() {
        log.info("开始执行系统日志自动备份任务...");
        try {
            sysLogService.backupLogs();
            log.info("日志自动备份任务执行成功。");
        } catch (Exception e) {
            log.error("日志自动备份任务执行失败：", e);
        }
    }
}
