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
    private String msg;
    private Integer code;
    private T data;

    public Result() {

    }

    public Result(String msg, Integer code, T data) {
        this.msg = msg;
        this.code = code;
        this.data = data;
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
        return new Result(msg, FAIL_CODE, null);
    }

    public static Result success(Object data) {
        return new Result<>(null, SUCCESS_CODE, data);
    }

    public static Result success() {
        return new Result(null, SUCCESS_CODE, null);
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
                '}';
    }
}
