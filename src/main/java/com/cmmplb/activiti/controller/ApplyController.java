package com.cmmplb.activiti.controller;

import com.cmmplb.activiti.beans.PageResult;
import com.cmmplb.activiti.dto.ApplyDTO;
import com.cmmplb.activiti.result.Result;
import com.cmmplb.activiti.result.ResultUtil;
import com.cmmplb.activiti.service.ApplyService;
import com.cmmplb.activiti.vo.ApplyDetailsVO;
import com.cmmplb.activiti.vo.ApplyVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author penglibo
 * @date 2023-11-15 09:09:58
 * @since jdk 1.8
 */

@Api(tags = "事项申请管理")
@Slf4j
@RestController
@RequestMapping("/apply")
public class ApplyController {

    @Autowired
    private ApplyService applyService;

    @ApiOperation("分页条件查询列表")
    @PostMapping(value = "/paged")
    public Result<PageResult<ApplyVO>> getByPaged(@RequestBody ApplyDTO dto) {
        return ResultUtil.success(applyService.getByPaged(dto));
    }

    @ApiOperation("详情")
    @GetMapping(value = "/{id}")
    @ApiImplicitParam(name = "id", paramType = "query", value = "申请id", required = true, dataType = "Long", dataTypeClass = Long.class, example = "1")
    public Result<ApplyDetailsVO> getApplyDetailsById(@PathVariable(value = "id") Long id) {
        return ResultUtil.success(applyService.getApplyDetailsById(id));
    }

    @ApiOperation("查看申请进度流程图")
    @ApiImplicitParam(name = "id", paramType = "query", value = "申请id", required = true, dataType = "Long", dataTypeClass = Long.class, example = "1")
    @GetMapping(value = {"/show/{id}"})
    public void showProgressChart(@PathVariable(value = "id") Long id) {
        applyService.showProgressChart(id);
    }

    @ApiOperation("撤销申请")
    @DeleteMapping(value = "/cancel/{id}")
    @ApiImplicitParam(name = "id", paramType = "query", value = "申请id", required = true, dataType = "Long", dataTypeClass = Long.class, example = "1")
    public Result<Boolean> cancelApply(@PathVariable(value = "id") Long id) {
        return ResultUtil.success(applyService.cancelApply(id));
    }

    @ApiOperation("根据id删除")
    @DeleteMapping(value = "/{id}")
    @ApiImplicitParam(name = "id", paramType = "query", value = "申请id", required = true, dataType = "Long", dataTypeClass = Long.class, example = "1")
    public Result<Boolean> deleteById(@PathVariable(value = "id") Long id) {
        return ResultUtil.success(applyService.deleteById(id));
    }


}
