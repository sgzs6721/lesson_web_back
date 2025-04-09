package com.lesson.interceptor;

import com.lesson.utils.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 放行OPTIONS请求
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        // 从请求头中获取token
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("未登录或token已过期");
        }

        try {
            // 验证token
            jwtUtil.parseToken(token);
            // 将用户ID和机构ID存入request属性中，方便后续使用
            request.setAttribute("userId", jwtUtil.getUserId(token));
            request.setAttribute("orgId", jwtUtil.getOrgId(token));
            return true;
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("token已过期");
        } catch (Exception e) {
            throw new RuntimeException("token无效");
        }
    }
} 