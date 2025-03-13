package com.zhiqiong.exception;

import com.zhiqiong.common.ErrorCode;
import lombok.Getter;

/**
 * @author huangkun
 * @date 2024/12/9 18:52
 */
@Getter
public class BusinessException extends RuntimeException{
     private final int code;

    public BusinessException(int code,String message) {
        super(message);
        this.code = code;
    }


    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }


    public BusinessException(ErrorCode errorCode,String message) {
        super(message);
        this.code = errorCode.getCode();
    }


}
