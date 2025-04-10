package com.zhiqiong.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhiqiong.common.ErrorCode;
import com.zhiqiong.common.constant.TokenConstants;
import com.zhiqiong.common.constant.UserConstants;
import com.zhiqiong.enums.UserRoleEnum;
import com.zhiqiong.exception.BusinessException;
import com.zhiqiong.manager.token.TokenService;
import com.zhiqiong.mapper.UserMapper;
import com.zhiqiong.model.entity.UserEntity;
import com.zhiqiong.model.vo.IdVO;
import com.zhiqiong.model.vo.user.LoginUserVO;
import com.zhiqiong.model.vo.user.RegisterUserVO;
import com.zhiqiong.model.vo.user.UserPageVO;
import com.zhiqiong.model.vo.user.UserVO;
import com.zhiqiong.security.UserDetailVO;
import com.zhiqiong.service.UserService;
import com.zhiqiong.utils.JwtUtil;
import com.zhiqiong.utils.RandomNameUtil;
import com.zhiqiong.utils.ThrowExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

    @Resource
    private HttpServletRequest httpServletRequest;
    @Resource
    @Lazy
    private AuthenticationManager authenticationManager;
    @Resource
    @Lazy
    private PasswordEncoder passwordEncoder;
    @Resource
    private TokenService tokenService;


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
        String encode = passwordEncoder.encode(userPassword);
        userEntity.setUserPassword(encode);
        userEntity.setUserRole(UserRoleEnum.USER.getValue());
        return this.save(userEntity);
    }

    @Override
    public String login(LoginUserVO loginUserVO) {
        //校验用户名密码
        String userAccount = loginUserVO.getUserAccount();
        String userPassword = loginUserVO.getUserPassword();
        ThrowExceptionUtil.throwIf(StrUtil.hasBlank(userAccount, userPassword), ErrorCode.ERROR_PARAM, "账号或密码不能为空");
        Authentication authentication = null;
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userAccount, userPassword);
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
            authentication = authenticationManager.authenticate(authenticationToken);
        } catch (AuthenticationException e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.ERROR_PARAM, "账号或密码错误");
        }
        UserDetailVO userDetailVO = (UserDetailVO) authentication.getPrincipal();
        String token = tokenService.createToken(userDetailVO);
        return token;
    }

    @Override
    public void logout() {
        String token = httpServletRequest.getHeader(TokenConstants.USER_TOKEN);
        String userId = JwtUtil.getUserId(token);
        tokenService.delLoginUser(userId);
    }

    @Override
    public void resetPassword(IdVO idVO) {
        //根据用户id查询用户是否存在
        Long id = idVO.getId();
        UserEntity user = this.getById(id);
        ThrowExceptionUtil.throwIf(user == null, ErrorCode.ERROR_PARAM, "用户不存在");
        //重置密码
        LambdaUpdateWrapper<UserEntity> updateWrapper = new LambdaUpdateWrapper<>();
        String encode = passwordEncoder.encode(UserConstants.USER_PASSWORD);
        updateWrapper.set(UserEntity::getUserPassword, encode);
        updateWrapper.eq(UserEntity::getId, id);
        boolean update = this.update(updateWrapper);
        //清除用户登录信息
        if (update) {
            tokenService.delLoginUser(String.valueOf(id));
        }
    }

    @Override
    public Boolean updatePassword(UserVO userVO) {
        Long id = userVO.getId();
        String password = userVO.getPassword();
        UserEntity user = this.getById(id);
        ThrowExceptionUtil.throwIf(user == null, ErrorCode.ERROR_PARAM, "用户不存在");
        ThrowExceptionUtil.throwIf(StrUtil.isBlank(password), ErrorCode.ERROR_PARAM, "密码不能为空");

        String encode = passwordEncoder.encode(UserConstants.USER_PASSWORD);
        LambdaUpdateWrapper<UserEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(UserEntity::getUserPassword, encode);
        updateWrapper.eq(UserEntity::getId, id);
        boolean update = this.update(updateWrapper);
        if (update) {
            tokenService.delLoginUser(String.valueOf(id));
        }
        return update;
    }

    @Override
    public List<UserVO> selectList(String userName, String userRole) {
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(userName), UserEntity::getUserName, userName);
        queryWrapper.eq(StrUtil.isNotBlank(userRole), UserEntity::getUserRole, userRole);
        List<UserEntity> list = this.baseMapper.selectList(queryWrapper);
        return this.converterVO(list);
    }

    @Override
    public UserVO getUserInfo(Long userId) {
        UserEntity user = this.getById(userId);
        return converterVO(user);
    }

    @Override
    public UserVO getCurrentUser() {
        UserDetailVO loginUser = tokenService.getLoginUser(httpServletRequest);
        if (loginUser == null) return null;
        UserVO user = loginUser.getUser();
        return user;
    }

    @Override
    public void addUser(UserVO userVO) {
        String userAccount = userVO.getUserAccount();
        ThrowExceptionUtil.throwIf(StrUtil.hasBlank(userAccount), ErrorCode.ERROR_PARAM, "账号不能为空");

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

        String encode = passwordEncoder.encode(UserConstants.USER_PASSWORD);
        userEntity.setUserPassword(encode);
        userEntity.setUserRole(userRole);
        this.save(userEntity);
    }


    @Override
    public void deleteUser(IdVO idVO) {
        Long id = idVO.getId();
        ThrowExceptionUtil.throwIf(id == null, ErrorCode.ERROR_PARAM, "id不能为空");
        boolean remove = this.removeById(id);
        if (remove) tokenService.delLoginUser(String.valueOf(id));
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
        updateWrapper.set(StrUtil.isNotBlank(userVO.getUserName()), UserEntity::getUserName, userVO.getUserName());
        updateWrapper.set(StrUtil.isNotBlank(userVO.getUserAvatar()), UserEntity::getUserAvatar, userVO.getUserAvatar());
        updateWrapper.set(StrUtil.isNotBlank(userVO.getUserProfile()), UserEntity::getUserProfile, userVO.getUserProfile());
        updateWrapper.set(StrUtil.isNotBlank(userVO.getUserRole()), UserEntity::getUserRole, userVO.getUserRole());
        updateWrapper.set(StrUtil.isNotBlank(userVO.getMpOpenId()), UserEntity::getMpOpenId, userVO.getMpOpenId());
        updateWrapper.set(StrUtil.isNotBlank(userVO.getUnionId()), UserEntity::getUnionId, userVO.getUnionId());
        updateWrapper.eq(UserEntity::getId, id);
        boolean update = this.update(updateWrapper);
    }

    @Override
    public Page<UserVO> selectPage(UserPageVO userPageVO) {
        String userName = userPageVO.getUserName();
        String userAccount = userPageVO.getUserAccount();
        String userRole = userPageVO.getUserRole();
        Integer pageNum = userPageVO.getPageNum();
        Integer pageSize = userPageVO.getPageSize();
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(userName), UserEntity::getUserName, userName);
        queryWrapper.like(StrUtil.isNotBlank(userAccount), UserEntity::getUserAccount, userAccount);
        queryWrapper.eq(StrUtil.isNotBlank(userRole), UserEntity::getUserRole, userRole);
        queryWrapper.orderByDesc(UserEntity::getCreateTime);
        IPage<UserEntity> page = this.page(new Page<>(pageNum, pageSize), queryWrapper);
        Page<UserVO> pageResult = new Page<>(pageNum, pageSize, page.getTotal());
        pageResult.setRecords(converterVO(page.getRecords()));
        return pageResult;
    }

    @Override
    public List<UserVO> selectUserInfoBatch(List<Long> userIds) {
        List<UserEntity> list = this.listByIds(userIds);
        return converterVO(list);
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
        UserVO user = selectByAccount(userAccount);
        ThrowExceptionUtil.throwIf(ObjUtil.isNull(id) ? (user != null) : (user != null && !user.getId().equals(id)), ErrorCode.ERROR_PARAM, "账号已存在");
    }

    @Override
    public UserVO selectByAccount(String userAccount) {
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserEntity::getUserAccount, userAccount);
        return converterVO(this.baseMapper.selectOne(queryWrapper));
    }
}
