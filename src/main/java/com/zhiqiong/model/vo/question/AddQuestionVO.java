package com.zhiqiong.model.vo.question;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 题目表
 * </p>
 *
 * @author hk
 * @since 2025-03-13
 */
@Data
@ApiModel(value = "QuestionVO对象")
public class AddQuestionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "题目列表")
    private List<TopicVO> topicVOList;

    @ApiModelProperty(value = "应用 id")
    private Long appId;


}
