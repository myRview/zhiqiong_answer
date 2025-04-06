package com.zhiqiong.security;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import com.zhiqiong.common.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

/**
 * 未登录处理
 *
 * @author huangkun
 * @date 2025/3/30 10:34
 */
@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {
    /**
     * 当未登录或者token失效访问接口时，自定义的返回结果
     *
     * @param request
     * @param response
     * @param authException
     * @throws IOException
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        log.error("JWT认证失败，无法访问系统资源，请求访问：{},异常信息：{}", request.getRequestURI(),authException.getMessage());
        response.setStatus(HttpStatus.HTTP_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().print(JSONUtil.toJsonStr(ResponseResult.fail("用户未登录")));
    }
}
