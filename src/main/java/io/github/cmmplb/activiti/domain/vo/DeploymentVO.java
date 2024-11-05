package io.github.cmmplb.activiti.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author penglibo
 * @date 2024-10-31 16:39:24
 * @since jdk 1.8
 */

@Data
@ApiModel(value = "DeploymentVO", description = "流程部署返回参数")
public class DeploymentVO {

    @ApiModelProperty(value = "主键", example = "1")
    private String id;

    @ApiModelProperty(value = "部署名称", example = "请假模型")
    private String name;

    @ApiModelProperty(value = "类型", example = "leave-bpmn-js")
    private String category;

    @ApiModelProperty(value = "关键字", example = "leave")
    private String key;

    @ApiModelProperty(value = "部署时间", example = "2023-11-11 12:12:11")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deploymentTime;

    @ApiModelProperty(value = "版本号, 从 1 开始", example = "1")
    private Integer version;
}