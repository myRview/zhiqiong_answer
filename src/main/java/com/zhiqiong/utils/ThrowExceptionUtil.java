package com.zhiqiong.utils;

import com.zhiqiong.common.ErrorCode;
import com.zhiqiong.exception.BusinessException;

/**
 * @author huangkun
 * @date 2024/12/9 18:57
 */
public class ThrowExceptionUtil {

    /**
     *
     * @param condition  条件
     * @param exception  错误
     */
    public static void throwIf(boolean condition,RuntimeException exception){
        if (condition){
            throw exception;
        }
    }

    /**
     *
     * @param condition  条件
     * @param errorCode  错误码
     */
    public static void throwIf(boolean condition, ErrorCode errorCode){
        throwIf(condition,new BusinessException(errorCode));
    }

    /**
     *
     * @param condition  条件
     * @param errorCode  错误码
     * @param message   错误信息
     */
    public static void throwIf(boolean condition,ErrorCode errorCode,String message){
        throwIf(condition,new BusinessException(errorCode,message));
    }



}
