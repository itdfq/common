## sql in的参数过多进行拆分并发查询并汇总

### 原理
1. 获取需要拆分的参数,将需要查询的数据分组
2. 使用线程池进行并发执行
3. 将线程池执行的结果进行汇总返回

### 注解
1. SplitSqlIn
    * threadPool  设置线程池
    * handlerReturnClass 设置数据汇总方式，一般都是list直接使用即可
    * splitLimit 设置需要拆分的限制，超过此限制才进行拆分
    * splitGroupNum 拆分后,每次执行数据条数
2. NeedSplitParam
   * 无参数,放在方法参数中,代表这个参数需要进行拆分


### 简单使用

```java
   /**
     * 根据id列表查询
     * @param ids
     * @return
     * 使用线程池，当查询总数大于1000时,拆分 一次查询500个，再将结果汇总返回
     */
    @SplitSqlIn(threadPool = ThreadPoolEnum.THREAD_POOL_1, splitGroupNum = 500,splitLimit = 1000)
    public List<Integer> listByIdList(@NeedSplitParam List<Long> ids){
        if (CollectionUtils.isEmpty(ids)){
            return null;
        }

        //执行sql 查询逻辑
        return null;

    }
```
