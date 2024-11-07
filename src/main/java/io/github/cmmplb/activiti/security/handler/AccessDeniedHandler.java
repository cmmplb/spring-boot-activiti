package io.github.cmmplb.activiti.security.handler;

import com.alibaba.fastjson.JSON;
import io.github.cmmplb.activiti.result.HttpCodeEnum;
import io.github.cmmplb.activiti.result.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author penglibo
 * @date 2024-11-06 21:44:39
 * @since jdk 1.8
 * 权限不足处理
 */

@Slf4j
public class AccessDeniedHandler implements org.springframework.security.web.access.AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.info("禁止访问-未授权:{}", request.getRequestURI());
        response.setStatus(HttpCodeEnum.FORBIDDEN.getCode());
        response.setContentType("application/json");
        response.getWriter().write(JSON.toJSONString(ResultUtil.error(HttpCodeEnum.FORBIDDEN)));
    }
}
