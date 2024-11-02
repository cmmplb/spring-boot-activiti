package io.github.cmmplb.activiti.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.github.cmmplb.activiti.beans.PageResult;
import io.github.cmmplb.activiti.beans.QueryPageBean;
import io.github.cmmplb.activiti.domain.vo.DeploymentVO;
import io.github.cmmplb.activiti.result.Result;
import io.github.cmmplb.activiti.result.ResultUtil;
import io.github.cmmplb.activiti.service.DeploymentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author penglibo
 * @date 2024-10-31 16:12:00
 * @since jdk 1.8
 */

@Api(tags = "部署管理")
@Slf4j
@RestController
@ApiSupport(order = 4)
@RequestMapping("/deployment")
public class DeploymentController {

    @Autowired
    private DeploymentService deploymentService;

    @ApiOperation("分页条件查询列表")
    @PostMapping(value = "/paged")
    @ApiOperationSupport(order = 1)
    public Result<PageResult<DeploymentVO>> getByPaged(@RequestBody QueryPageBean queryPageBean) {
        return ResultUtil.success(deploymentService.getByPaged(queryPageBean));
    }

    @ApiOperation("上传部署流程文件")
    @PostMapping(value = "/upload")
    @ApiOperationSupport(order = 2)
    public Result<Boolean> upload(@RequestPart(value = "files") MultipartFile[] files) {
        return ResultUtil.success(deploymentService.upload(files));
    }

    @ApiOperation("删除")
    @DeleteMapping(value = "/{id}")
    @ApiOperationSupport(order = 3, ignoreParameters = {"id"})
    public Result<Boolean> removeById(@PathVariable(value = "id") String id) {
        return ResultUtil.success(deploymentService.removeById(id));
    }
}