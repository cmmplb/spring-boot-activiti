package io.github.cmmplb.activiti.security.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import io.github.cmmplb.activiti.constants.SecurityConstant;
import io.github.cmmplb.activiti.handler.exection.BusinessException;
import io.github.cmmplb.activiti.security.UserDetails;
import io.github.cmmplb.activiti.utils.TimeExpiredPoolCacheUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author penglibo
 * @date 2024-11-06 17:04:11
 * @since jdk 1.8
 * 自定义认证信息验证过滤器，根据 authorization 获取用户信息后放入到 SecurityContext 上下文中
 */
public class AuthenticationFilter extends BasicAuthenticationFilter {

    private final ConcurrentHashMap<String, UserDetails> cache = new ConcurrentHashMap<>();

    public AuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request.getRequestURI().contains("login")) {
            // 登录接口，直接放行
            chain.doFilter(request, response);
            return;
        }
        // 获取请求头中的token
        String authorization = request.getHeader(SecurityConstant.AUTHORIZATION);
        if (StringUtils.isEmpty(authorization)) {
            // 交给其他的过滤器处理
            chain.doFilter(request, response);
            return;
        }
        // 提取 token, 基于内存缓存
        String userDetailsJson = null;
        try {
            userDetailsJson = TimeExpiredPoolCacheUtil.getInstance().get(SecurityConstant.AUTHORIZATION_PREFIX + authorization);
        } catch (Exception e) {
            throw new BusinessException("读取缓存信息失败");
        }
        if (StringUtils.isNotEmpty(userDetailsJson)) {
            UserDetails userDetails = JSON.parseObject(userDetailsJson, new TypeReference<UserDetails>() {
            });
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails, "N/A", userDetails.getAuthorities());
            // 将用户信息放入到 SecurityContext 中
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        }
        chain.doFilter(request, response);
    }
}