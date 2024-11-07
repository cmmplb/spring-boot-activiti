package io.github.cmmplb.activiti.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.github.cmmplb.activiti.domain.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @author penglibo
 * @date 2024-11-06 16:32:43
 * @since jdk 1.8
 */
public interface UserService extends UserDetailsService, IService<User> {
}