package com.zhiqiong.model.vo.app;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author huangkun
 * @date 2025/3/25 16:08
 */
@Data
public class OperateAppVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "应用名")
    private String appName;

    @ApiModelProperty(value = "应用描述")
    private String appDesc;

    @ApiModelProperty(value = "应用图标")
    private String appIcon;

    @ApiModelProperty(value = "应用类型（0-得分类，1-测评类）")
    private Integer appType;

    @ApiModelProperty(value = "评分策略（0-自定义，1-AI）")
    private Integer scoringStrategy;
}
