package com.zhiqiong.scoring.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一返回的结果封装类
 *
 * @author 20231
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScoringResult {
    @ApiModelProperty(value = "结果")
    private String typeName;
    @ApiModelProperty(value = "结果描述")
    private String description;
    @ApiModelProperty(value = "分数")
    private int score;
    @ApiModelProperty(value = "结果图标")
    private String resultPicture;
    @ApiModelProperty(value = "结果属性")
    private String resultProp;
    @ApiModelProperty(value = "结果得分范围，如 80，表示 80及以上的分数命中此结果")
    private Integer resultScoreRange;
}