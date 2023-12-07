package com.cmmplb.activiti.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmmplb.activiti.dao.UserMapper;
import com.cmmplb.activiti.entity.User;
import com.cmmplb.activiti.service.UserService;
import com.cmmplb.activiti.vo.UserInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author penglibo
 * @date 2023-11-17 09:44:28
 * @since jdk 1.8
 */

@Slf4j
@Service
@Transactional
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public List<User> getListByIds(Set<String> userIds) {
        return baseMapper.selectList(new LambdaQueryWrapper<User>().in(User::getId, userIds));
    }

    @Override
    public List<UserInfoVO> getList() {
        List<UserInfoVO> list = new ArrayList<>();
        List<User> userList = baseMapper.selectList(new LambdaQueryWrapper<User>());
        UserInfoVO vo;
        for (User user : userList) {
            vo = new UserInfoVO();
            vo.setId(user.getId());
            vo.setUsername(user.getUsername());
            vo.setName(user.getName());
            vo.setStatus(user.getStatus());
            vo.setCreateTime(user.getCreateTime());
            list.add(vo);
        }
        return list;
    }
}
