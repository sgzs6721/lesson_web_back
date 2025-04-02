package com.lesson.common;

import lombok.Data;

/**
 * 统一返回结果
 */
@Data
public class Result<T> {
    /**
     * 状态码
     */
    private Integer code;

    /**
     * 状态
     */
    private Boolean success;

    /**
     * 消息
     */
    private String msg;

    /**
     * 数据
     */
    private T data;

    /**
     * 成功
     *
     * @param data 数据
     * @param <T>  数据类型
     * @return 结果
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setSuccess(true);
        result.setMsg("操作成功");
        result.setData(data);
        return result;
    }

    /**
     * 成功
     *
     * @param msg  消息
     * @param data 数据
     * @param <T>  数据类型
     * @return 结果
     */
    public static <T> Result<T> success(String msg, T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setSuccess(true);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    /**
     * 失败
     *
     * @param code 状态码
     * @param msg  消息
     * @param <T>  数据类型
     * @return 结果
     */
    public static <T> Result<T> error(Integer code, String msg) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setSuccess(false);
        result.setMsg(msg);
        return result;
    }

    /**
     * 失败
     *
     * @param msg 消息
     * @param <T> 数据类型
     * @return 结果
     */
    public static <T> Result<T> error(String msg) {
        return error(500, msg);
    }
} 