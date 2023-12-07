package com.cmmplb.activiti.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cmmplb.activiti.entity.User;
import com.cmmplb.activiti.vo.UserInfoVO;

import java.util.List;
import java.util.Set;

/**
 * @author penglibo
 * @date 2023-11-17 09:44:23
 * @since jdk 1.8
 */
public interface UserService extends IService<User> {

    List<User> getListByIds(Set<String> userIds);

    List<UserInfoVO> getList();
}
