package com.zhiqiong.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhiqiong.model.entity.UserEntity;
import com.zhiqiong.model.vo.IdVO;
import com.zhiqiong.model.vo.user.LoginUserVO;
import com.zhiqiong.model.vo.user.RegisterUserVO;
import com.zhiqiong.model.vo.user.UserPageVO;
import com.zhiqiong.model.vo.user.UserVO;

import java.util.List;

/**
 * <p>
 * 用户 服务类
 * </p>
 *
 * @author hk
 * @since 2025-03-12
 */
public interface UserService extends IService<UserEntity> {

    boolean register(RegisterUserVO registerUserVO);

    List<UserVO> selectList(String userName, String userRole);

    void addUser(UserVO userVO);

    void deleteUser(IdVO idVO);

    void updateUser(UserVO userVO);

    String login(LoginUserVO loginUserVO);

    void resetPassword(IdVO idVO);

    Boolean updatePassword(UserVO userVO);

    UserVO getUserInfo(Long userId);

    UserVO getCurrentUser();

    void logout();

    Page<UserVO> selectPage(UserPageVO userPageVO);

    List<UserVO> selectUserInfoBatch(List<Long> userIds);

    UserVO selectByAccount(String userAccount);
}
