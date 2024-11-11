package io.github.cmmplb.activiti.service.sys.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.cmmplb.activiti.dao.sys.UserMapper;
import io.github.cmmplb.activiti.domain.entity.sys.User;
import io.github.cmmplb.activiti.security.UserDetails;
import io.github.cmmplb.activiti.service.sys.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * @author penglibo
 * @date 2024-10-19 23:32:24
 * @since jdk 1.8
 */

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = baseMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) {
            throw new UsernameNotFoundException("用户名不存在");
        }
        UserDetails userDetails = new UserDetails();
        userDetails.setId(user.getId());
        userDetails.setUsername(user.getUsername());
        userDetails.setPassword(passwordEncoder.encode(user.getPassword()));
        userDetails.setName(user.getName());
        userDetails.setAvatar(user.getAvatar());
        userDetails.setEnabled(user.getEnabled().equals((byte) 0));
        userDetails.setAuthorities(new ArrayList<>());
        return userDetails;
    }
}