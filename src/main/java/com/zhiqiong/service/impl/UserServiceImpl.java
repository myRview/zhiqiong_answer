package com.zhiqiong.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhiqiong.cache.RedisCacheManager;
import com.zhiqiong.common.ErrorCode;
import com.zhiqiong.common.constant.CacheConstants;
import com.zhiqiong.common.constant.UserConstants;
import com.zhiqiong.enums.UserRoleEnum;
import com.zhiqiong.model.entity.UserEntity;
import com.zhiqiong.mapper.UserMapper;
import com.zhiqiong.model.vo.IdVO;
import com.zhiqiong.model.vo.LoginUserVO;
import com.zhiqiong.model.vo.RegisterUserVO;
import com.zhiqiong.model.vo.UserVO;
import com.zhiqiong.service.UserService;
import com.zhiqiong.utils.JwtUtil;
import com.zhiqiong.utils.RandomNameUtil;
import com.zhiqiong.utils.ThrowExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
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
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

    @Resource
    private RedisCacheManager redisCacheManager;


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
    public String login(LoginUserVO loginUserVO) {
        //校验用户名密码
        String userAccount = loginUserVO.getUserAccount();
        String userPassword = loginUserVO.getUserPassword();
        ThrowExceptionUtil.throwIf(StrUtil.hasBlank(userAccount, userPassword), ErrorCode.ERROR_PARAM, "账号或密码不能为空");

        String md5Hex = DigestUtil.md5Hex(userPassword);
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserEntity::getUserAccount, userAccount);
        queryWrapper.eq(UserEntity::getUserPassword, md5Hex);
        UserEntity user = this.baseMapper.selectOne(queryWrapper);
        ThrowExceptionUtil.throwIf(ObjUtil.isNull(user), ErrorCode.ERROR_PARAM, "账号或密码错误");

        //生成token
        HashMap<String, Object> map = new HashMap<>();
        Long userId = user.getId();
        map.put("userId", userId);
        map.put("user", user);
        String token = JwtUtil.createToken(map);

        //TODO:  将token添加到缓存，设置过期时间，这里过期时间暂时固定，到时候完善成动态设置
        String key = CacheConstants.LOGIN_TOKEN_KEY + userId;
        redisCacheManager.setCacheValue(key, token, 30, TimeUnit.MINUTES);
        return token;
    }

    @Override
    public void resetPassword(IdVO idVO) {
        //根据用户id查询用户是否存在
        Long id = idVO.getId();
        UserEntity user = this.getById(id);
        ThrowExceptionUtil.throwIf(user == null, ErrorCode.ERROR_PARAM, "用户不存在");
        //重置密码
        LambdaUpdateWrapper<UserEntity> updateWrapper = new LambdaUpdateWrapper<>();
        String md5Hex = DigestUtil.md5Hex(UserConstants.USER_PASSWORD);
        updateWrapper.set(UserEntity::getUserPassword, md5Hex);
        updateWrapper.eq(UserEntity::getId, id);
        boolean update = this.update(updateWrapper);
        //清除用户登录信息
        if (update) {
            String key = CacheConstants.LOGIN_TOKEN_KEY + id;
            redisCacheManager.delete(key);
        }
    }

    @Override
    public Boolean updatePassword(UserVO userVO) {
        Long id = userVO.getId();
        String password = userVO.getPassword();
        UserEntity user = this.getById(id);
        ThrowExceptionUtil.throwIf(user == null, ErrorCode.ERROR_PARAM, "用户不存在");
        ThrowExceptionUtil.throwIf(StrUtil.isBlank(password), ErrorCode.ERROR_PARAM, "密码不能为空");

        String md5Hex = DigestUtil.md5Hex(password);
        LambdaUpdateWrapper<UserEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(UserEntity::getUserPassword, md5Hex);
        updateWrapper.eq(UserEntity::getId, id);
        return this.update(updateWrapper);
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

        LambdaUpdateWrapper<UserEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(UserEntity::getUserAccount, userAccount);
        if (StrUtil.isNotBlank(userVO.getUserName())) {
            updateWrapper.set(UserEntity::getUserName, userVO.getUserName());
        }
        if (StrUtil.isNotBlank(userVO.getUserAvatar())) {
            updateWrapper.set(UserEntity::getUserAvatar, userVO.getUserAvatar());
        }
        if (StrUtil.isNotBlank(userVO.getUserProfile())) {
            updateWrapper.set(UserEntity::getUserProfile, userVO.getUserProfile());
        }
        if (StrUtil.isNotBlank(userVO.getUserRole())) {
            updateWrapper.set(UserEntity::getUserRole, userVO.getUserRole());
        }
        if (StrUtil.isNotBlank(userVO.getMpOpenId())) {
            updateWrapper.set(UserEntity::getMpOpenId, userVO.getMpOpenId());
        }
        if (StrUtil.isNotBlank(userVO.getUnionId())) {
            updateWrapper.set(UserEntity::getUnionId, userVO.getUnionId());
        }
        updateWrapper.eq(UserEntity::getId, id);
        boolean update = this.update(updateWrapper);
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
