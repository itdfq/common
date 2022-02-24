package com.itdfq.common.redislock.annotation.support;

import com.itdfq.common.redislock.annotation.DisLock;
import com.itdfq.common.redislock.pojo.RedisLock;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.platform.commons.util.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author GodChin
 * @date 2022/2/23 16:36
 * @email 909256107@qq.com
 */
@Component
@Aspect
@Slf4j
public class DefaultDisLockAop implements DisLockAop {

    /***
     * spEL表达式 :Spring表达式语言（简称SpEl），一种强大的表达式语言，支持在运行时查询和操作对象。
     * ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
     * EvaluationContext evaluationContext = new MethodBasedEvaluationContext(null, method, args, parameterNameDiscoverer);
     * ExpressionParser expressionParser = new SpelExpressionParser();
     * expressionParser.parseExpression(expression).getValue(evaluationContext);
     *
     */
    private ParameterNameDiscoverer parameterNameDiscoverer;
    private ExpressionParser expressionParser;
    @Autowired
    private Redisson redisson;

    public DefaultDisLockAop() {
        expressionParser = new SpelExpressionParser();
        parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    }

    /**
     * @param joinPoint
     */
    @Around("@annotation(com.itdfq.common.redislock.annotation.DisLock)")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        Boolean isLock = null;
        RedisLock redisLock = getRedisLock(joinPoint);
        RLock rlock = redisson.getLock(redisLock.getKey());
        try {
            isLock = tryLock(rlock, redisLock);
            if (Boolean.TRUE.equals(isLock)) {
                //往下继续执行
                return joinPoint.proceed(joinPoint.getArgs());
            }
            return null;
        } finally {
            if (rlock != null) {
                rlock.unlock();
            }
        }
    }

    /**
     * 尝试获取锁
     *
     * @param rlock
     * @param redisLock
     * @return
     * @throws InterruptedException
     */
    protected Boolean tryLock(RLock rlock, RedisLock redisLock) throws InterruptedException {

        return rlock.tryLock(redisLock.getWaitMill(), redisLock.getExpireMill(), TimeUnit.MILLISECONDS);
    }

    private RedisLock getRedisLock(ProceedingJoinPoint joinPoint) {
        RedisLock lock = new RedisLock();
        Signature signature = joinPoint.getSignature();
        Method method = ((MethodSignature) signature).getMethod();
        DisLock disLock = getDisLock(joinPoint, method);
        Object[] args = joinPoint.getArgs();
        EvaluationContext evaluationContext = new MethodBasedEvaluationContext(null, method, args, parameterNameDiscoverer);
        String key = getKey(joinPoint, method, disLock, args, evaluationContext);
        lock.setKey(key);
        lock.setExpireMill(disLock.expire());
        lock.setThrowExceptionIfFailed(disLock.throwExceptionIfFailed());
        lock.setWaitMill(disLock.waitTime());
        return lock;
    }


    /**
     * 获取注解信息
     *
     * @param joinPoint
     * @param method
     * @return
     */
    protected DisLock getDisLock(ProceedingJoinPoint joinPoint, Method method) {
        return method.getAnnotation(DisLock.class);
    }


    /**
     * 获取key
     *
     * @param joinPoint
     * @param method
     * @param disLock
     * @param args
     * @param evaluationContext
     * @return
     */
    protected String getKey(ProceedingJoinPoint joinPoint, Method method, DisLock disLock, Object[] args, EvaluationContext evaluationContext) {
        return Optional.ofNullable(disLock.key())
                .filter(StringUtils::isNotBlank)
                .map(str -> parseExpression(evaluationContext, str))
                .orElse(getDefaultKey(joinPoint));
    }


    /**
     * 获取表达式
     *
     * @param evaluationContext
     * @param expression
     * @return
     */
    protected String parseExpression(EvaluationContext evaluationContext, String expression) {
        return expressionParser.parseExpression(expression).getValue(evaluationContext).toString();
    }
}