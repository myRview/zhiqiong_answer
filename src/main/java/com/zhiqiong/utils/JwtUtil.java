package com.zhiqiong.utils;

import com.zhiqiong.common.constant.TokenConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Map;

/**
 * @author 20231
 */
public class JwtUtil {

    /**
     * 签名密钥
     */
    private final static String TOKEN_STRING_KEY = "token_string_key";

    /**
     * 生成令牌
     *
     * @param claims
     * @return
     */
    public static String createToken(Map<String, Object> claims) {
        String token = Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, TOKEN_STRING_KEY)
                .compact();
        return token;
    }

    /**
     * 解析令牌
     *
     * @return
     */
    public static Claims parseToken(String token) {
        return Jwts.parser().setSigningKey(TOKEN_STRING_KEY).parseClaimsJws(token).getBody();
    }

    /**
     * 获取用户ID
     * @param token
     * @return
     */
    public static String getUserId(String token) {
        Claims claims = parseToken(token);
        return String.valueOf(claims.get(TokenConstants.USER_ID));
    }
}
