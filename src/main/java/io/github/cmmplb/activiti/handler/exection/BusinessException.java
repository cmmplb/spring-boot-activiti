package io.github.cmmplb.activiti.handler.exection;


import io.github.cmmplb.activiti.result.HttpCodeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author plb
 * @date 2020/6/12 9:45
 * 业务异常
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = -7787200346109889949L;

    private int code;

    private int statusCode = 200;

    private String message;

    public BusinessException(String message) {
        this.code = HttpCodeEnum.INTERNAL_SERVER_ERROR.getCode();
        this.message = message;
    }

    public BusinessException(HttpCodeEnum httpCodeEnum) {
        this.code = httpCodeEnum.getCode();
        this.message = httpCodeEnum.getMessage();
    }

    public BusinessException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public BusinessException(int code, int statusCode, String message) {
        this.code = code;
        this.statusCode = statusCode;
        this.message = message;
    }

    public BusinessException(int code) {
        this.code = code;
        this.message = HttpCodeEnum.INTERNAL_SERVER_ERROR.getMessage();
    }
}
