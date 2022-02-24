package com.itdfq.common.redislock.annotation.support;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.PriorityOrdered;

import java.lang.reflect.Method;

/**
 * @author GodChin
 * @date 2022/2/23 16:34
 * @email 909256107@qq.com
 */
public interface DisLockAop extends PriorityOrdered {
    /**
     * 默认分隔符
     */
    String DEFAULT_KEY_DELIMITER = ":";

    /**
     * 默认最低优先级
     * @return
     */
    @Override
    default int getOrder() {
        return PriorityOrdered.LOWEST_PRECEDENCE;
    }

    /**
     * 默认key
     *
     */
    default String getDefaultKey(ProceedingJoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        Method method = ((MethodSignature) signature).getMethod();
        return method.getDeclaringClass().getName() + DEFAULT_KEY_DELIMITER + method.getName();
    }

    /**
     * 默认值
     *
     */
    default String getDefaultValue(ProceedingJoinPoint joinPoint) {
        return String.valueOf(System.currentTimeMillis());
    }
}


