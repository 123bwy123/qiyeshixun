package com.itheima.qiyeshixun;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.itheima.qiyeshixun.mapper")
public class QiYeShiXunApplication {
    public static void main(String[] args) {
        SpringApplication.run(QiYeShiXunApplication.class, args);
    }
}