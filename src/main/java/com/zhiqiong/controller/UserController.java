package com.zhiqiong.controller;


import com.zhiqiong.common.ResponseResult;
import com.zhiqiong.model.vo.IdVO;
import com.zhiqiong.model.vo.RegisterUserVO;
import com.zhiqiong.model.vo.UserVO;
import com.zhiqiong.service.UserService;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 用户 前端控制器
 * </p>
 *
 * @author hk
 * @since 2025-03-12
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/register")
    @ApiOperation(value = "注册用户")
    public ResponseResult register(@RequestBody RegisterUserVO registerUserVO) {
        boolean register = userService.register(registerUserVO);
        return register ? ResponseResult.success(register) : ResponseResult.fail();
    }


    @PostMapping("/list")
    @ApiOperation(value = "根据条件获取用户")
    public ResponseResult<List<UserVO>> selectList(@RequestParam(value = "userName", required = false) String userName,@RequestParam(value = "userRole", required = false) String userRole) {
        List<UserVO> userVOList = userService.selectList(userName,userRole);
        return ResponseResult.success(userVOList);
    }

    @PostMapping("/add")
    @ApiOperation(value = "新增用户")
    public ResponseResult addUser(@RequestBody UserVO userVO) {
        userService.addUser(userVO);
        return ResponseResult.success();
    }

    @DeleteMapping("/delete")
    @ApiOperation(value = "删除用户")
    public ResponseResult deleteUser(@RequestBody IdVO idVO) {
        userService.deleteUser(idVO);
        return ResponseResult.success();
    }

    @PostMapping("/update")
    @ApiOperation(value = "修改用户")
    public ResponseResult updateUser(@RequestBody UserVO userVO) {
        userService.updateUser(userVO);
        return ResponseResult.success();
    }


}
