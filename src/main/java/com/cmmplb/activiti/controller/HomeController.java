package com.cmmplb.activiti.controller;

import com.cmmplb.activiti.result.Result;
import com.cmmplb.activiti.result.ResultUtil;
import com.cmmplb.activiti.service.HomeService;
import com.cmmplb.activiti.vo.ApplyStatisticsVO;
import com.cmmplb.activiti.vo.ItemCountVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author penglibo
 * @date 2023-12-06 13:43:12
 * @since jdk 1.8
 */

@Api(tags = "首页")
@Slf4j
@RestController
@RequestMapping("/home")
public class HomeController {

    @Autowired
    private HomeService homeService;

    @ApiOperation("获取事项数量")
    @GetMapping(value = "/item/count")
    public Result<ItemCountVO> getItemCount() {
        return ResultUtil.success(homeService.getItemCount());
    }

    @ApiOperation("获取申请统计信息")
    @GetMapping(value = "/apply/statistics/{type}")
    @ApiImplicitParam(name = "type", paramType = "query", value = "统计类型:1-24小时;2-近30天;", required = true, dataType = "Integer", dataTypeClass = Integer.class, example = "1")
    public Result<ApplyStatisticsVO> getApplyStatistics(@PathVariable(value = "type") Integer type) {
        return ResultUtil.success(homeService.getApplyStatistics(type));
    }
}
