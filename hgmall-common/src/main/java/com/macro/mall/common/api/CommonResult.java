package com.macro.mall.common.api;

import lombok.Data;

/**
 * 通用返回结果
 */
@Data
public class CommonResult<T> {
    private long code;
    private String message;
    private T data;

    protected CommonResult() {
    }

    protected CommonResult(long code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功返回结果
     */
    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<T>(200, "操作成功", data);
    }

    /**
     * 成功返回结果
     */
    public static <T> CommonResult<T> success(String message, T data) {
        return new CommonResult<T>(200, message, data);
    }

    /**
     * 失败返回结果
     */
    public static <T> CommonResult<T> failed(String message) {
        return new CommonResult<T>(500, message, null);
    }

    /**
     * 失败返回结果
     */
    public static <T> CommonResult<T> failed(long code, String message) {
        return new CommonResult<T>(code, message, null);
    }
}

