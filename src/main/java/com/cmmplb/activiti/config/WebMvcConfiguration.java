package com.cmmplb.activiti.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author penglibo
 * @date 2021-09-19 22:57:43
 * @since jdk 1.8
 */

@Slf4j
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    /**
     * 配置页面请求路径
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
        // 首页
        registry.addViewController("/view/home").setViewName("/home");
        // 流程管理
        // --模型管理
        registry.addViewController("/view/model/management").setViewName("/process/model");
        // --部署管理
        registry.addViewController("/view/deploy/management").setViewName("/process/deploy");

        // 功能表
        // --发起申请
        registry.addViewController("/view/initiate/apply").setViewName("/matter/initiate-apply");
        // --申请历史
        registry.addViewController("/view/apply/history").setViewName("/matter/apply-history");

        // 办理事项
        // --代办任务
        registry.addViewController("/view/incomplete/task").setViewName("/task/incomplete-task");
        // --已办任务
        registry.addViewController("/view/completed/task").setViewName("/task/completed-task");

        // 流程图编辑器
        registry.addViewController("/editor").setViewName("/modeler");
    }
}
