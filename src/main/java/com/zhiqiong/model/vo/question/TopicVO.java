package com.zhiqiong.model.vo.question;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author huangkun
 * @date 2025/3/19 20:25
 */
@Data
@ApiModel(value = "题目实体VO")
public class TopicVO {

    @ApiModelProperty(value = "题目id")
    private Long id;

    @ApiModelProperty(value = "题目内容")
    private String title;

    @ApiModelProperty(value = "选项列表")
    private List<OptionVO> optionVOList;
}
