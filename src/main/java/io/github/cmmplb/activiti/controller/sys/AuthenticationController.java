package io.github.cmmplb.activiti.controller.sys;

import com.alibaba.fastjson.JSON;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.github.cmmplb.activiti.constants.SecurityConstant;
import io.github.cmmplb.activiti.domain.dto.sys.LoginDTO;
import io.github.cmmplb.activiti.handler.exection.BusinessException;
import io.github.cmmplb.activiti.result.HttpCodeEnum;
import io.github.cmmplb.activiti.result.Result;
import io.github.cmmplb.activiti.result.ResultUtil;
import io.github.cmmplb.activiti.security.UserDetails;
import io.github.cmmplb.activiti.utils.TimeExpiredPoolCacheUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author penglibo
 * @date 2024-11-06 17:24:18
 * @since jdk 1.8
 */

@Api(tags = "认证管理")
@Slf4j
@ApiSupport(order = 6)
@RestController
@RequestMapping("/authentication")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * 自定义登录，逻辑参照
     * {@link org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter}
     */
    @ApiOperation("登录")
    @ApiOperationSupport(order = 1)
    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginDTO dto) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword());
        Authentication authenticate = null;
        try {
            authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        } catch (BadCredentialsException e) {
            throw new BusinessException(HttpCodeEnum.BAD_CREDENTIALS);
        }
        log.info("authenticate:{}", authenticate);
        if (null == authenticate) {
            throw new BusinessException(HttpCodeEnum.BAD_CREDENTIALS);
        }
        Object principal = authenticate.getPrincipal();
        UserDetails userDetails = (UserDetails) principal;
        userDetails.setPassword("N/A");
        String authorization = UUID.randomUUID().toString();
        String prefix = SecurityConstant.AUTHORIZATION_PREFIX + authorization;
        try {
            // 这里使用内存来缓存用户信息, 需要使用其他方案缓存用户信息的话再调整, 单位毫秒值, 缓存三十分钟
            TimeExpiredPoolCacheUtil.getInstance().put(prefix, JSON.toJSONString(userDetails), 30 * 60 * 1000L);
        } catch (Exception e) {
            throw new BusinessException("设置缓存信息失败");
        }
        return ResultUtil.success(authorization);
    }
}