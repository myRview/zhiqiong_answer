package com.zhiqiong.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 应用表
 * </p>
 *
 * @author hk
 * @since 2025-03-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("app")
@ApiModel(value="AppEntity对象", description="应用表")
public class AppEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
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

    @ApiModelProperty(value = "审核状态：0-待审核, 1-通过, 2-拒绝")
    private Integer reviewStatus;

    @ApiModelProperty(value = "审核信息")
    private String reviewMessage;

    @ApiModelProperty(value = "审核人 id")
    private Long reviewerId;

    @ApiModelProperty(value = "审核时间")
    private Date reviewTime;

    @ApiModelProperty(value = "创建用户 id")
    private Long userId;

    @ApiModelProperty(value = "是否删除")
    private Integer status;


}
