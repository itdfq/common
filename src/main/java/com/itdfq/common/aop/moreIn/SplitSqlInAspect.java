package com.itdfq.common.aop.moreIn;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author dfq 2025/3/12 14:08
 * @implNote
 */
@Slf4j
@Aspect
@Component
public class SplitSqlInAspect {

    private static final String LOG_PREFIX = "自动拆分in语句";
    @Resource
    private Map<ThreadPoolEnum, ThreadPoolExecutor> threadPools;


    @Around("@annotation(splitSqlIn)")
    public Object around(ProceedingJoinPoint point, SplitSqlIn splitSqlIn) throws Throwable {
        Signature signature = point.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method targetMethod = methodSignature.getMethod();
        Object[] args = point.getArgs();
        int splitLimit = splitSqlIn.splitLimit();
        int splitGroupNum = splitSqlIn.splitGroupNum();
        if (args == null || args.length == 0) {
            return point.proceed();
        }
        int needSplitParamIndex = -1;
        for (int i = 0; i < targetMethod.getParameters().length; i++) {
            Parameter parameter = targetMethod.getParameters()[i];
            NeedSplitParam annotation = parameter.getAnnotation(NeedSplitParam.class);
            // 判断下 参数有没有用这个注解
            if (Objects.nonNull(annotation)) {
                needSplitParamIndex = i;
                break;
            }
        }
        if (needSplitParamIndex == -1) {
            log.error(LOG_PREFIX + ",未使用NeedSplitParam注解,不执行");
            return point.proceed();
        }
        // 只能处理  object[] 和 collection类型
        if (!(args[needSplitParamIndex] instanceof Object[] || args[needSplitParamIndex] instanceof Collection)) {
            log.error(LOG_PREFIX + ",参数类型不支持,不执行");
            return point.proceed();
        }
        Object needSplitParam = args[needSplitParamIndex];
        // 如果目标的长度小于拆分长度，不执行
        if (needSplitParam instanceof Object[]) {
            if (((Object[]) needSplitParam).length < splitLimit) {
                log.error(LOG_PREFIX + ",参数长度小于拆分长度,不执行");
                return point.proceed();
            }
        } else {
            if (((Collection) needSplitParam).size() < splitLimit) {
                log.error(LOG_PREFIX + ",参数长度小于拆分长度,不执行");
                return point.proceed();
            }
        }

        // 计算需要拆分多少次
        int batchNum = getBatchNum(needSplitParam, splitGroupNum);

        // 使用多线程进行查询
        ThreadPoolEnum pool = splitSqlIn.threadPool();
        if (Objects.isNull(pool)) {
            return point.proceed();
        }
        CompletableFuture<?>[] futures = new CompletableFuture[batchNum];
        for (int currentBatch = 0; currentBatch < batchNum; currentBatch++) {
            int finalNeedSplitParamIndex = needSplitParamIndex;
            int finalCurrentBatch = currentBatch;
            Object finalNeedSplitParam = needSplitParam;
            futures[currentBatch] = CompletableFuture.supplyAsync(() -> {
                try {
                    Object[] dest = new Object[args.length];
                    System.arraycopy(args, 0, dest, 0, args.length);
                    dest[finalNeedSplitParamIndex] = getPartParam(finalNeedSplitParam, splitGroupNum, finalCurrentBatch);
                    return point.proceed(dest);
                } catch (Throwable e) {
                    log.error(LOG_PREFIX + ",执行异常 msg:{}", e.getMessage(), e);
                    throw new RuntimeException(e);
                }

            }, threadPools.get(pool));
        }
        CompletableFuture<Void> all = CompletableFuture.allOf(futures);
        all.get();
        Class<? extends ResultProcessor> handleReturn = splitSqlIn.handlerReturnClass();
        List<Object> resultList = new ArrayList<>(futures.length);
        for (CompletableFuture<?> future : futures) {
            resultList.add(future.get());
        }
        // 获取到每个part的结果然后调用处理函数
        return handleReturn.getDeclaredMethods()[0].invoke(handleReturn.getDeclaredConstructor().newInstance(), resultList);
    }

    /**
     * 获取当前批次
     * @param needSplitParam 分组参数
     * @param splitGroupNum 一组多少个
     * @param batch 第几批
     * @return
     */
    private Object getPartParam(Object needSplitParam, int splitGroupNum, int batch) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (needSplitParam instanceof Object[]) {
            Object[] splitParam = (Object[]) needSplitParam;
            int end = Math.min((batch + 1) * splitGroupNum, splitParam.length);
            return Arrays.copyOfRange(splitParam, batch * splitGroupNum, end);
        } else if (needSplitParam instanceof List) {
            List<?> splitParam = (List<?>) needSplitParam;
            int end = Math.min((batch + 1) * splitGroupNum, splitParam.size());
            return splitParam.subList(batch * splitGroupNum, end);
        } else if (needSplitParam instanceof Set) {
            List splitParam = new ArrayList<>((Set) needSplitParam);
            int end = Math.min((batch + 1) * splitGroupNum, splitParam.size());
            // 参数具体化了
            Set<?> set = (Set<?>) needSplitParam.getClass().getDeclaredConstructor().newInstance();
            set.addAll(splitParam.subList(batch * splitGroupNum, end));
            return set;
        } else {
            return null;
        }

    }

    /**
     * 计算需要拆分多少次
     * @param needSplitParam1 需要拆分的参数
     * @param splitGroupNum 一组多少个
     * @return
     */
    public Integer getBatchNum(Object needSplitParam1, Integer splitGroupNum) {
        if (needSplitParam1 instanceof Object[]) {
            Object[] splitParam = (Object[]) needSplitParam1;
            return splitParam.length % splitGroupNum == 0 ? splitParam.length / splitGroupNum : splitParam.length / splitGroupNum + 1;
        } else if (needSplitParam1 instanceof Collection) {
            Collection<?> splitParam = (Collection<?>) needSplitParam1;
            return splitParam.size() % splitGroupNum == 0 ? splitParam.size() / splitGroupNum : splitParam.size() / splitGroupNum + 1;
        } else {
            return 1;
        }
    }

}
