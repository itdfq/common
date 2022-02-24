package com.itdfq.common.redislock.annotation.support;



import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * @author GodChin
 * @date 2022/2/23 9:15
 * @email 909256107@qq.com
 */
@Data
@Slf4j
@Service
public class RedisLockService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisOperations<String, String> operations;


    private static final int WAIT_SECOND = 200000;
    private static final long EXPIRE_MILL = 600;
    private static final int THREAD_SLEEP_TIME = 50;
    private static final String KEY_PREFIX = "study:lock:";
    /**
     * 更新成功
     */
    private static final Long SUCCESS_CODE = 1L;

    private static final String COMPARE_AND_DELETE =
            "if redis.call('get',KEYS[1]) == ARGV[1]\n" +
                    "then\n" +
                    "    return redis.call('del',KEYS[1])\n" +
                    "else\n" +
                    "    return 0\n" +
                    "end";

    public Boolean getLock(String lockKey, String uniqueId, Long expireMill) {
        //获取锁
        return redisTemplate.opsForValue().setIfAbsent(lockKey, uniqueId, expireMill, TimeUnit.MILLISECONDS);
    }

    private String getKey(String lockKey) {
        return KEY_PREFIX + lockKey;
    }

    public String get(String key){
        return (String) redisTemplate.opsForValue().get(key);
    }

    public boolean tryLock(long acquireTimeoutInMillis, String lockKey, String uniqueId, Long expireMill) throws InterruptedException {
        long acquireTime = System.currentTimeMillis();
        long expiryTime = acquireTime + acquireTimeoutInMillis;
        while (System.currentTimeMillis() < expiryTime) {
            if (Boolean.TRUE.equals(getLock(lockKey, uniqueId, expireMill))) {
                return true;
            }
            Thread.sleep(THREAD_SLEEP_TIME);
        }
        return false;
    }


    public boolean releaseLock(String lockKey, String value) {
        Long res = operations.execute(new DefaultRedisScript<>(COMPARE_AND_DELETE, Long.class),
                Collections.singletonList(lockKey), value);
        return SUCCESS_CODE.equals(res);
    }


}
