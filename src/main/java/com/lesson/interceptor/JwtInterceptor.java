package com.lesson.interceptor;

import com.lesson.common.exception.AuthException;
import com.lesson.context.UserContext;
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
            throw new AuthException("未登录或token已过期");
        }

        // 处理Bearer前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            // 验证token
            jwtUtil.parseToken(token);
            
            // 获取用户ID和机构ID
            Long userId = jwtUtil.getUserId(token);
            Long orgId = jwtUtil.getOrgId(token);
            
            // 将用户ID和机构ID存入request属性中，方便后续使用
            request.setAttribute("userId", userId);
            request.setAttribute("orgId", orgId);
            
            // 设置用户上下文
            UserContext.setCurrentUserId(userId);
            UserContext.setCurrentInstitutionId(orgId);
            
            // 从数据库获取用户信息，设置校区ID
            // 这里需要注入用户服务，暂时先不设置校区ID，在具体业务中处理
            
            return true;
        } catch (ExpiredJwtException e) {
            throw new AuthException("token已过期");
        } catch (Exception e) {
            throw new AuthException("token无效");
        }
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清理用户上下文
        UserContext.clear();
    }
} 