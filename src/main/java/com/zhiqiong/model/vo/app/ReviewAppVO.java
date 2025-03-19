package com.zhiqiong.model.vo.app;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author huangkun
 * @date 2025/3/19 19:26
 */
@Data
public class ReviewAppVO {

    @ApiModelProperty(value = "审核信息")
    private String reviewMessage;

    @ApiModelProperty(value = "审核状态：0-待审核, 1-通过, 2-拒绝")
    private Integer reviewStatus;

    @ApiModelProperty(value = "应用 id")
    private Long appId;

}
