package com.zhiqiong.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhiqiong.model.entity.UserAnswerEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户答题记录 Mapper 接口
 * </p>
 *
 * @author hk
 * @since 2025-03-12
 */
@Mapper
public interface UserAnswerMapper extends BaseMapper<UserAnswerEntity> {

}
