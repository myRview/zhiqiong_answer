package com.zhiqiong.enums;

import lombok.Getter;

/**
 * 评分策略枚举
 * @author huangkun
 * @date 2025/3/13 19:30
 */
@Getter
public enum ScoreStrategyEnum {

    TYPE_DEFINED(0, "自定义"),
    TYPE_AI(1, "AI"),
    ;
    private final int value;
    private final String description;

    ScoreStrategyEnum(int value, String description) {
        this.value = value;
        this.description = description;
    }

    /**
     * 根据value获取
     *
     * @param value
     * @return
     */
    public static ScoreStrategyEnum getEnumByValue(int value) {
        for (ScoreStrategyEnum roleEnum : ScoreStrategyEnum.values()) {
            if (roleEnum.value == value) {
                return roleEnum;
            }
        }
        return null;
    }


}
