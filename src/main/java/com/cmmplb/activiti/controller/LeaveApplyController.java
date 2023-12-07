package com.cmmplb.activiti.controller;

import com.cmmplb.activiti.dto.LeaveApplyDTO;
import com.cmmplb.activiti.result.Result;
import com.cmmplb.activiti.result.ResultUtil;
import com.cmmplb.activiti.service.LeaveApplyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.ActivitiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author penglibo
 * @date 2023-11-15 09:09:58
 * @since jdk 1.8
 */

@Api(tags = "请假管理")
@Slf4j
@RestController
@RequestMapping("/leave")
public class LeaveApplyController {

    @Autowired
    private LeaveApplyService leaveApplyService;

    @ApiOperation("添加请假申请")
    @PostMapping(value = "/save")
    public Result<Boolean> save(@RequestBody LeaveApplyDTO dto) {
        boolean save;
        try {
            save = leaveApplyService.save(dto);
        } catch (ActivitiException e) {
            throw new RuntimeException(e.getMessage());
        }
        return ResultUtil.success(save);
    }
}
