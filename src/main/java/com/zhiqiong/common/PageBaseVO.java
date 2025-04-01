package com.zhiqiong.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author huangkun
 * @date 2025/3/27 21:56
 */
@Data
public class PageBaseVO implements Serializable {

    private Integer pageNum = 1;
    private Integer pageSize =10;
}
