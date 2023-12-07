package com.cmmplb.activiti.result;

/**
 * @author plb
 * @date 2020/6/8 15:56
 */

public class ResultUtil<T> {

    /**
     * 请求成功
     *
     * @return T
     */
    public static <T> Result<T> success() {
        return new Result<T>(HttpCodeEnum.OK.getCode(), HttpCodeEnum.OK.getMessage());
    }

    /**
     * 成功请求
     *
     * @param data T
     * @return T
     */
    public static <T> Result<T> success(T data) {
        return new Result<T>(HttpCodeEnum.OK.getCode(), HttpCodeEnum.OK.getMessage(), data);
    }

    /**
     * 操作失败
     *
     * @return T
     */
    public static <T> Result<T> fail() {
        return new <T>Result<T>(HttpCodeEnum.FAIL.getCode(), HttpCodeEnum.FAIL.getMessage());
    }

    /**
     * 操作失败
     *
     * @return T
     */
    public static <T> Result<T> fail(T data) {
        return new Result<T>(HttpCodeEnum.FAIL.getCode(), HttpCodeEnum.FAIL.getMessage(), data);
    }

    /**
     * 操作失败
     *
     * @return T
     */
    public static <T> Result<T> fail(int code, String msg) {
        return new Result<T>(code, msg, null);
    }

    /**
     * 操作失败
     *
     * @param httpCodeEnum httpCodeEnum
     * @return T
     */
    public static <T> Result<T> error(HttpCodeEnum httpCodeEnum) {
        return new Result<T>(httpCodeEnum);
    }

    /**
     * 自定义返回
     *
     * @param message message
     * @return T
     */
    public static <T> Result<T> custom(String message) {
        return new Result<T>(HttpCodeEnum.FAIL.getCode(), message);
    }


    /**
     * 自定义返回
     *
     * @param e HttpCodeEnum
     * @return T
     */
    public static <T> Result<T> custom(HttpCodeEnum e) {
        return new Result<T>(e.getCode(), e.getMessage());
    }

    /**
     * 自定义返回
     *
     * @param code code
     * @return T
     */
    public static <T> Result<T> custom(int code) {
        return new Result<T>(code, HttpCodeEnum.getMessage(code));
    }

    /**
     * 自定义返回
     *
     * @param error String
     * @return T
     */
    public static <T> Result<T> custom(int code, String error) {
        return new Result<T>(code, error);
    }
}
