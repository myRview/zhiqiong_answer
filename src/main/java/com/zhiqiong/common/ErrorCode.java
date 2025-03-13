package com.zhiqiong.common;

import lombok.Getter;

/**
 * 自定义错误
 * @author huangkun
 * @date 2024/12/9 18:48
 */
@Getter
public enum ErrorCode {
    SUCCESS(20000,"成功"),
    NOT_LOGIN(40100,"未登录"),
    NO_PERMISSION(40300,"无权限"),
    ERROR_SYSTEM(50000,"系统错误"),
    ERROR_PARAM(60000,"参数错误")

    ;


    private final int code;

    private final String  message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
