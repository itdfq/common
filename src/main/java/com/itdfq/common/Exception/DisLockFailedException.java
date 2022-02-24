package com.itdfq.common.Exception;

/**
 * @author GodChin
 * @date 2022/2/24 10:09
 * @email 909256107@qq.com
 */
public class DisLockFailedException extends RuntimeException{
    public DisLockFailedException(String message) {
        super(message);
    }
}
