package io.github.cmmplb.activiti.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author penglibo
 * @date 2023-10-17 11:13:43
 * @since jdk 1.8
 */

@Data
@ApiModel(value = "BpmnJsDTO", description = "BpmnJsDTO请求参数")
public class BpmnJsDTO {

    @ApiModelProperty(value = "模型ID", hidden = true)
    private String id;

    @ApiModelProperty(value = "流程设计文件 xml")
    private String xml;

    @ApiModelProperty(value = "流程设计图片 svg")
    private String svg;
}
