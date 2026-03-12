package com.itheima.qiyeshixun;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.itheima.qiyeshixun.mapper")
public class QiYeShiXunApplication {
    public static void main(String[] args) {
        SpringApplication.run(QiYeShiXunApplication.class, args);
    }
}