package com.zhiqiong.model.vo.question;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author huangkun
 * @date 2025/3/24 22:17
 */
@Data
public class AIGeneratorRequestVO {
    @ApiModelProperty(value = "应用id")
    private Long appId;
    @ApiModelProperty(value = "题目数量")
    private Integer questionNum=10;
    @ApiModelProperty(value = "选项数量")
    private Integer optionNum=4;
}
