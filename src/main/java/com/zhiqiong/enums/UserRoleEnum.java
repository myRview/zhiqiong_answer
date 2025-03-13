package com.zhiqiong.enums;

import lombok.Getter;

/**
 * 用户角色枚举
 * @author huangkun
 * @date 2025/3/13 19:30
 */
@Getter
public enum UserRoleEnum {

    USER("user", "普通用户"),
    ADMIN("admin", "管理员"),
    ;
    private final String value;
    private final String description;

    UserRoleEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }

    /**
     * 根据value获取
     *
     * @param value
     * @return
     */
    public static UserRoleEnum getEnumByValue(String value) {
        for (UserRoleEnum roleEnum : UserRoleEnum.values()) {
            if (roleEnum.value.equals(value)) {
                return roleEnum;
            }
        }
        return null;
    }


}
