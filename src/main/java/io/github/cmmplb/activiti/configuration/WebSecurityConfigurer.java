package io.github.cmmplb.activiti.configuration;

import io.github.cmmplb.activiti.security.filter.AuthenticationFilter;
import io.github.cmmplb.activiti.security.handler.AccessDeniedHandler;
import io.github.cmmplb.activiti.security.handler.ResourceAuthenticationEntryPoint;
import io.github.cmmplb.activiti.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author penglibo
 * @date 2024-10-19 22:36:23
 * @since jdk 1.8
 */

@Configuration
public class WebSecurityConfigurer {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /*
     * 配置权限相关的配置，spring 官方推荐使用 Lambda 表达式的写法
     * spring security 6.x 版本之后, and() 方法将被移除，
     * */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 配置请求的权限
        http.authorizeHttpRequests(registry -> {
                    // 配置不需要安全拦截url
                    registry.antMatchers("/authentication/login").permitAll();
                    // 其他路径都要进行拦截
                    registry.anyRequest().authenticated();
                }
        );
        // 设置异常处理器
        http.exceptionHandling(handlingConfigurer -> handlingConfigurer
                // 权限不足处理器, 当访问的资源需要权限，但是用户没有权限，就会进入这里进行处理
                .accessDeniedHandler(new AccessDeniedHandler())
                // 异常处理器, 如果不配置, 默认是 Http403ForbiddenEntryPoint 处理器, 也就是响应 403 错误
                // 因为是前后端分离, 所以需要处理响应体结构, 并将错误信息返回给前端
                .authenticationEntryPoint(new ResourceAuthenticationEntryPoint())
        );
        // 自定义 token 认证过滤器, 根据 token 获取用户信息后放入到 SecurityContext 上下文中, 执行顺序放在 UsernamePasswordAuthenticationFilter 之前
        http.addFilterBefore(new AuthenticationFilter(authenticationManager())
                , UsernamePasswordAuthenticationFilter.class);
        // 关闭csrf保护
        http.csrf(AbstractHttpConfigurer::disable);
        // SessionCreationPolicy.ALWAYS – 会话将始终创建（如果它不存在）。
        // SessionCreationPolicy.NEVERSpring, Security 永远不会创建 HttpSession, 但如果它已经存在, 则会使用它（可通过应用程序服务器获得）HttpSession
        // SessionCreationPolicy.IF_REQUIRED, Spring Security 只会在需要时创建 HttpSession （默认配置, 如果您不指定, Spring 安全性将使用此选项）
        // SessionCreationPolicy.STATELESS, Spring Security 永远不会创建一个HttpSession, 它永远不会使用它来获取 SecurityContext
        // 前后端分离需要设置为 STATELESS, 不创建, 否则就算令牌失效, cookie 中依然存在 JSESSIONID, 接口还是能请求
        http.sessionManagement(sessionConfigurer -> sessionConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    /**
     * 配置AuthenticationManager，另一种配置方式，这个对象是spring security中用户认证的核心对象，它负责用户认证
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        // 身份验证器
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        // 用户详情服务
        daoAuthenticationProvider.setUserDetailsService(userService);
        // 密码编码器
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(daoAuthenticationProvider);
    }
}
