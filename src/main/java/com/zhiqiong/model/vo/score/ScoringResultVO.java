package com.zhiqiong.model.vo.score;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author huangkun
 * @date 2025/3/30 21:38
 */
@Data
public class ScoringResultVO implements Serializable {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "结果名称，如物流师")
    private String resultName;

    @ApiModelProperty(value = "结果描述")
    private String resultDesc;

    @ApiModelProperty(value = "结果图片")
    private String resultPicture;

    @ApiModelProperty(value = "结果属性")
    private String resultProp;

    @ApiModelProperty(value = "结果得分范围，如 80，表示 80及以上的分数命中此结果")
    private Integer resultScoreRange;

    @ApiModelProperty(value = "应用 id")
    private Long appId;

    @ApiModelProperty(value = "创建用户 id")
    private Long userId;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

}
