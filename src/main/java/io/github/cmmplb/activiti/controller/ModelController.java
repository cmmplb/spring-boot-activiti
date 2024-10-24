package io.github.cmmplb.activiti.controller;

import io.github.cmmplb.activiti.domain.dto.ModelDTO;
import io.github.cmmplb.activiti.result.Result;
import io.github.cmmplb.activiti.result.ResultUtil;
import io.github.cmmplb.activiti.service.impl.ModelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author penglibo
 * @date 2024-10-22 10:38:24
 * @since jdk 1.8
 * 模型管理
 */

@Api(tags = "模型管理")
@Slf4j
@RestController
@RequestMapping("/model")
public class ModelController {

    @Autowired
    private ModelService modelService;

    @ApiOperation("新增")
    @PostMapping(value = "/save")
    public Result<Boolean> save(@RequestBody ModelDTO dto) {
        return ResultUtil.success(modelService.save(dto));
    }
}
