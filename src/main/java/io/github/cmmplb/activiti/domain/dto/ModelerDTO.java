package io.github.cmmplb.activiti.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author penglibo
 * @date 2023-10-17 11:13:43
 * @since jdk 1.8
 * 模型参数
 */

@Data
@ApiModel(value = "ModelerDTO", description = "流程设计请求参数")
public class ModelerDTO {

    @ApiModelProperty(value = "模型ID", hidden = true)
    private String id;

    @ApiModelProperty(value = "模型名称", example = "请假申请")
    private String name;

    @ApiModelProperty(value = "模型描述", example = "请假申请流程")
    private String description;

    @ApiModelProperty(value = "流程设计文件 xml")
    private String jsonXml;

    @ApiModelProperty(value = "流程设计图片 svg")
    private String svgXml;

}
