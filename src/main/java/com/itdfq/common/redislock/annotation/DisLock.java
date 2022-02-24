package com.itdfq.common.redislock.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author GodChin
 * @date 2022/2/23 16:30
 * @email 909256107@qq.com
 * 分布式锁的注解
 */
@Target(value = {ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface DisLock {
    /**
     * key，如果为空则默认"类名+方法名"
     *
     */
    String key() default "";


    /**
     * 默认key过期时间
     */
    long expire() default 600;

    /**
     * 获取锁的等到时长
     * 默认10S
     */
    long waitTime() default 10000;

    /**
     * 获取不到锁是否要抛异常，如果不抛异常，获取锁失败结果会返回null
     *
     */
    boolean throwExceptionIfFailed() default true;
}
