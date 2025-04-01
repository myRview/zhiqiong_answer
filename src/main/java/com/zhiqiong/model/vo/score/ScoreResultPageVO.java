package com.zhiqiong.model.vo.score;

import com.zhiqiong.common.PageBaseVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author huangkun
 * @date 2025/3/30 21:38
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ScoreResultPageVO extends PageBaseVO {

    @ApiModelProperty(value = "结果名称，如物流师")
    private String resultName;

    @ApiModelProperty(value = "应用 id")
    private Long appId;


}
