package com.zhiqiong.security;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 * @author huangkun
 * @date 2025/4/6 9:20
 */
@Component("ss")
@Slf4j
public class PermissionService {
    public boolean hasPermission(String permission) {
        if (StrUtil.isBlank(permission)) return false;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailVO loginUser = null;
        try {
            loginUser = (UserDetailVO) authentication.getPrincipal();
        } catch (Exception e) {
            e.printStackTrace();
            log.info("获取用户信息失败:{}",e);
        }
        if (ObjUtil.isNull(loginUser) || CollectionUtils.isEmpty(loginUser.getPermissions()))
        {
            return false;
        }
        return hasPermissions(loginUser.getPermissions(), permission);
    }

    /**
     * 判断是否包含权限
     *
     * @param permissions 权限列表
     * @param permission 权限字符串
     * @return 用户是否具备某权限
     */
    private boolean hasPermissions(Set<String> permissions, String permission) {
        return permissions.contains(permission);
    }
}
