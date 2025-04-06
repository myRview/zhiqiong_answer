package com.zhiqiong.enums;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Getter;

import java.util.List;
import java.util.Set;

/**
 * 用户角色枚举
 * @author huangkun
 * @date 2025/3/13 19:30
 */
@Getter
public enum UserRoleEnum {

    USER("user", "普通用户", CollectionUtil.newHashSet("sys:view")),
    ADMIN("admin", "管理员", CollectionUtil.newHashSet("sys:view","sys:edit")),
    ;
    private final String value;
    private final String description;
    private final Set<String> permissions;

    UserRoleEnum(String value, String description, Set<String> permissions) {
        this.value = value;
        this.description = description;
        this.permissions = permissions;
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
