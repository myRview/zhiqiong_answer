package com.zhiqiong.model.vo.question;

import com.zhiqiong.common.PageBaseVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 题目表
 * </p>
 *
 * @author hk
 * @since 2025-03-13
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionPageVO extends PageBaseVO {
    @ApiModelProperty(value = "题目名称")
    private String questionName;
    @ApiModelProperty(value = "应用 id")
    private Long appId;


}
