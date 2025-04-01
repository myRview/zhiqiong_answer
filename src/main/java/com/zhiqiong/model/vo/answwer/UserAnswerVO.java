package com.zhiqiong.model.vo.answwer;

import com.zhiqiong.model.vo.app.AppVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 用户答题记录
 * </p>
 *
 * @author hk
 * @since 2025-03-13
 */
@Data
public class UserAnswerVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "应用 id")
    private Long appId;

    @ApiModelProperty(value = "应用类型（0-得分类，1-角色测评类）")
    private Integer appType;

    @ApiModelProperty(value = "评分策略（0-自定义，1-AI）")
    private Integer scoringStrategy;

    @ApiModelProperty(value = "用户答案（JSON 数组）")
    private String choices;

    @ApiModelProperty(value = "评分结果 id")
    private Long resultId;

    @ApiModelProperty(value = "结果名称，如物流师")
    private String resultName;

    @ApiModelProperty(value = "结果描述")
    private String resultDesc;

    @ApiModelProperty(value = "结果图标")
    private String resultPicture;

    @ApiModelProperty(value = "得分")
    private Integer resultScore;

    @ApiModelProperty(value = "用户 id")
    private Long userId;

    @ApiModelProperty(value="用户答案")
    private List<String> choicesResult;

    @ApiModelProperty(value="应用信息")
    private AppVO appVO;



}
