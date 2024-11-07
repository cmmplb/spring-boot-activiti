package io.github.cmmplb.activiti.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.github.cmmplb.activiti.result.Result;
import io.github.cmmplb.activiti.result.ResultUtil;
import io.github.cmmplb.activiti.security.UserDetails;
import io.github.cmmplb.activiti.utils.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author penglibo
 * @date 2024-11-06 22:28:39
 * @since jdk 1.8
 */

@Api(tags = "用户管理")
@Slf4j
@ApiSupport(order = 7)
@RestController
@RequestMapping("/user")
public class UserController {

    @ApiOperation("获取用户信息")
    @GetMapping(value = "/info")
    @ApiOperationSupport(order = 1)
    public Result<UserDetails> getInfo() {
        return ResultUtil.success(SecurityUtil.getUser());
    }
}
