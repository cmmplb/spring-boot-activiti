package io.github.cmmplb.activiti.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.github.cmmplb.activiti.beans.PageResult;
import io.github.cmmplb.activiti.beans.QueryPageBean;
import io.github.cmmplb.activiti.domain.dto.SuspendDefinitionDTO;
import io.github.cmmplb.activiti.domain.vo.ProcessDefinitionVO;
import io.github.cmmplb.activiti.result.Result;
import io.github.cmmplb.activiti.result.ResultUtil;
import io.github.cmmplb.activiti.service.ProcessDefinitionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author penglibo
 * @date 2024-11-02 18:49:28
 * @since jdk 1.8
 */

@Api(tags = "定义管理")
@Slf4j
@RestController
@ApiSupport(order = 5)
@RequestMapping("/process/definition")
public class ProcessDefinitionController {

    @Autowired
    private ProcessDefinitionService processDefinitionService;

    @ApiOperation("分页条件查询列表")
    @PostMapping(value = "/paged")
    @ApiOperationSupport(order = 1)
    public Result<PageResult<ProcessDefinitionVO>> getByPaged(@RequestBody QueryPageBean queryPageBean) {
        return ResultUtil.success(processDefinitionService.getByPaged(queryPageBean));
    }

    @ApiOperation("查看流程文件")
    @GetMapping(value = "/show/{deploymentId}")
    @ApiOperationSupport(order = 2, ignoreParameters = {"deploymentId"})
    public Result<String> show(@PathVariable(value = "deploymentId") String deploymentId, @RequestParam(value = "resourceName") String resourceName) {
        // 注意这里的 resource 不能使用 PathVariable, 不然 resource 值为 /processes/xxx.bpmn20.xml 的时候，会报路径不存在,
        // resources/processes 路径下的流程文件设置了自动部署, 其生成的 resource 名字为 /processes/xxx.bpmn20.xml
        return ResultUtil.success(processDefinitionService.show(deploymentId, resourceName));
    }

    @ApiOperation("查看流程图")
    @GetMapping(value = "/show/chart/{id}")
    @ApiOperationSupport(order = 3, ignoreParameters = {"id", "designType"})
    public void showChart(@PathVariable(value = "id") String id) {
        processDefinitionService.showChart(id);
    }

    @ApiOperation("查看流程图-bpmn-js")
    @GetMapping(value = "/show/chart/bpmn-js/{id}")
    @ApiOperationSupport(order = 4, ignoreParameters = {"id"})
    public Result<String> showChartBpmnJs(@PathVariable(value = "id") String id) {
        return ResultUtil.success(processDefinitionService.showChartBpmnJs(id));
    }

    @ApiOperation("将流程定义转为模型")
    @PostMapping(value = "/exchange/{id}/{designType}")
    @ApiOperationSupport(order = 5, ignoreParameters = {"id", "designType"})
    public Result<Boolean> exchangeToModel(@PathVariable(value = "id") String id, @PathVariable(value = "designType") Integer designType) {
        return ResultUtil.success(processDefinitionService.exchangeToModel(id, designType));
    }

    @ApiOperation("挂起流程定义")
    @PostMapping(value = "/suspend/{id}")
    @ApiOperationSupport(order = 6, ignoreParameters = {"id"})
    public Result<Boolean> suspend(@PathVariable(value = "id") String id, @RequestBody SuspendDefinitionDTO dto) {
        dto.setId(id);
        return ResultUtil.success(processDefinitionService.suspend(dto));
    }

    @ApiOperation("激活流程定义")
    @PostMapping(value = "/activate/{id}")
    @ApiOperationSupport(order = 7, ignoreParameters = {"id"})
    public Result<Boolean> activate(@PathVariable(value = "id") String id, @RequestBody SuspendDefinitionDTO dto) {
        dto.setId(id);
        return ResultUtil.success(processDefinitionService.activate(dto));
    }
}
