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
     * 如果是map对象，会在map对象最外层包裹一层xmlKey
     * 例如：<br/>
     * @XmlProperty(xmlKey = "map",contentObj = Map.class)
     * public  Map<String,String> map
     *
     *   <map>
     *       <qq>123131</qq>
     *       <wx>微信号o</wx>
     *     </map>
     * @return
     */
    String xmlKey() default "";


    /**
     * 当前属性包含的对象类型
     * 注意：Map类型  不支持嵌套对象
     * @return
     */
    Class<?> contentObj() default String.class;
}
