package com.cmmplb.activiti.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author plb
 * @date 2020/6/8 15:55
 * http 状态码
 * 2xx 一般表示请求处理成功
 * 200 OK，请求处理成功
 * 201 Created，请求处理成功，并且新资源已经创建
 * 202 Accepted，请求已经接受，正在处理，尚未处理完成
 * 204 No Content，响应内容为空，在 asp.net core 中返回一个 Json(null) 的时候就会是一个 NoContent 的结果
 * 3xx 一般表示重定向
 * 301 Moved Permanently 永久重定向
 * 302 Found 临时重定向
 * 307 Temporary Redirect 临时重定向请求
 * 308 Permanent Redirect 永久重定向请求
 * 这几个重定向的区别：
 * 301、302 只支持 GET 请求，如果是 POST 请求，重定向后会使用 GET 请求且 Body 数据会丢失
 * 307、308 支持 POST 请求，在 POST 重定向的时候会带上原来请求的 body 再请求新的地址，body 数据不会丢失
 * 302、307 是临时重定向，
 * 301、308 是永久重定向，是允许缓存的，浏览器可以缓存
 * 304 Not Modified，资源未发生修改，可以直接使用浏览器本地缓存
 * 4xx 一般表示客户端请求错误
 * 400 BadRequest，错误请求，一般用来表示请求参数异常，比如请求的某一个参数不能为空，但实际请求是空
 * 401 Unauthorized，未授权，资源需要授权或登录，而用户没有登录或者没有提供访问所需的 Token 等
 * 403 Forbidden，禁止访问，当前用户没有权限访问资源，如需要Admin角色的用户，但是请求的用户没有这个角色
 * 404 NotFound，未找到资源，资源不存在
 * 405 Method Not Allowed，不允许的方法调用，资源不支持的请求方法，比如资源只允许 GET 请求，但是实际请求使用了 POST 或 DELETE 方法
 * 406 Not Acceptable，请求的资源客户端不支持处理，比如客户端希望获取 xml 的响应，但是服务器端只支持 JSON 响应
 * 408 Request Timeout, 请求处理超时
 * 409 Conflict，请求资源冲突，常发生在 PUT 更新资源信息时发生，比如更新时指定资源的 ETAG，但是PUT请求时，资源的 ETAG 已经发生变化
 * 410 Gone，请求资源在源服务器上不再可用
 * 411 Length Required，请求需要携带 Content-Length 请求头
 * 412 Precondition Failed，请求预检失败，请求的某些参数不符合条件
 * 413 Payload Too Large，请求的参数太大，请求的 body 过大，服务器拒绝处理
 * 414 URI Too Long，请求的 URI 地址太长，服务器拒绝处理
 * 415 Unsupported Media Type，不支持的媒体类型或不支持的编码，比如服务器只支持处理 JSON 请求，但是请求是 xml 格式
 * 5xx 一般表示服务端错误
 * 500 Internal Server Error，服务器内部错误
 * 501 Not Implemented 服务器不支持需要处理请求的功能，比如图片压缩等处理
 * 502 Bad Gateway 反向代理或网关找不到处理请求的服务器
 * 503 Service Unavailable 服务不可用
 * 504 Gateway Timeout 网关超时
 * 505 HTTP Version Not Supported，不支持的 HTTP 版本，服务器不支持或拒绝处理这个 HTTP 版本的请求
 */

@Getter
@AllArgsConstructor
public enum HttpCodeEnum {

    /**
     * http状态码枚举
     */
    OK(200, "操作成功"),
    INVALID_REQUEST(400, "请求参数错误,请检查参数"),
    UNAUTHORIZED(401, "未授权-未登录"),
    FORBIDDEN(403, "禁止访问-未授权"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "不允许的方法"),
    NOT_ACCEPTABLE(406, "请求的格式不正确"),
    REQUEST_ENTITY_TOO_LARGE(413, "上传文件资源大小过大"),
    REQUIRED_REQUEST_BODY_IS_MISSING(424, "请求参数错误，请检查参数"),
    METHOD_ARGUMENT_TYPE_MISMATCH(425, "方法参数类型不匹配异常,请确认请求路径、请求方式是否正确"),
    INTERNAL_SERVER_ERROR(500, "服务器繁忙"),
    FAIL(501, "操作失败"),
    FEIGN_ERROR(503, "内部服务调用异常"),

    BAD_CREDENTIALS(600, "用户名或密码错误"),


    NULL_POINT_ERROR(900, "空指针异常"),
    CLASS_CAST_ERROR(901, "类型转换异常"),
    NO_SUCH_MESSAGE_ERROR(902, "消息获取异常"),
    ARITHMETIC_ERROR(903, "算术异常"),
    REDIS_CONNECTION_ERROR(904, "redis连接异常"),
    RETRY_ERROR(998, "乐观锁异常重试失败异常"),
    LOCKER_ERROR(999, "触发乐观锁异常"),
    EXCEL_ERROR(1000, "excel异常"),
    PDF_ERROR(1001, "pdf异常"),

    INVALID_ERROR(1500, "配置异常"),

    ;

    private final int code;
    private final String message;

    public static String getMessage(int code) {
        for (HttpCodeEnum httpCodeEnum : values()) {
            if (httpCodeEnum.getCode() == (code)) {
                return httpCodeEnum.message;
            }
        }
        return null;
    }
}
