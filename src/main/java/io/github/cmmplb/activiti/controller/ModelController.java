package io.github.cmmplb.activiti.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.github.cmmplb.activiti.beans.PageResult;
import io.github.cmmplb.activiti.beans.QueryPageBean;
import io.github.cmmplb.activiti.domain.dto.ModelDTO;
import io.github.cmmplb.activiti.domain.vo.ModelVO;
import io.github.cmmplb.activiti.result.Result;
import io.github.cmmplb.activiti.result.ResultUtil;
import io.github.cmmplb.activiti.service.ModelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author penglibo
 * @date 2024-10-22 10:38:24
 * @since jdk 1.8
 * 模型管理
 */

@Api(tags = "模型管理")
@Slf4j
@RestController
@ApiSupport(order = 1)
@RequestMapping("/model")
public class ModelController {

    @Autowired
    private ModelService modelService;

    @ApiOperation("新增")
    @PostMapping(value = "/save")
    @ApiOperationSupport(order = 1)
    public Result<Boolean> save(@RequestBody ModelDTO dto) {
        return ResultUtil.success(modelService.save(dto));
    }

    @ApiOperation("修改")
    @PutMapping(value = "/{id}")
    @ApiOperationSupport(order = 2, ignoreParameters = {"id"})
    public Result<Boolean> updateById(@PathVariable(value = "id") String id, @RequestBody ModelDTO dto) {
        dto.setId(id);
        return ResultUtil.success(modelService.update(dto));
    }

    @ApiOperation("根据id删除")
    @DeleteMapping(value = "/{id}")
    @ApiOperationSupport(order = 3, ignoreParameters = {"id"})
    public Result<Boolean> removeById(@PathVariable(value = "id") String id) {
        return ResultUtil.success(modelService.removeById(id));
    }

    @ApiOperation("分页条件查询列表")
    @PostMapping(value = "/paged")
    @ApiOperationSupport(order = 4)
    public Result<PageResult<ModelVO>> getByPaged(@RequestBody QueryPageBean dto) {
        return ResultUtil.success(modelService.getByPaged(dto));
    }

    @ApiOperation("根据id获取详情信息")
    @GetMapping(value = "/{id}")
    @ApiOperationSupport(order = 5, ignoreParameters = {"id"})
    public Result<ModelVO> getInfoById(@PathVariable(value = "id") String id) {
        return ResultUtil.success(modelService.getInfoById(id));
    }

    @ApiOperation("导出流程模型文件")
    @GetMapping("/export/{id}")
    @ApiOperationSupport(order = 6, ignoreParameters = {"id"})
    public void export(@PathVariable(value = "id") String id) {
        modelService.export(id);
    }

    @ApiOperation("部署模型")
    @PostMapping("/deploy/{id}")
    @ApiOperationSupport(order = 7, ignoreParameters = {"id"})
    public Result<Boolean> deployment(@PathVariable(value = "id") String id) {
        return ResultUtil.success(modelService.deployment(id));
    }
}
