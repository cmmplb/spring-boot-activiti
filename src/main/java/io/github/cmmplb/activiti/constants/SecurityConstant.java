package io.github.cmmplb.activiti.constants;

/**
 * @author penglibo
 * @date 2024-11-06 17:17:33
 * @since jdk 1.8
 */
public interface SecurityConstant {

    // 登录认证请求头
    String AUTHORIZATION = "Authorization";

    // 认证请求头缓存前缀
    String AUTHORIZATION_PREFIX = AUTHORIZATION + ":prefix:";
}
