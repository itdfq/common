package com.itdfq.common.aop.selectMoreIn;

import java.lang.annotation.*;

/**
 * 需要进行拆分查询的参数
 * @author dfq 2025/3/12 14:06
 * @implNote
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Documented
public @interface NeedSplitParam {
}
