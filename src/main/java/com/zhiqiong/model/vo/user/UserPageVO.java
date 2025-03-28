package com.zhiqiong.model.vo.user;

import com.zhiqiong.common.PageBaseVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author huangkun
 * @date 2025/3/27 21:59
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserPageVO extends PageBaseVO {

     private String userName;

     private String userAccount;

     private String userRole;

}
