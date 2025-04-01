package com.zhiqiong.model.vo.question;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 题目表
 * </p>
 *
 * @author hk
 * @since 2025-03-13
 */
@Data
public class QuestionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "题目内容（json格式）")
    private String questionContent;

    @ApiModelProperty(value = "应用 id")
    private Long appId;

    @ApiModelProperty(value = "创建用户 id")
    private Long userId;



}
