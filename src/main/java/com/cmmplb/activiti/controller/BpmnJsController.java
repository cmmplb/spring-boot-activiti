package com.cmmplb.activiti.controller;

import com.cmmplb.activiti.dto.ModelBpmnDTO;
import com.cmmplb.activiti.result.Result;
import com.cmmplb.activiti.result.ResultUtil;
import com.cmmplb.activiti.service.BpmnJsService;
import com.cmmplb.activiti.vo.BpmnInfoVO;
import com.cmmplb.activiti.vo.BpmnProgressVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author penglibo
 * @date 2023-12-13 16:14:55
 * @since jdk 1.8
 */
@Api(tags = "处理BpmnJs")
@Slf4j
@RestController
@RequestMapping("/bpmn/js")
public class BpmnJsController {

    @Autowired
    private BpmnJsService bpmnJsService;

    @ApiOperation("获取模型流程设计")
    @GetMapping(value = "/{id}")
    @ApiImplicitParam(name = "id", paramType = "query", value = "模型id", required = true, dataType = "Long", dataTypeClass = Long.class, example = "1")
    public Result<BpmnInfoVO> getBpmnInfo(@PathVariable(value = "id") String id) {
        return ResultUtil.success(bpmnJsService.getBpmnInfo(id));
    }

    @ApiOperation("保存流程设计")
    @PutMapping(value = "/save/{id}")
    @ApiImplicitParam(name = "id", paramType = "query", value = "模型id", required = true, dataType = "Long", dataTypeClass = Long.class, example = "1")
    public Result<Boolean> saveDesign(@PathVariable(value = "id") String id, ModelBpmnDTO dto) {
        return ResultUtil.success(bpmnJsService.saveDesign(id, dto));
    }

    @ApiOperation("查看流程图")
    @GetMapping(value = "/show/model/{id}")
    @ApiImplicitParam(name = "id", paramType = "query", value = "流程定义id", required = true, dataType = "Long", dataTypeClass = Long.class, example = "1")
    public Result<String> showFlowChart(@PathVariable(value = "id") String id) {
        return ResultUtil.success(bpmnJsService.showFlowChart(id));
    }

    @ApiOperation("查看申请进度流程图")
    @ApiImplicitParam(name = "id", paramType = "query", value = "申请id", required = true, dataType = "Long", dataTypeClass = Long.class, example = "1")
    @GetMapping(value = {"/show/{id}"})
    public Result<BpmnProgressVO> showProgressChart(@PathVariable(value = "id") Long id) {
        return ResultUtil.success(bpmnJsService.showProgressChart(id));
    }

}
