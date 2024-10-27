package io.github.cmmplb.activiti.result;

/**
 * @author plb
 * @date 2020/6/8 15:56
 */

public class ResultUtil<T> {

    /**
     * 请求成功
     * @return T
     */
    public static <T> Result<T> success() {
        return new Result<T>(HttpCodeEnum.OK.getCode(), HttpCodeEnum.OK.getMessage());
    }

    /**
     * 成功请求
     * @param data T
     * @return T
     */
    public static <T> Result<T> success(T data) {
        return new Result<T>(HttpCodeEnum.OK.getCode(), HttpCodeEnum.OK.getMessage(), data);
    }

    /**
     * 操作失败
     * @param httpCodeEnum httpCodeEnum
     * @return T
     */
    public static <T> Result<T> error(HttpCodeEnum httpCodeEnum) {
        return new Result<T>(httpCodeEnum);
    }


    /**
     * 操作失败
     * @param code  错误码
     * @param error 错误信息
     * @return T
     */
    public static <T> Result<T> error(int code, String error) {
        return new Result<T>(code, error);
    }

}
