package io.github.cmmplb.activiti.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author penglibo
 * @date 2023-10-17 11:13:43
 * @since jdk 1.8
 * 模型参数，对应 ACT_RE_MODEL 表里面的字段
 */

@Data
@ApiModel(value = "ModelDTO", description = "流程设计模型请求参数")
public class ModelDTO {

    @ApiModelProperty(value = "模型关键字", example = "LEAVE_APPLY")
    private String key;

    @ApiModelProperty(value = "模型名称", example = "请假申请")
    private String name;

    @ApiModelProperty(value = "模型作者", example = "cmmplb")
    private String author;

    @ApiModelProperty(value = "模型类型", example = "leave")
    private String category;

    @ApiModelProperty(value = "模型描述", example = "请假申请流程")
    private String description;
}
