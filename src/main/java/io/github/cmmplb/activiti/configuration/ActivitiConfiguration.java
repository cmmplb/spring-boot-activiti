package io.github.cmmplb.activiti.configuration;

import io.github.cmmplb.activiti.configuration.properties.ActivitiProperties;
import io.github.cmmplb.activiti.utils.MD5Util;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author penglibo
 * @date 2024-11-01 15:40:52
 * @since jdk 1.8
 */

@Configuration
@EnableConfigurationProperties(ActivitiProperties.class)
public class ActivitiConfiguration {

    /**
     * 配置密码编码器，负责用户密码的加密, 放到这个配置类声明的原因是防止嵌套依赖
     * The dependencies of some of the beans in the application context form a cycle:
     * ┌─────┐
     * |  webSecurityConfigurer (field private io.github.cmmplb.activiti.service.UserService io.github.cmmplb.activiti.configuration.WebSecurityConfigurer.userService)
     * ↑     ↓
     * |  userServiceImpl (field private org.springframework.security.crypto.password.PasswordEncoder io.github.cmmplb.activiti.service.impl.UserServiceImpl.passwordEncoder)
     * └─────┘
     * 嫌麻烦的话, 可以添加允许循环依赖 spring.main.allow-circular-references = true, 然后放到 WebSecurityConfigurer 中
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // 自定义密码编码器, 使用 MD5 加密
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return MD5Util.encode(rawPassword.toString());
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return MD5Util.encode(rawPassword.toString()).equals(encodedPassword);
            }
        };
    }
}
