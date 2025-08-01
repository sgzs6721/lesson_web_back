package com.lesson.interceptor;

import com.lesson.common.Result;
import com.lesson.context.UserContext;
import com.lesson.service.UserService;
import com.lesson.vo.user.UserLoginVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private ApplicationContext applicationContext;
    
    // 延迟获取UserService，避免循环依赖
    private UserService getUserService() {
        return applicationContext.getBean(UserService.class);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUri = request.getRequestURI();
        log.info("AuthenticationInterceptor - 请求URI: {}", requestUri);
        
        // 如果是认证相关的接口或机构注册接口，直接放行
        if (requestUri.startsWith("/lesson/api/auth") || requestUri.startsWith("/lesson/api/institution/register")) {
            log.info("AuthenticationInterceptor - 认证接口或机构注册接口放行");
            return true;
        }

        // 获取token
        String token = request.getHeader("Authorization");
        log.info("AuthenticationInterceptor - Token: {}", token);
        
        if (token == null || token.isEmpty()) {
            log.warn("AuthenticationInterceptor - Token为空，拦截请求");
            handleUnauthorized(response, "请先登录");
            return false;
        }

        // 从token中获取用户信息 - 使用延迟加载的方式获取UserService
        UserLoginVO user = getUserService().getUserByToken(token);
        if (user == null) {
            log.warn("AuthenticationInterceptor - 用户信息为空，拦截请求");
            handleUnauthorized(response, "登录已过期，请重新登录");
            return false;
        }

        log.info("AuthenticationInterceptor - 用户信息: {}", user);

        // 设置用户上下文
        UserContext.setCurrentUserId(user.getUserId());
        UserContext.setCurrentInstitutionId(user.getInstitutionId());

        // 设置请求属性，用于权限控制
        request.setAttribute("orgId", user.getInstitutionId());
        request.setAttribute("campusId", user.getCampusId());

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }

    private void handleUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(objectMapper.writeValueAsString(Result.error(401, message)));
    }
} 