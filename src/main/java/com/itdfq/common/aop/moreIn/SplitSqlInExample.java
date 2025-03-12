package com.itdfq.common.aop.moreIn;

import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author dfq 2025/3/12 15:46
 * @implNote
 */

public class SplitSqlInExample {

    /**
     * 根据id列表查询
     * @param ids
     * @return
     * 使用线程池，当查询总数大于1000时,拆分 一次查询500个，再将结果汇总返回
     */
    @SplitSqlIn(threadPool = ThreadPoolEnum.THREAD_POOL_1, splitGroupNum = 500,splitLimit = 1000)
    public List<Integer> listByIdList(@NeedSplitParam List<Long> ids){
        if (CollectionUtils.isEmpty(ids)){
            return null;
        }

        //执行sql 查询逻辑
        return null;

    }
}
