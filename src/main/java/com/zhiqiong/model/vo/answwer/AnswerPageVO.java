package com.zhiqiong.model.vo.answwer;

import com.zhiqiong.common.PageBaseVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author huangkun
 * @date 2025/4/1 16:38
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AnswerPageVO extends PageBaseVO {
    private Long appId;
}
