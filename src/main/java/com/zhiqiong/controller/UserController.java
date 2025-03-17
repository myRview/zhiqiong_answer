package com.zhiqiong.controller;


import com.zhiqiong.common.ResponseResult;
import com.zhiqiong.model.vo.IdVO;
import com.zhiqiong.model.vo.user.LoginUserVO;
import com.zhiqiong.model.vo.user.RegisterUserVO;
import com.zhiqiong.model.vo.user.UserVO;
import com.zhiqiong.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
@Api(tags = "用户管理")
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/register")
    @ApiOperation(value = "注册用户")
    public ResponseResult register(@RequestBody RegisterUserVO registerUserVO) {
        boolean register = userService.register(registerUserVO);
        return register ? ResponseResult.success(register) : ResponseResult.fail();
    }

    @PostMapping("/login")
    @ApiOperation(value = "登录")
    public ResponseResult login(@RequestBody LoginUserVO loginUserVO) {
        String token = userService.login(loginUserVO);
        return ResponseResult.success(token);
    }

    @PostMapping("/logout")
    @ApiOperation(value = "退出登录")
    public ResponseResult logout() {
        userService.logout();
        return ResponseResult.success();
    }


    @PostMapping("/reset/pwd")
    @ApiOperation(value = "重置密码")
    public ResponseResult resetPassword(@RequestBody IdVO idVO) {
         userService.resetPassword(idVO);
         return ResponseResult.success();
    }

    @PostMapping("/update/pwd")
    @ApiOperation(value = "修改密码")
    public ResponseResult updatePassword(@RequestBody UserVO userVO) {
        userService.updatePassword(userVO);
        return ResponseResult.success();
    }

    @GetMapping("/id")
    @ApiOperation(value = "根据条件获取用户")
    public ResponseResult<UserVO> getUserInfo(@RequestParam(value = "id")Long id) {
        UserVO userVO = userService.getUserInfo(id);
        return ResponseResult.success(userVO);
    }


    @GetMapping("/list")
    @ApiOperation(value = "根据条件获取用户")
    public ResponseResult<List<UserVO>> selectList(@RequestParam(value = "userName", required = false) String userName,
                                                   @RequestParam(value = "userRole", required = false) String userRole) {
        List<UserVO> userVOList = userService.selectList(userName, userRole);
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
