package com.cmmplb.activiti.controller;

import com.cmmplb.activiti.beans.PageResult;
import com.cmmplb.activiti.dto.HandleTaskDTO;
import com.cmmplb.activiti.dto.TaskQueryDTO;
import com.cmmplb.activiti.result.Result;
import com.cmmplb.activiti.result.ResultUtil;
import com.cmmplb.activiti.service.TaskService;
import com.cmmplb.activiti.vo.CompletedTaskVO;
import com.cmmplb.activiti.vo.IncompleteTaskVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author penglibo
 * @date 2023-11-16 10:41:33
 * @since jdk 1.8
 */

@Api(tags = "任务管理")
@Slf4j
@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @ApiOperation("分页条件查询代办列表")
    @PostMapping(value = "/incomplete/paged")
    public Result<PageResult<IncompleteTaskVO>> getIncompleteByPaged(@RequestBody TaskQueryDTO dto) {
        return ResultUtil.success(taskService.getByPaged(dto));
    }

    @ApiOperation("分页条件查询已办列表")
    @PostMapping(value = "/completed/paged")
    public Result<PageResult<CompletedTaskVO>> getCompletedByPaged(@RequestBody TaskQueryDTO dto) {
        return ResultUtil.success(taskService.getCompletedByPaged(dto));
    }

    @ApiOperation("办理任务")
    @PostMapping(value = "/handle/{id}")
    @ApiImplicitParam(name = "id", paramType = "query", value = "任务id", required = true, dataType = "Long", dataTypeClass = Long.class, example = "1")
    public Result<Boolean> handleTask(@PathVariable(value = "id") String id, @RequestBody HandleTaskDTO dto) {
        dto.setId(id);
        return ResultUtil.success(taskService.handleTask(dto));
    }

    @ApiOperation("委托他人办理")
    @PostMapping(value = "/entrust/{id}/{userId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", paramType = "query", value = "任务id", required = true, dataType = "Long", dataTypeClass = Long.class, example = "1"),
            @ApiImplicitParam(name = "userId", paramType = "query", value = "用户id", required = true, dataType = "Long", dataTypeClass = Long.class, example = "1")
    })
    public Result<Boolean> entrustTask(@PathVariable(value = "id") String id, @PathVariable(value = "userId") String userId) {
        return ResultUtil.success(taskService.entrustTask(id, userId));
    }
}
