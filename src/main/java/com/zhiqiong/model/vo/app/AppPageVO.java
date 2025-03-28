package com.zhiqiong.model.vo.app;

import com.zhiqiong.common.PageBaseVO;
import com.zhiqiong.model.vo.user.UserVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 应用表
 * </p>
 *
 * @author hk
 * @since 2025-03-13
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AppPageVO extends PageBaseVO {

    @ApiModelProperty(value = "应用名")
    private String appName;

    @ApiModelProperty(value = "应用类型（0-得分类，1-测评类）")
    private Integer appType;

    @ApiModelProperty(value = "评分策略（0-自定义，1-AI）")
    private Integer scoringStrategy;

    @ApiModelProperty(value = "审核状态：0-待审核, 1-通过, 2-拒绝")
    private Integer reviewStatus;



}
