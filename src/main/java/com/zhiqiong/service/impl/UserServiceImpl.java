package com.zhiqiong.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhiqiong.common.ErrorCode;
import com.zhiqiong.enums.UserRoleEnum;
import com.zhiqiong.model.entity.UserEntity;
import com.zhiqiong.mapper.UserMapper;
import com.zhiqiong.model.vo.IdVO;
import com.zhiqiong.model.vo.RegisterUserVO;
import com.zhiqiong.model.vo.UserVO;
import com.zhiqiong.service.UserService;
import com.zhiqiong.utils.RandomNameUtil;
import com.zhiqiong.utils.ThrowExceptionUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户 服务实现类
 * </p>
 *
 * @author hk
 * @since 2025-03-12
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {


    @Override
    public boolean register(RegisterUserVO registerUserVO) {
        String userAccount = registerUserVO.getUserAccount();
        String userName = registerUserVO.getUserName();
        String userPassword = registerUserVO.getUserPassword();
        ThrowExceptionUtil.throwIf(StrUtil.hasBlank(userAccount, userPassword), ErrorCode.ERROR_PARAM, "账号或密码不能为空");
        if (StrUtil.isBlank(userName)) {
            userName = RandomNameUtil.generateName();
        }
        checkExistUser(userAccount, null);

        UserEntity userEntity = new UserEntity();
        userEntity.setUserAccount(userAccount);
        userEntity.setUserName(userName);
        String md5 = DigestUtil.md5Hex(userPassword);
        userEntity.setUserPassword(md5);
        userEntity.setUserRole(UserRoleEnum.USER.getValue());
        return this.save(userEntity);
    }

    @Override
    public List<UserVO> selectList(String userName, String userRole) {
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StrUtil.isNotBlank(userName), UserEntity::getUserName, userName);
        queryWrapper.eq(StrUtil.isNotBlank(userRole), UserEntity::getUserRole, userRole);
        List<UserEntity> list = this.baseMapper.selectList(queryWrapper);
        return this.converterVO(list);
    }

    @Override
    public void addUser(UserVO userVO) {
        String userAccount = userVO.getUserAccount();
        String password = userVO.getPassword();
        ThrowExceptionUtil.throwIf(StrUtil.hasBlank(userAccount, password), ErrorCode.ERROR_PARAM, "账号或密码不能为空");

        checkExistUser(userAccount, null);

        String userName = userVO.getUserName();
        if (StrUtil.isBlank(userName)) {
            userName = RandomNameUtil.generateName();
            userVO.setUserName(userName);
        }
        String userRole = userVO.getUserRole();
        if (StrUtil.isBlank(userRole)) {
            userRole = UserRoleEnum.USER.getValue();
        }
        UserEntity userEntity = new UserEntity();
        BeanUtil.copyProperties(userVO, userEntity);

        String md5Hex = DigestUtil.md5Hex(password);
        userEntity.setUserPassword(md5Hex);
        userEntity.setUserRole(userRole);
        this.save(userEntity);
    }


    @Override
    public void deleteUser(IdVO idVO) {
        Long id = idVO.getId();
        ThrowExceptionUtil.throwIf(id == null, ErrorCode.ERROR_PARAM, "id不能为空");
        this.removeById(id);
    }

    @Override
    public void updateUser(UserVO userVO) {
        Long id = userVO.getId();
        ThrowExceptionUtil.throwIf(id == null, ErrorCode.ERROR_PARAM, "id不能为空");
        String userAccount = userVO.getUserAccount();
        ThrowExceptionUtil.throwIf(StrUtil.isBlank(userAccount), ErrorCode.ERROR_PARAM, "账号不能为空");
        checkExistUser(userAccount, id);
        UserEntity user = new UserEntity();
        user.setId(id);
        user.setUserAccount(userAccount);
        user.setUserName(userVO.getUserName());
        user.setUserAvatar(userVO.getUserAvatar());
        user.setUserProfile(userVO.getUserProfile());
        user.setUserRole(userVO.getUserRole());
        user.setMpOpenId(userVO.getMpOpenId());
        user.setUnionId(userVO.getUnionId());
        this.updateById(user);

    }

    private List<UserVO> converterVO(List<UserEntity> list) {
        if (CollectionUtil.isEmpty(list)) {
            return CollectionUtil.newArrayList();
        }
        return list.stream().map(this::converterVO).collect(Collectors.toList());
    }

    private UserVO converterVO(UserEntity userEntity) {
        if (userEntity == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(userEntity, userVO);
        return userVO;
    }


    /**
     * @param userAccount
     * @param id          用户Id，修改时校验
     */
    private void checkExistUser(String userAccount, Long id) {
        UserEntity user = selectByAccount(userAccount);
        ThrowExceptionUtil.throwIf(ObjUtil.isNull(id) ? (user != null) : (user != null && !user.getId().equals(id)), ErrorCode.ERROR_PARAM, "账号已存在");
    }

    private UserEntity selectByAccount(String userAccount) {
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserEntity::getUserAccount, userAccount);
        return this.baseMapper.selectOne(queryWrapper);
    }
}
