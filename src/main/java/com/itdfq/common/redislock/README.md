### 使用方法
#### 添加依赖

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
        <version>1.0.0</version>
    </dependency>
```
#### 首先配置Redis

```sql
#Redis服务器地址
spring.redis.host=121.36.72.23
spring.redis.password=root
#Redis服务器连接端口
spring.redis.port=6379
#Redis数据库索引（默认为0）
spring.redis.database= 0
```
#### 开启扫描包

```java
@ComponentScan( basePackages = {"com.itdfq.common","com.itdfq.loginuser"})
```
`com.itdfq.common` 是工具包的包名，让项目进行扫描，`com.itdfq.loginuser` 是本地项目的包接口
#### 使用注解@DisLock 

```java
public @interface DisLock {
		//锁的key，如果不填默认是包名+类名+参数名
    String key() default "";

		//过期时间
    long expire() default 600L;

		//等待获取锁时间
    long waitTime() default 10000L;
		//是否抛出异常
    boolean throwExceptionIfFailed() default true;
}
```
#### 例子
在Redis中定义了study:lock:sum  的值为50,使用jMether创建100个线程并发操作，模拟用户批量下单
![Redis库存](https://img-blog.csdnimg.cn/49161daf13564251b496a650fc969eb8.png)

操作代码：

```java
  		Integer sum = (Integer) redisTemplate.opsForValue().get(KEY);
        if (sum > 0) {
            redisTemplate.opsForValue().set(KEY, sum - 1, EXPIRE_SECOND, TimeUnit.SECONDS);
            return Result.success("购买成功，剩余库存" + (sum - 1));
        }
```
 使用注解加锁

```java
 @DisLock
    public Result lockTest() {
        Integer sum = (Integer) redisTemplate.opsForValue().get(KEY);
        if (sum > 0) {
            redisTemplate.opsForValue().set(KEY, sum - 1, EXPIRE_SECOND, TimeUnit.SECONDS);
            return Result.success("购买成功，剩余库存" + (sum - 1));
        }
        return Result.fail("没有库存");
    }
```
创建Controller测试

```java
/**
 * @author GodChin
 * @date 2022/2/24 10:23
 * @email 909256107@qq.com
 */
@RestController
@RequestMapping("/lock")
public class RedisLockTestController {
    @Autowired
    private RedisService lockTestService;

    @GetMapping("/submit3")
    public Result submit3() {
        return lockTestService.lockTest();
    }
}
```
结果：
![库存](https://img-blog.csdnimg.cn/69670abb08664aa1bc7a50d35ec60d3a.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBASVRkZnE=,size_19,color_FFFFFF,t_70,g_se,x_16)
![jMeter](https://img-blog.csdnimg.cn/3ff4f1edfeba4efaa484db235410b33c.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBASVRkZnE=,size_20,color_FFFFFF,t_70,g_se,x_16)
