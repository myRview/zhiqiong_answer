package com.zhiqiong.model.vo.question;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 20231
 */
@Data
@ApiModel(value="选项VO")
public class OptionVO {

    @ApiModelProperty(value = "选项内容")
    private String result;
    @ApiModelProperty(value = "选项显示文本")
    private String value;
    @ApiModelProperty(value = "选项标识")
    private String key;
}