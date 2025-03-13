package com.zhiqiong.enums;

import lombok.Getter;

/**
 * 应用类型枚举
 * @author huangkun
 * @date 2025/3/13 19:30
 */
@Getter
public enum AppTypeEnum {

    TYPE_SCORE(0, "得分类"),
    TYPE_EVALUATION(1, "测评类"),
    ;
    private final int value;
    private final String description;

    AppTypeEnum(int value, String description) {
        this.value = value;
        this.description = description;
    }

    /**
     * 根据value获取
     *
     * @param value
     * @return
     */
    public static AppTypeEnum getEnumByValue(int value) {
        for (AppTypeEnum roleEnum : AppTypeEnum.values()) {
            if (roleEnum.value == value) {
                return roleEnum;
            }
        }
        return null;
    }


}
