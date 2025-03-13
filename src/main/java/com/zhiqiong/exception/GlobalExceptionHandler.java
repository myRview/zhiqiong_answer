package com.zhiqiong.exception;

import com.zhiqiong.common.ErrorCode;
import com.zhiqiong.common.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author huangkun
 * @date 2024/12/9 19:28
 * 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseResult<?> businessExceptionHandler(BusinessException e){
        log.error("BusinessException",e);
        return ResponseResult.fail(e.getCode(),e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseResult<?> businessExceptionHandler(RuntimeException e){
        log.error("RuntimeException",e);
        return ResponseResult.fail(ErrorCode.ERROR_SYSTEM,e.getMessage());
    }

}
