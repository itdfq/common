package com.itdfq.common.utils.xmlUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author dfq 2024/7/4 10:24
 * @implNote
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlProperty {
    /**
     * 声明当前属性在xml中的key
     * @return
     */
    String xmlKey() default "";


    /**
     * 当前属性包含的对象类型
     * @return
     */
    Class<?> contentObj() default String.class;
}
