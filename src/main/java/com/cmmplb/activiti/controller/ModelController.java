package com.cmmplb.activiti.controller;

import com.cmmplb.activiti.beans.PageResult;
import com.cmmplb.activiti.beans.QueryPageBean;
import com.cmmplb.activiti.dto.ModelDTO;
import com.cmmplb.activiti.result.Result;
import com.cmmplb.activiti.result.ResultUtil;
import com.cmmplb.activiti.service.ModelService;
import com.cmmplb.activiti.vo.ModelVO;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author penglibo
 * @date 2023-11-03 11:44:08
 * @since jdk 1.8
 */

@Api(tags = "模型管理")
@Slf4j
@RestController
@RequestMapping("/model")
public class ModelController {

    @Autowired
    private ModelService modelService;

    @ApiOperation("新增/编辑")
    @PostMapping(value = "/save")
    public Result<Boolean> save(@RequestBody ModelDTO dto) {
        return ResultUtil.success(modelService.save(dto));
    }

    @ApiOperation("保存流程设计")
    @PutMapping(value = "/save/{id}")
    @ApiImplicitParam(name = "id", paramType = "query", value = "模型id", required = true, dataType = "Long", dataTypeClass = Long.class, example = "1")
    public Result<Boolean> saveDesign(@PathVariable(value = "id") String id, ModelDTO dto) {
        return ResultUtil.success(modelService.saveDesign(id, dto));
    }

    @ApiOperation("获取模型流程设计")
    @GetMapping(value = "/json/{id}")
    @ApiImplicitParam(name = "id", paramType = "query", value = "模型id", required = true, dataType = "Long", dataTypeClass = Long.class, example = "1")
    public Result<ObjectNode> getEditorJson(@PathVariable(value = "id") String id) {
        return ResultUtil.success(modelService.getEditorJson(id));
    }

    @ApiOperation("部署模型")
    @PostMapping("/deploy/{id}")
    @ApiImplicitParam(name = "id", paramType = "query", value = "模型id", required = true, dataType = "Long", dataTypeClass = Long.class, example = "1")
    public Result<Boolean> deployment(@PathVariable(value = "id") String id) {
        return ResultUtil.success(modelService.deployment(id));
    }

    @ApiOperation("导出模型")
    @GetMapping("/export/{id}")
    @ApiImplicitParam(name = "id", paramType = "query", value = "模型id", required = true, dataType = "Long", dataTypeClass = Long.class, example = "1")
    public void export(@PathVariable(value = "id") String id) {
        modelService.export(id);
    }

    @ApiOperation("根据id删除")
    @DeleteMapping(value = "/{id}")
    @ApiImplicitParam(name = "id", paramType = "query", value = "模型id", required = true, dataType = "Long", dataTypeClass = Long.class, example = "1")
    public Result<Boolean> removeById(@PathVariable(value = "id") String id) {
        return ResultUtil.success(modelService.removeById(id));
    }

    @ApiOperation("分页条件查询列表")
    @PostMapping(value = "/paged")
    public Result<PageResult<ModelVO>> getByPaged(@RequestBody QueryPageBean dto) {
        return ResultUtil.success(modelService.getByPaged(dto));
    }

    @ApiOperation("根据id获取详情信息")
    @GetMapping(value = "/{id}")
    @ApiImplicitParam(name = "id", paramType = "query", value = "模型id", required = true, dataType = "Long", dataTypeClass = Long.class, example = "1")
    public Result<ModelVO> getInfoById(@PathVariable(value = "id") String id) {
        return ResultUtil.success(modelService.getInfoById(id));
    }
}
