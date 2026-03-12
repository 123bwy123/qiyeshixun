package com.itheima.qiyeshixun.common.annotation;

import java.lang.annotation.*;

/**
 * 自定义操作日志记录注解
 */
@Target({ ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
    /** 模块 */
    String module() default "";

    /** 功能 */
    String operation() default "";
}
