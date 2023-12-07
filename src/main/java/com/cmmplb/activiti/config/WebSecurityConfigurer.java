package com.cmmplb.activiti.config;

import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @author penglibo
 * @date 2021-09-19 23:20:24
 * @since jdk 1.8
 */

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true, securedEnabled = true)
public class WebSecurityConfigurer extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.headers().frameOptions().disable(); // 关闭frame检查
        http.csrf().disable() // 关闭csrf跨域检查
                .authorizeRequests() // 打开请求认证
                // 几种规则: -/**.html   -/js/*.js   -/js/j?.js
                // .antMatchers("/js/**", "/css/**").permitAll() // 放行登陆页、首页和静态资源
                // .antMatchers("/doc.html", "/**/webjars/**", "/**/swagger-ui.html**", "/**/swagger-resources/**", /*v2改成v3了*/"/**/v3/api-docs/**").permitAll() // 放行swagger相关资源
                // .antMatchers("/resource/**").hasAuthority("resource") // 配置资源管理权限
                .anyRequest().permitAll() // 其他请求放行
        // .anyRequest().authenticated() // 其他请求都需要登录
        ;
    }
}
