### 实现逻辑
1. 使用了@ControllerAdvice对请求数据进行预处理
2. 随机生产16为字符串，使用RSA加密生成key
3. 再对数据进行AES加密，使用步骤2key进行加密
### 使用方法
#### 依赖

```java
   <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>
   <dependency>
        <groupId>com.gitee.godchin</groupId>
        <artifactId>common</artifactId>
        <version>v2.0.2.24</version>
    </dependency>
```
#### 设置公钥和私钥

```java
encry.public.key=
decrypt.private.key=
```
#### 注解介绍

```java
public @interface SecurityParameter {
    /**
     * 入参是否解密，默认解密
     */
    boolean inDecode() default true;

    /**
     * 出参是否加密，默认加密
     */
    boolean outEncode() default true;
}
```
#### 使用举例

 - controller

```java
	 /**
     * 数据加密
     * @param user
     * @return
     */
    @RequestMapping("/encry")
    @SecurityParameter(inDecode = false)
    public Result save(@RequestBody DfqUsers user) {
        System.out.println(JSON.toJSONString(user));
        return Result.success(user);
    }
 	/**
     * 数据解密
     * @param user
     * @return
     */
    @RequestMapping(value = "/decrypt",method = {RequestMethod.GET,RequestMethod.POST})
    @SecurityParameter(outEncode = false)
    public Result jiemi(@RequestBody Object user){

        return Result.success(user);
    }
```

 - postman调用
 	
	 - 加密
	 ![加密](https://img-blog.csdnimg.cn/c874976a5e2143019d4463ee407572c4.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBASVRkZnE=,size_20,color_FFFFFF,t_70,g_se,x_16)

	 - 解密
![解密](https://img-blog.csdnimg.cn/7bc761f75ed7431e9be6fc8785c519bb.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBASVRkZnE=,size_20,color_FFFFFF,t_70,g_se,x_16)
