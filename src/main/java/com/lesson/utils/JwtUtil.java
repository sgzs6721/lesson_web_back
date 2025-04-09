package com.lesson.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    private static final long EXPIRE_TIME = 24 * 60 * 60 * 1000; // 24小时
    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    /**
     * 生成token
     * @param userId 用户ID
     * @param orgId 机构ID
     * @return token字符串
     */
    public String generateToken(Long userId, Long orgId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("orgId", orgId);
        
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_TIME))
                .signWith(key)
                .compact();
    }

    /**
     * 解析token
     * @param token token字符串
     * @return Claims对象
     */
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从token中获取用户ID
     * @param token token字符串
     * @return 用户ID
     */
    public Long getUserId(String token) {
        Claims claims = parseToken(token);
        return Long.valueOf(claims.get("userId").toString());
    }

    /**
     * 从token中获取机构ID
     * @param token token字符串
     * @return 机构ID
     */
    public Long getOrgId(String token) {
        Claims claims = parseToken(token);
        return Long.valueOf(claims.get("orgId").toString());
    }
} 