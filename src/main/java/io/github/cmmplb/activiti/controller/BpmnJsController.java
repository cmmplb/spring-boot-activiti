package io.github.cmmplb.activiti.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.github.cmmplb.activiti.domain.dto.BpmnJsDTO;
import io.github.cmmplb.activiti.domain.vo.BpmnJsVO;
import io.github.cmmplb.activiti.result.Result;
import io.github.cmmplb.activiti.result.ResultUtil;
import io.github.cmmplb.activiti.service.BpmnJsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author penglibo
 * @date 2023-12-13 16:14:55
 * @since jdk 1.8
 */
@Api(tags = "bpmn-js 流程设计管理")
@Slf4j
@RestController
@RequestMapping("/bpmn-js")
public class BpmnJsController {

    @Autowired
    private BpmnJsService bpmnJsService;

    @ApiOperation("保存流程设计")
    @PutMapping(value = "/save/{id}")
    @ApiOperationSupport(order = 1, ignoreParameters = {"id"})
    public Result<Boolean> saveDesign(@PathVariable(value = "id") String id, @RequestBody BpmnJsDTO dto) {
        dto.setId(id);
        return ResultUtil.success(bpmnJsService.saveDesign(dto));
    }

    @ApiOperation("获取模型流程设计")
    @GetMapping(value = "/{id}")
    @ApiOperationSupport(order = 2, ignoreParameters = {"id"})
    public Result<BpmnJsVO> getBpmnInfoById(@PathVariable(value = "id") String id) {
        return ResultUtil.success(bpmnJsService.getBpmnInfoById(id));
    }
}
