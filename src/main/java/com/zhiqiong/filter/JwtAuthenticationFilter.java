package com.zhiqiong.filter;

import com.zhiqiong.manager.token.TokenService;
import com.zhiqiong.security.UserDetailVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author 20231
 */
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Resource
    private TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // 获取当前登录用户
        UserDetailVO loginUser = tokenService.getLoginUser(request);
        // 如果当前登录用户不为空
        if (loginUser != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            tokenService.verifyToken(loginUser);
            // 创建一个UsernamePasswordAuthenticationToken对象，用于存储当前登录用户的信息
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
            // 设置当前登录用户的详细信息
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            // 将当前登录用户的信息存储到SecurityContextHolder中
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        // 继续执行过滤器链
        chain.doFilter(request, response);
    }
}