package com.itdfq.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author GodChin
 * @date 2022/2/18 16:23
 * @email 909256107@qq.com
 */
@Data
@AllArgsConstructor
public class Result {
    /**
     * 标识返回的状态码
     */
    private Integer code;
    /**
     * 标识返回的信息
     */
    private String message;
    /**
     * 标识返回的数据
     */
    private Object data;

    private static final int SUCCESS_CODE = 200;
    private static final int FAIL_CODE = 9999;

    /**
     * 私有化，防止new
     */
    private Result() {
    }


    public static Result ok(Object data, String message) {
        return new Result(SUCCESS_CODE, message, data);
    }

    public static Result ok(Object data) {
        return Result.ok(data, "success");
    }

    public static Result error(String message) {
        return new Result(FAIL_CODE, message, "");
    }

    public static Result error(Integer code, String message) {
        return new Result(code, message, "");
    }
}
