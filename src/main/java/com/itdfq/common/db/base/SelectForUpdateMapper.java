package com.itdfq.common.db.base;

import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

/**
 * @author duanfangqin 2022/9/21 15:05
 * @implNote
 */
public interface SelectForUpdateMapper<T> {

    /**
     * 根据主键。锁定数据
     * @param
     * @return select * from table for update
     */
    @SelectProvider(type = SelectForUpdateProvider.class, method = "dynamicSQL")
    T lockByPrimaryKey(Object o);
    /**
     * 根据主键。锁定数据
     * @param
     * @return select * from table for update
     */
    @SelectProvider(type = SelectForUpdateProvider.class, method = "dynamicSQL")
    List<T> lockByPrimaryKeyList(Object o);
}

