package com.itdfq.common.utils.xmlUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 使用此注解声明当前类需要解析为xml
 *
 * @author dfq 2024/7/4 10:23
 * @implNote
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlObject {
}
