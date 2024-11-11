package io.github.cmmplb.activiti.controller.biz;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.github.cmmplb.activiti.beans.PageResult;
import io.github.cmmplb.activiti.domain.dto.biz.ApplyDTO;
import io.github.cmmplb.activiti.domain.dto.biz.ApplyQueryDTO;
import io.github.cmmplb.activiti.domain.vo.biz.ApplyDetailsVO;
import io.github.cmmplb.activiti.domain.vo.biz.ApplyProgressBpmnJsVO;
import io.github.cmmplb.activiti.domain.vo.biz.ApplyVO;
import io.github.cmmplb.activiti.handler.exection.BusinessException;
import io.github.cmmplb.activiti.result.Result;
import io.github.cmmplb.activiti.result.ResultUtil;
import io.github.cmmplb.activiti.service.biz.ApplyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.ActivitiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author penglibo
 * @date 2024-11-06 08:57:27
 * @since jdk 1.8
 */

@Api(tags = "事项管理")
@Slf4j
@ApiSupport(order = 8)
@RestController
@RequestMapping("/apply")
public class ApplyController {

    @Autowired
    private ApplyService applyService;

    @ApiOperation("发起申请")
    @PostMapping(value = "/save")
    @ApiOperationSupport(order = 1)
    public Result<Boolean> save(@RequestBody ApplyDTO dto) {
        boolean save;
        try {
            save = applyService.save(dto);
        } catch (ActivitiException e) {
            throw new BusinessException(e.getMessage());
        }
        return ResultUtil.success(save);
    }

    @ApiOperation("分页条件查询列表")
    @PostMapping(value = "/paged")
    @ApiOperationSupport(order = 2)
    public Result<PageResult<ApplyVO>> getByPaged(@RequestBody ApplyQueryDTO dto) {
        return ResultUtil.success(applyService.getByPaged(dto));
    }

    @ApiOperation("详情")
    @GetMapping(value = "/{id}")
    @ApiOperationSupport(order = 3, ignoreParameters = {"id"})
    public Result<ApplyDetailsVO> getApplyDetailsById(@PathVariable(value = "id") Long id) {
        return ResultUtil.success(applyService.getApplyDetailsById(id));
    }

    @ApiOperation("查看申请进度流程图")
    @GetMapping(value = {"/show/{id}"})
    @ApiOperationSupport(order = 4, ignoreParameters = {"id"})
    public void showProgressChart(@PathVariable(value = "id") Long id) {
        applyService.showProgressChart(id);
    }

    @ApiOperation("查看申请进度流程图 bpmn-js")
    @GetMapping(value = {"/show/bpmn-js/{id}"})
    @ApiOperationSupport(order = 5, ignoreParameters = {"id"})
    public Result<ApplyProgressBpmnJsVO> showProgressChartBpmnJs(@PathVariable(value = "id") Long id) {
        return ResultUtil.success(applyService.showProgressChartBpmnJs(id));
    }

    @ApiOperation("撤销申请")
    @DeleteMapping(value = "/cancel/{id}")
    @ApiOperationSupport(order = 6, ignoreParameters = {"id"})
    public Result<Boolean> revokeApply(@PathVariable(value = "id") Long id) {
        return ResultUtil.success(applyService.revokeApply(id));
    }

    @ApiOperation("根据 id 删除")
    @DeleteMapping(value = "/{id}")
    @ApiOperationSupport(order = 7, ignoreParameters = {"id"})
    public Result<Boolean> deleteById(@PathVariable(value = "id") Long id) {
        return ResultUtil.success(applyService.deleteById(id));
    }

}
