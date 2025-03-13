package com.zhiqiong.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhiqiong.model.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户 Mapper 接口
 * </p>
 *
 * @author hk
 * @since 2025-03-12
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {

}
