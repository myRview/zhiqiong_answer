package com.zhiqiong.enums;

import lombok.Getter;

/**
 * 审核状态枚举
 * @author huangkun
 * @date 2025/3/13 19:30
 */
@Getter
public enum ReviewStatusEnum {

    REVIEW(0, "待审核"),
    PASS(1, "通过"),
    REJECT(2, "拒绝"),
    ;
    private final int value;
    private final String description;

    ReviewStatusEnum(int value, String description) {
        this.value = value;
        this.description = description;
    }

    /**
     * 根据value获取
     *
     * @param value
     * @return
     */
    public static ReviewStatusEnum getEnumByValue(int value) {
        for (ReviewStatusEnum roleEnum : ReviewStatusEnum.values()) {
            if (roleEnum.value == value) {
                return roleEnum;
            }
        }
        return null;
    }


}
