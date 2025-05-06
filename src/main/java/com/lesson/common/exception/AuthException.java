package com.lesson.common.exception;

import lombok.Getter;

/**
 * 认证异常
 */
@Getter
public class AuthException extends RuntimeException {
    
    private int code = 401; // HTTP 401 Unauthorized
    
    public AuthException(String message) {
        super(message);
    }
    
    public AuthException(String message, int code) {
        super(message);
        this.code = code;
    }
} 