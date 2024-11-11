package io.github.cmmplb.activiti.controller.act;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.github.cmmplb.activiti.domain.dto.act.ModelerDTO;
import io.github.cmmplb.activiti.domain.vo.act.ModelerVO;
import io.github.cmmplb.activiti.handler.exection.BusinessException;
import io.github.cmmplb.activiti.result.Result;
import io.github.cmmplb.activiti.result.ResultUtil;
import io.github.cmmplb.activiti.service.act.ModelerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author penglibo
 * @date 2024-10-28 17:22:18
 * @since jdk 1.8
 */

@Api(tags = "activiti-modeler 流程设计管理")
@Slf4j
@RestController
@ApiSupport(order = 2)
@RequestMapping("/modeler")
public class ModelerController {

    @Autowired
    private ModelerService modelService;

    @ApiOperation("获取 stencilset 配置")
    @GetMapping(value = "/stencilset")
    @ApiOperationSupport(order = 1)
    public String getStencilSet() {
        // 这里的接口路径对应 /web/public/activiti-explorer/editor-app/configuration/url-config.js 这个配置文件
        // 后面时间戳我感觉是为了防止前端 get 请求缓存的问题, 加上时间戳取最新的
        // return ACTIVITI.CONFIG.contextRoot + '/editor/stencilset?version=' + Date.now();
        ClassLoader classLoader = this.getClass().getClassLoader();
        try {
            return IOUtils.toString(Objects.requireNonNull(classLoader.getResourceAsStream("static/stencilset-zh.json")), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new BusinessException("获取 stencilset 配置文件失败");
        }
    }

    // 这里的接口路径对应 /web/public/activiti-explorer/editor-app/configuration/url-config.js -> putModel 这个配置文件接口
    @ApiOperation("保存流程设计")
    @PutMapping(value = "/save/{id}")
    @ApiOperationSupport(order = 2, ignoreParameters = {"id"})
    public Result<Boolean> saveDesign(@PathVariable(value = "id") String id, ModelerDTO dto) {
        dto.setId(id);
        return ResultUtil.success(modelService.saveDesign(dto));
    }

    // 这里的接口路径对应 /web/public/activiti-explorer/editor-app/configuration/url-config.js -> getModel 这个配置文件接口
    @ApiOperation("根据流程 id 获取模型流程设计 json 信息")
    @GetMapping(value = "/json/{id}")
    @ApiOperationSupport(order = 3, ignoreParameters = {"id"})
    public Result<ModelerVO> getModelerJsonById(@PathVariable(value = "id") String id) {
        return ResultUtil.success(modelService.getModelerJsonById(id));
    }
}