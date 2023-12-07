package com.cmmplb.activiti.controller;

import com.cmmplb.activiti.beans.PageResult;
import com.cmmplb.activiti.beans.QueryPageBean;
import com.cmmplb.activiti.dto.SuspendActivateProcessDefinitionDTO;
import com.cmmplb.activiti.result.Result;
import com.cmmplb.activiti.result.ResultUtil;
import com.cmmplb.activiti.service.DeployService;
import com.cmmplb.activiti.vo.ProcessDefinitionVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author penglibo
 * @date 2023-11-09 11:42:38
 * @since jdk 1.8
 */

@Api(tags = "部署管理")
@Slf4j
@RestController
@RequestMapping("/deploy")
public class DeployController {

    @Autowired
    private DeployService deployService;

    @ApiOperation("分页条件查询列表")
    @PostMapping(value = "/paged")
    public Result<PageResult<ProcessDefinitionVO>> getByPaged(@RequestBody QueryPageBean queryPageBean) {
        return ResultUtil.success(deployService.getByPaged(queryPageBean));
    }

    @ApiOperation("上传部署流程文件")
    @PostMapping(value = "/upload")
    public Result<Boolean> upload(@RequestPart(value = "files") MultipartFile[] files) {
        return ResultUtil.success(deployService.upload(files));
    }

    @ApiOperation("删除")
    @DeleteMapping(value = "/{id}")
    @ApiImplicitParam(name = "id", paramType = "query", value = "部署id", required = true, dataType = "Long", dataTypeClass = Long.class, example = "1")
    public Result<Boolean> removeById(@PathVariable(value = "id") String id) {
        return ResultUtil.success(deployService.removeById(id));
    }

    @ApiOperation("查看工作流定义")
    @GetMapping(value = "/show/process/{id}/{resource}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", paramType = "query", value = "部署id", required = true, dataType = "Long", dataTypeClass = Long.class, example = "1"),
            @ApiImplicitParam(name = "resource", paramType = "query", value = "资源路径", required = true, dataType = "String", dataTypeClass = String.class, example = "1"),
    })
    public void showProcessDefinition(@PathVariable("id") String id, @PathVariable(value = "resource") String resource) {
        deployService.showProcessDefinition(id, resource);
    }

    @ApiOperation("查看流程图")
    @GetMapping(value = "/show/model/{id}")
    @ApiImplicitParam(name = "id", paramType = "query", value = "流程定义id", required = true, dataType = "Long", dataTypeClass = Long.class, example = "1")
    public void showFlowChart(@PathVariable(value = "id") String id) {
        deployService.showProcessChart(id);
    }

    @ApiOperation("将流程定义转为模型")
    @PostMapping(value = "/exchange/{id}")
    @ApiImplicitParam(name = "id", paramType = "query", value = "流程定义id", required = true, dataType = "Long", dataTypeClass = Long.class, example = "1")
    public Result<Boolean> exchangeProcessToModel(@PathVariable(value = "id") String id) {
        return ResultUtil.success(deployService.exchangeProcessToModel(id));
    }

    @ApiOperation("挂起流程定义")
    @PostMapping(value = "/suspend/{id}")
    @ApiImplicitParam(name = "id", paramType = "query", value = "流程定义id", required = true, dataType = "Long", dataTypeClass = Long.class, example = "1")
    public Result<Boolean> suspendProcessDefinition(@PathVariable(value = "id") String id, @RequestBody SuspendActivateProcessDefinitionDTO dto) {
        return ResultUtil.success(deployService.suspendProcessDefinition(id, dto));
    }

    @ApiOperation("激活流程定义")
    @PostMapping(value = "/activate/{id}")
    @ApiImplicitParam(name = "id", paramType = "query", value = "流程定义id", required = true, dataType = "Long", dataTypeClass = Long.class, example = "1")
    public Result<Boolean> activateProcessDefinition(@PathVariable(value = "id") String id, @RequestBody SuspendActivateProcessDefinitionDTO dto) {
        return ResultUtil.success(deployService.activateProcessDefinition(id, dto));
    }
}
