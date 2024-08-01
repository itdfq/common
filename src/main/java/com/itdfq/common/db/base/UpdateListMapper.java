package com.itdfq.common.db.base;

import org.apache.ibatis.annotations.UpdateProvider;
import tk.mybatis.mapper.annotation.RegisterMapper;

import java.util.List;


@RegisterMapper
public interface UpdateListMapper<T> {
    /**
     * 批量更新,不更新null值<br>
     * @param recordList
     * @return
     */
    @UpdateProvider(type = UpdateListProvider.class, method = "dynamicSQL")
    int updateListByPrimaryKeySelective(List<T> recordList);

}
