package com.zhiqiong.model.vo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author huangkun
 * @date 2025/3/13 19:58
 */

@Data
@ApiModel(value="RegisterUserVO对象", description="用户登录")
public class LoginUserVO {

    @ApiModelProperty(value = "账号")
    private String userAccount;
    @ApiModelProperty(value = "密码")
    private String userPassword;

}
