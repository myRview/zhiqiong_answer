package com.zhiqiong.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.zhiqiong.model.entity.UserEntity;
import com.zhiqiong.model.vo.IdVO;
import com.zhiqiong.model.vo.RegisterUserVO;
import com.zhiqiong.model.vo.UserVO;

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
}
