package com.itdfq.common.utils.xmlUtils;


import com.itdfq.common.Exception.AssertUtils;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;


import org.apache.poi.util.DocumentHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * xml工具类
 *
 * @author dfq 2024/7/4 10:29
 * @implNote 使用方法：
 * public static void main(String[] args) throws XMLStreamException {
 * Student student = new Student();
 * student.setName("小明");
 * student.setAge(18);
 * Student student1 = new Student();
 * student1.setName("小张");
 * student1.setAge(18);
 * student1.setAddress("住址");
 * Student student2 = new Student();
 * student2.setName("小红");
 * student2.setAge(23);
 * student2.setAddress("成华大道");
 * student.setOther(Arrays.asList(student2,student1));
 * String xmlParseObject = getXmlParseObject(student);
 * System.out.println(xmlParseObject);
 * System.out.println("----------");
 * String listXml  = getXmlParseCollection(Arrays.asList(student1, student2),"Student");
 * System.out.println(listXml);
 * }
 * <p>
 * 打印结果为：
 */
@Slf4j
public class XmlTransFormUtils {

    @Data
    @XmlObject
    public static class Student {
        @XmlProperty(xmlKey = "name")
        private String name;

        @XmlProperty(xmlKey = "age")
        private Integer age;

        @XmlProperty(xmlKey = "REAL_ADRESS")
        private String address;

        @XmlProperty(xmlKey = "other", contentObj = Student.class)
        private List<Student> other;

        @XmlProperty(xmlKey = "book")
        private List<String> bookList;

        @XmlProperty(xmlKey = "sex")
        private Boolean sex;

        /**
         * map类型 暂时只支持 基本类型，不能嵌套对象</>
         */
        @XmlProperty(contentObj = Map.class,xmlKey = "map")
        private Map<String, String> map;

    }


    public static void main(String[] args) {
        // 举例测试对象
        Student student = new Student();
        student.setName("小明");
        student.setAge(18);
        Student student1 = new Student();
        student1.setName("小张");
        student1.setAge(18);
        student1.setAddress("住址");
        Student student2 = new Student();
        student2.setName("小红");
        student2.setAge(23);
        student2.setAddress("成华大道");
        student2.setSex(true);
        student2.setBookList(Arrays.asList("java", "spring"));
        Map<String, String> map = new HashMap<>();
        map.put("qq", "123131");
        map.put("wx","微信号o");
        student2.setMap(map);
        student.setOther(Arrays.asList(student2, student1));

        String xmlParseObject = getXmlParseObject(student);
        System.out.println(xmlParseObject);
        System.out.println("----------");
        String listXml  = getXmlParseCollection(Arrays.asList(student1, student2),"Student");
        System.out.println(listXml);

        //<?xml version="1.0" encoding="UTF-8" standalone="no"?>
        // <xml>
        //   <name>小明</name>
        //   <age>18</age>
        //   <other>
        //     <name>小红</name>
        //     <age>23</age>
        //     <REAL_ADRESS>成华大道</REAL_ADRESS>
        //     <book>java</book>
        //     <book>spring</book>
        //     <sex>true</sex>
        //     <map>
        //       <qq>123131</qq>
        //       <wx>微信号o</wx>
        //     </map>
        //   </other>
        //   <other>
        //     <name>小张</name>
        //     <age>18</age>
        //     <REAL_ADRESS>住址</REAL_ADRESS>
        //   </other>
        // </xml>
        //
        // ----------
        // <?xml version="1.0" encoding="UTF-8" standalone="no"?>
        // <xml>
        //   <Student>
        //     <name>小张</name>
        //     <age>18</age>
        //     <REAL_ADRESS>住址</REAL_ADRESS>
        //   </Student>
        //   <Student>
        //     <name>小红</name>
        //     <age>23</age>
        //     <REAL_ADRESS>成华大道</REAL_ADRESS>
        //     <book>java</book>
        //     <book>spring</book>
        //     <sex>true</sex>
        //     <map>
        //       <qq>123131</qq>
        //       <wx>微信号o</wx>
        //     </map>
        //   </Student>
        // </xml>
    }


    /**
     * 获取对象xml
     *
     * @param o
     * @return
     */
    public static String getXmlParseObject(Object o) {
        Document document = DocumentHelper.createDocument();
        Element root = document.createElement("xml");
        document.appendChild(root);
        addXml(root, o, document, null);
        return getStr(document);

    }

    private static String getStr(Document doc){
        try {
            OutputFormat format = new OutputFormat(doc);
            format.setIndenting(true);
            XMLSerializer serializer = new XMLSerializer(System.out, format);
            serializer.serialize(doc);
            return serializer.toString();
        } catch (Exception e) {
            log.error("xml转换字符串失败", e);
            return null;
        }
    }
    /**
     * 获取集合对象xml
     *
     * @param o              集合对象
     * @param collectionName 声明的集合名称
     * @return
     */
    public static String getXmlParseCollection(Object o, String collectionName) {
        Document document = DocumentHelper.createDocument();
        Element root = document.createElement("xml");
        document.appendChild(root);
        addXml(root, o, document, collectionName);
        return getStr(document);

    }

    /**
     * 添加xml
     *
     * @param root
     * @param obj
     * @param document
     * @param collectionName 集合名称的XML key
     */
    private static void addXml(Element root, Object obj, Document document, String collectionName) {

        if (obj instanceof List) {
            List<?> list = (List<?>) obj;
            for (Object o : list) {
                if (StringUtils.isNoneBlank(collectionName)) {
                    Element element = document.createElement(collectionName);
                    root.appendChild(element);
                    addXml(element, o, o.getClass(), document);
                    continue;
                }
                addXml(root, o, o.getClass(), document);
            }
        } else {
            addXml(root, obj, obj.getClass(), document);
        }

    }

    private static void addXml(Element root, Object obj, Class<?> aClass, Document document) {
        // 获取当前对象的所有属性
        Field[] fields = aClass.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            AssertUtils.isTrue(field.isAnnotationPresent(XmlProperty.class), "当前属性:" + field.getName() + "不存在XmlProperty注解,请检查");
            XmlProperty annotation = field.getAnnotation(XmlProperty.class);
            String xmlKey = annotation.xmlKey();
            Class<?> contentedObj = annotation.contentObj();
            Object o = invokeGetMethod(aClass, field, obj);
            if (o == null) {
                // 为空不继续执行
                continue;
            }
            // 如果随机性中的contentClazz传入的对象包含了PassiveMsg注解,表示的是嵌套对象需要进行迭代生成xml
            if (contentedObj.isAnnotationPresent(XmlObject.class) && !(o instanceof Map)) {
                // Element e = document.createElement(xmlKey);
                // root.appendChild(e);
                addXml(root, o, document, xmlKey);
                continue;
            }
            if (o instanceof List) {
                List<?> list = (List<?>) o;
                // 如果进入到这里说明 不是嵌套对象类型的集合,直接生成xml
                for (Object obj1 : list) {
                    Element element = document.createElement(xmlKey);
                    root.appendChild(element);
                    Text tName = document.createTextNode(obj1.toString());
                    element.appendChild(tName);
                }
            } else if (o instanceof Map) {
                Map<Object, Object> map = (Map<Object, Object>) o;
                //如果map集合 有xmlKey属性，就在最外层包裹一层 <xmlkey></xmlKey>
                if (xmlKey!=null && !xmlKey.isEmpty()) {
                    Element element = document.createElement(xmlKey);
                    root.appendChild(element);
                    root=element;
                }
                for (Map.Entry<Object, Object> entry : map.entrySet()) {
                    Element element = document.createElement(entry.getKey().toString());
                    root.appendChild(element);
                    Text tName = document.createTextNode(entry.getValue().toString());
                    element.appendChild(tName);
                }
            } else {

                Element element = document.createElement(xmlKey);
                root.appendChild(element);
                Text tName = document.createTextNode(o.toString());
                element.appendChild(tName);
            }


        }
    }

    /**
     * 调用dto的 get方法
     *
     * @param clazz
     * @param field
     * @param obj
     * @return
     */
    private static Object invokeGetMethod(Class<?> clazz, Field field, Object obj) {
        Object invoke = null;
        try {
            // 首字母大写
            String methodName = field.getName().replaceFirst(field.getName().substring(0, 1), field.getName().substring(0, 1).toUpperCase());
            invoke = clazz.getMethod("get" + methodName).invoke(obj);
        } catch (Exception e) {
            log.error("XmlUtils调用class的 get方法发生异常,请检查！", e);
        }
        return invoke;
    }


}
