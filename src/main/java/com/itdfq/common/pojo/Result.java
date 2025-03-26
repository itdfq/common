package com.itdfq.common.pojo;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.MDC;

import java.io.Serializable;

/**
 * @author: GodChin
 * @date: 2022/2/24 14:32
 * @mark: Controller统一返回类
 */
@Setter
@Getter
public class Result<T> implements Serializable {
    private static final int SUCCESS_CODE = 200;
    private static final int FAIL_CODE = 9999;
    private static final long DEFAULT_COUNT = 1;
    private String msg;
    private Integer code;
    private long count;
    private T data;
    /**扩展字段*/
    private Object extra;

    private String traceUUID;

    public Result() {
        //从线程中获取 uuid
        this.traceUUID = MDC.get("trace_uuid");
    }

    public Result(String msg, Integer code, long count, T data) {
        this.msg = msg;
        this.code = code;
        this.count = count;
        this.data = data;
        this.traceUUID = MDC.get("trace_uuid");
    }
    public static <T> Result<T> fail(String msg) {
        return new Result<T>(msg, FAIL_CODE,0, null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<T>(null, SUCCESS_CODE, DEFAULT_COUNT, data);
    }

    public static <T> Result<T> success(T data, long count) {
        return new Result<T>(null, SUCCESS_CODE, count, data);
    }

    public static <T> Result<T> success(String msg, T data, long count) {
        return new Result<>(msg, SUCCESS_CODE, count, data);
    }

    public static <T> Result<T> success(String msg, Integer code, T data, long count) {
        return new Result<T>(msg, code, count, data);
    }

    public static <T> Result<T> success() {
        return new Result<>(null, SUCCESS_CODE,0, null);
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
                ", count=" + count +
                ", data=" + data +
                ", extra=" + extra +
                ", traceUUID='" + traceUUID + '\'' +
                '}';
    }
}
