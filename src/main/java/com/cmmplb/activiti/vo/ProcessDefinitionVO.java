package com.cmmplb.activiti.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author penglibo
 * @date 2023-10-17 11:13:43
 * @since jdk 1.8
 */

@Data
@ApiModel(value = "ProcessDefinitionVO", description = "流程定义信息")
public class ProcessDefinitionVO {

    /**
     * 流程定义id
     */
    @ApiModelProperty(value = "流程定义id", example = "1")
    private String id;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称", example = "请假")
    private String name;

    /**
     * 类型
     */
    @ApiModelProperty(value = "类型", example = "home")
    private String category;

    /**
     * 关键字
     */
    @ApiModelProperty(value = "关键字", example = "leave")
    private String key;

    /**
     * 描述信息
     */
    @ApiModelProperty(value = "描述信息", example = "请假")
    private String description;

    /**
     * 版本，从1开始
     */
    @ApiModelProperty(value = "版本，从1开始", example = "1")
    private Integer version;

    /**
     * 资源路径
     */
    @ApiModelProperty(value = "资源路径", example = "/usr/local/leave.bpmn20.xml")
    private String resourceName;

    /**
     * 部署ID
     */
    @ApiModelProperty(value = "部署ID", example = "1")
    private String deploymentId;

    /**
     * 是否挂起/激活
     */
    @ApiModelProperty(value = "是否挂起/激活", example = "true")
    private Boolean isSuspended;
}
