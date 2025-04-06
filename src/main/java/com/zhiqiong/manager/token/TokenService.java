package com.zhiqiong.manager.token;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.zhiqiong.cache.RedisCacheManager;
import com.zhiqiong.common.constant.CacheConstants;
import com.zhiqiong.common.constant.TokenConstants;
import com.zhiqiong.security.UserDetailVO;
import com.zhiqiong.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author huangkun
 * @date 2025/3/30 11:10
 */
@Component
@Slf4j
public class TokenService {


    private static final long MILLIS_MINUTE = 60 * 1000L;
    private static final long MILLIS_MINUTE_TEN = 60 * 60 * 1000L;

    @Value("${token.expireTime}")
    private int expireTime;
    @Resource
    private RedisCacheManager redisCacheManager;

    /**
     * 获取用户信息
     *
     * @param request
     * @return
     */
    public UserDetailVO getLoginUser(HttpServletRequest request) {
        String token = request.getHeader(TokenConstants.USER_TOKEN);
        if (StrUtil.isNotBlank(token)) {
            try {
                String userId = JwtUtil.getUserId(token);
                UserDetailVO userDetailVO = null;
                String cacheMapValue = redisCacheManager.getCacheValue(getKey(userId));
                if (StrUtil.isNotBlank(cacheMapValue)) {
                    userDetailVO = JSONUtil.toBean(cacheMapValue, UserDetailVO.class);
                }
                return userDetailVO;
            } catch (Exception e) {
                log.error("获取登录用户失败", e);
            }
        }
        return null;
    }

    /**
     * 创建令牌
     *
     * @param loginUser 登录用户
     * @return
     */
    public String createToken(UserDetailVO loginUser) {
        HashMap<String, Object> map = new HashMap<>();
        Long userId = loginUser.getUserId();
        map.put(TokenConstants.USER_ID, userId);
        String token = JwtUtil.createToken(map);
        loginUser.setToken(token);
        refreshToken(loginUser);
        return token;
    }

    /**
     * 刷新令牌有效期
     *
     * @param loginUser 登录用户
     */
    public void refreshToken(UserDetailVO loginUser) {
        long time = System.currentTimeMillis();
        loginUser.setLoginTime(time);
        loginUser.setExpireTime(time + expireTime * MILLIS_MINUTE);
        String userId = loginUser.getUserId().toString();
        redisCacheManager.setCacheValue(getKey(userId), JSONUtil.toJsonStr(loginUser), expireTime, TimeUnit.MINUTES);
    }

    private String getKey(String userId) {
        return CacheConstants.LOGIN_USER + userId;
    }

    /**
     * 验证令牌有效期，相差不足60分钟，自动刷新缓存
     *
     * @param loginUser
     * @return 令牌
     */
    public void verifyToken(UserDetailVO loginUser) {
        long expireTime = loginUser.getExpireTime();
        long currentTime = System.currentTimeMillis();
        if (expireTime - currentTime <= MILLIS_MINUTE_TEN) {
            refreshToken(loginUser);
        }
    }


    /**
     * 删除用户缓存信息
     *
     * @param userId 用户ID
     */
    public void delLoginUser(String userId) {
        if (StrUtil.isNotEmpty(userId)) {
            redisCacheManager.delete(getKey(userId));
        }
    }
}
