package com.itdfq.common.db.base;


import tk.mybatis.mapper.common.base.select.SelectAllMapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

/**
 * @author duanfangqin 2022/8/18 14:50
 * @implNote
 */
public interface BaseMapper<T> extends InsertListMapper<T>, UpdateListMapper<T>, SelectAllMapper<T>, SelectForUpdateMapper<T> {

}
