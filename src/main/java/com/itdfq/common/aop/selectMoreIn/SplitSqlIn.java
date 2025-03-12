package com.itdfq.common.aop.selectMoreIn;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 查分sql in 语句,并发分批执行
 * @author dfq 2025/3/12 14:03
 * @implNote
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SplitSqlIn {
    /**
     * 设置自定义线程池
     */
    ThreadPoolEnum threadPool();


    /**
     *返回值处理
     *
     */
    Class<? extends ResultProcessor> handlerReturnClass() default MergeFunction.class;

    /**
     * 超过多少开始拆分 >
     *
     * @return int
     */
    int splitLimit() default 1000;

    /**
     * 拆分后每组多少
     *
     * @return int
     */
    int splitGroupNum() default 1000;
}
