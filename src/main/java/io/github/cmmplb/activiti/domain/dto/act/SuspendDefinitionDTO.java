package io.github.cmmplb.activiti.domain.dto.act;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author penglibo
 * @date 2024-11-02 18:58:58
 * @since jdk 1.8
 */

@Data
@ApiModel(value = "SuspendProcessDefinitionDTO", description = "流程设计请求参数")
public class SuspendDefinitionDTO {

    @ApiModelProperty(value = "主键", example = "1")
    private String id;

    @ApiModelProperty(value = "是否挂起/激活关联的实例", example = "true")
    private Boolean activateProcessInstances;

    @ApiModelProperty(value = "定时挂起/激活时间", example = "2023-11-11 12:12:11")
    private String activationDate;
}