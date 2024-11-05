package io.github.cmmplb.activiti.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author penglibo
 * @date 2024-11-02 18:57:08
 * @since jdk 1.8
 */

@Data
@ApiModel(value = "ProcessDefinitionVO", description = "流程定义返回参数")
public class ProcessDefinitionVO {

    @ApiModelProperty(value = "主键", example = "1")
    private String id;

    @ApiModelProperty(value = "类型", example = "home")
    private String category;

    @ApiModelProperty(value = "名称", example = "请假")
    private String name;

    @ApiModelProperty(value = "关键字", example = "leave")
    private String key;

    @ApiModelProperty(value = "描述信息", example = "请假")
    private String description;

    @ApiModelProperty(value = "版本，从1开始", example = "1")
    private Integer version;

    @ApiModelProperty(value = "资源路径", example = "/usr/local/leave.bpmn20.xml")
    private String resourceName;

    @ApiModelProperty(value = "部署ID", example = "1")
    private String deploymentId;

    @ApiModelProperty(value = "图片资源文件名称, png 流程图片名称", example = "请假申请.png")
    private String diagramResourceName;

    @ApiModelProperty(value = "是否挂起/激活", example = "true")
    private Boolean suspended;

    @ApiModelProperty(value = "自定义应用版本号, 需要设置 ProjectManifest", example = "true")
    private Integer appVersion;
}
