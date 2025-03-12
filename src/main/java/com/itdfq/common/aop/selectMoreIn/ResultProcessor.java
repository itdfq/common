package com.itdfq.common.aop.selectMoreIn;

import java.util.List;

/**
 * @author dfq 2025/3/12 14:57
 * @implNote
 */
public interface ResultProcessor {
    /**
     * 处理返回结果方法
     * @param t
     * @return
     */
    Object handle(List t);
}
