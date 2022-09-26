package com.itdfq.common.Exception;

/**
 * @author duanfangqin 2022/9/26 15:16
 * @implNote
 */
public class AssertUtils {
    public static void isTrue(boolean express,String msg){
        if (!express){
            throw new IllegalArgumentException(msg);
        }
    }

    public static void isFalse(boolean express,String msg){
        if (express){
            throw new IllegalArgumentException(msg);
        }
    }

    public static void isNull(String data,String msg){
        if (data==null){
            throw new IllegalArgumentException(msg);
        }
        if ("".equals(data)){
            throw new IllegalArgumentException(msg);
        }
    }

}
