package com.itdfq.common.pojo;

import java.io.Serializable;

/**
 * @author: GodChin
 * @date: 2022/2/24 14:32
 * @mark: Controller统一返回类
 */
public class Result<T> implements Serializable {
    private static final int SUCCESS_CODE = 200;
    private static final int FAIL_CODE = 9999;
    private static final long DEFAULT_COUNT = 1;
    private String msg;
    private Integer code;
    private long count;
    private T data;

    public Result() {

    }

    public Result(String msg, Integer code, long count, T data) {
        this.msg = msg;
        this.code = code;
        this.count = count;
        this.data = data;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public T getData() {
        return this.data;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static Result fail(String msg) {
        return new Result(msg, FAIL_CODE,0, null);
    }

    public static Result success(Object data) {
        return new Result<>(null, SUCCESS_CODE, DEFAULT_COUNT,data);
    }

    public static Result success(Object data, long count) {
        return new Result<>(null, SUCCESS_CODE, count, data);
    }

    public static Result success(String msg, Object data, long count) {
        return new Result<>(msg, SUCCESS_CODE, count, data);
    }

    public static Result success(String msg, Integer code, Object data, long count) {
        return new Result<>(msg, code, count, data);
    }

    public static Result success() {
        return new Result(null, SUCCESS_CODE,0, null);
    }

    public Boolean isSuccess() {
        if (SUCCESS_CODE == code) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    public String toString() {
        return "Result{" +
                "msg='" + msg + '\'' +
                ", code=" + code +
                ", data=" + data +
                ", count=" + count +
                '}';
    }
}
