package com.lesson.interceptor;

import com.lesson.annotation.RequirePermission;
import com.lesson.common.Result;
import com.lesson.service.PermissionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class PermissionInterceptor implements HandlerInterceptor {

    private final PermissionService permissionService;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUri = request.getRequestURI();
        log.info("PermissionInterceptor - 开始检查权限, URI: {}", requestUri);
        
        if (!(handler instanceof HandlerMethod)) {
            log.info("PermissionInterceptor - 不是HandlerMethod，放行");
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequirePermission requirePermission = handlerMethod.getMethodAnnotation(RequirePermission.class);
        if (requirePermission == null) {
            requirePermission = handlerMethod.getBeanType().getAnnotation(RequirePermission.class);
        }

        if (requirePermission == null) {
            log.info("PermissionInterceptor - 无需权限，放行");
            return true;
        }

        String[] permissions = requirePermission.value();
        log.info("PermissionInterceptor - 需要权限: {}", Arrays.toString(permissions));
        
        if (!permissionService.hasPermission(permissions)) {
            log.warn("PermissionInterceptor - 权限不足，拦截请求");
            handleUnauthorized(response, "没有操作权限");
            return false;
        }

        log.info("PermissionInterceptor - 权限验证通过");
        return true;
    }

    private void handleUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write(objectMapper.writeValueAsString(Result.error(403, message)));
    }
}