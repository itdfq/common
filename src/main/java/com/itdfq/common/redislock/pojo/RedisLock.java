package com.itdfq.common.redislock.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author GodChin
 * @date 2022/2/24 10:05
 * @email 909256107@qq.com
 */
@Data
public class RedisLock implements Serializable {
    private String key;
    /**
     * 单位毫秒
     */
    private Long expireMill;

    /**
     * 获取锁的等待时间：毫秒
     */
    private Long waitMill;

    /**
     * 是否抛出异常
     */
    private Boolean throwExceptionIfFailed;
}
