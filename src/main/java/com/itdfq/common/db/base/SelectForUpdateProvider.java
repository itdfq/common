package com.itdfq.common.db.base;




import org.apache.ibatis.mapping.MappedStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.mapperhelper.MapperTemplate;
import tk.mybatis.mapper.mapperhelper.SqlHelper;

import java.util.Set;


public class SelectForUpdateProvider extends MapperTemplate {
    private static final Logger log = LoggerFactory.getLogger(SelectForUpdateProvider.class);
    public SelectForUpdateProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    public String lockByPrimaryKey(MappedStatement ms) {
        Class<?> entityClass = this.getEntityClass(ms);
        this.setResultType(ms, entityClass);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.selectAllColumns(entityClass));
        sql.append(SqlHelper.fromTable(entityClass, this.tableName(entityClass)));
        sql.append(SqlHelper.wherePKColumns(entityClass));
        sql.append(" for update");
        return sql.toString();
    }

    public String lockByPrimaryKeyList(MappedStatement ms) {
        Class<?> entityClass = this.getEntityClass(ms);
        this.setResultType(ms, entityClass);

        //获取主键
        Set<EntityColumn> pkColumnList = EntityHelper.getPKColumns(entityClass);
        //无主键
        if(pkColumnList.size()<1){
            log.error("{}找不到主键", entityClass.getName());
            return "";
        }
        EntityColumn pkColumn = pkColumnList.iterator().next();

        return SqlHelper.selectAllColumns(entityClass) +
                SqlHelper.fromTable(entityClass, this.tableName(entityClass)) +
                "where " + pkColumn.getColumn() + " in" +
                " <foreach collection=\"list\" index=\"index\" item=\"item\" separator=\",\" open=\"(\" close=\")\">" +
                " #{item." + pkColumn.getProperty() + "} " +
                "</foreach>" +
                " for update";
    }
}

