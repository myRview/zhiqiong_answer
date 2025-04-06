package com.zhiqiong.security;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhiqiong.common.ErrorCode;
import com.zhiqiong.enums.UserRoleEnum;
import com.zhiqiong.exception.BusinessException;
import com.zhiqiong.model.entity.UserEntity;
import com.zhiqiong.model.vo.user.UserVO;
import com.zhiqiong.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

/**
 * @author 20231
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private UserService userService;


    @Override
    public UserDetails loadUserByUsername(String userAccount) throws UsernameNotFoundException {
        UserEntity userEntity = userService.getOne(new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getUserAccount, userAccount));
        if (userEntity == null) {
            throw new BusinessException(ErrorCode.ERROR_PARAM, "用户不存在");
        }
        return createUserDetail(userEntity);
    }

    private UserDetailVO createUserDetail(UserEntity userEntity) {
        UserVO user = new UserVO();
        BeanUtil.copyProperties(userEntity, user);
        user.setPassword(userEntity.getUserPassword());
        Set<String> set = new HashSet<>();
        UserRoleEnum roleEnum = UserRoleEnum.getEnumByValue(user.getUserRole());
        if (roleEnum != null) {
            set = roleEnum.getPermissions();
        }
        return new UserDetailVO(user.getId(), user, set);
    }
}