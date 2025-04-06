package com.zhiqiong.security;

import com.zhiqiong.model.vo.user.UserVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

/**
 * @author huangkun
 * @date 2025/3/30 11:23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailVO implements UserDetails {


    private static final long serialVersionUID = -1588658232859085706L;
    @ApiModelProperty(value = "userId")
    private Long userId;

    @ApiModelProperty(value = "token")
    private String token;

    @ApiModelProperty(value = "权限列表")
    private Set<String> permissions;

    @ApiModelProperty(value = "用户信息")
    private UserVO user;


    @ApiModelProperty(value = "登录时间")
    private Long loginTime;

    @ApiModelProperty(value = "过期时间")
    private Long expireTime;


    public UserDetailVO(Long userId,UserVO user,Set<String> permissions){
        this.userId = userId;
        this.user = user;
        this.permissions = permissions;
    }

    @Override
    public String getPassword() {
        // 返回用户的密码
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        // 返回用户的账号
        return user.getUserAccount();
    }

    @Override
    public boolean isAccountNonExpired() {
        // 返回用户的账户是否未过期
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // 返回用户的账户是否未锁定
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // 返回用户的凭证是否未过期
        return true;
    }

    @Override
    public boolean isEnabled() {
        // 返回用户的账户是否可用
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 返回用户的权限列表
        return null;
    }
}
