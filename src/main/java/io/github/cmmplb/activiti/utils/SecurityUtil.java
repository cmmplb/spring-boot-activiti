package io.github.cmmplb.activiti.utils;

import io.github.cmmplb.activiti.handler.exection.BusinessException;
import io.github.cmmplb.activiti.result.HttpCodeEnum;
import io.github.cmmplb.activiti.security.UserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author penglibo
 * @date 2024-11-01 10:25:34
 * @since jdk 1.8
 */
public class SecurityUtil {

    /**
     * 获取用户名
     */
    public static String getUserName() {
        return getUser().getUsername();
    }

    /**
     * 获取用户
     */
    public static UserDetails getUser() {
        Authentication authentication = getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return (UserDetails) principal;
        }
        throw new BusinessException(HttpCodeEnum.UNAUTHORIZED);
    }

    /**
     * 获取Authentication
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}