package com.zhiqiong.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 评分结果表
 * </p>
 *
 * @author hk
 * @since 2025-03-13
 */
@Data
@ApiModel(value="ScoringResultVO对象", description="评分结果表")
public class ScoringResultVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty(value = "结果名称，如物流师")
    private String resultName;

    @ApiModelProperty(value = "结果描述")
    private String resultDesc;

    @ApiModelProperty(value = "结果图片")
    private String resultPicture;

    @ApiModelProperty(value = "结果属性集合 JSON，如 [I,S,T,J]")
    private String resultProp;

    @ApiModelProperty(value = "结果得分范围，如 80，表示 80及以上的分数命中此结果")
    private Integer resultScoreRange;

    @ApiModelProperty(value = "应用 id")
    private Long appId;

    @ApiModelProperty(value = "创建用户 id")
    private Long userId;


}
