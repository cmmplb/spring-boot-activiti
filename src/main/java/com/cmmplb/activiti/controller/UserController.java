package com.cmmplb.activiti.controller;

import com.cmmplb.activiti.result.Result;
import com.cmmplb.activiti.result.ResultUtil;
import com.cmmplb.activiti.service.UserService;
import com.cmmplb.activiti.vo.UserInfoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author penglibo
 * @date 2023-11-21 14:24:00
 * @since jdk 1.8
 */

@Api(tags = "用户管理")
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @ApiOperation("获取用户列表")
    @GetMapping(value = "/list")
    public Result<List<UserInfoVO>> getList() {
        return ResultUtil.success(userService.getList());
    }
}
