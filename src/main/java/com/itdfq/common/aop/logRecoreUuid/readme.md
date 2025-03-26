## 记录操作的线程uuid

在一次请求或者一个操作时，记录下当前的uuid,方便根据此uuid查询当前所有操作的流程

可以在需要的代码上加入aop

```java
@Aspect
@Component
@Order(-1)
public class HandlerInterceptor {
    // 对mq线程加 trade_uuid
    @Around("@annotation(rabbitListener)")
    public Object mqpointcut(ProceedingJoinPoint point, RabbitListener rabbitListener) throws Throwable{
        MDC.put("trace_uuid", UuidUtils.generateUuid());
        return point.proceed();
    }

    // task 线程 加trade_uuid
    @Around("execution( public * com.itdfq..*.task..*.execute(*) )")
    public Object pointcut(ProceedingJoinPoint point) throws Throwable{
        MDC.put("trace_uuid", UuidUtils.generateForLog().toString());
        return point.proceed();
    }
}

