package io.github.cmmplb.activiti.domain.dto;

import com.alibaba.fastjson.annotation.JSONField;
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
@ApiModel(value = "ModelDTO", description = "流程设计模型请求参数")
public class ModelDTO {

    @ApiModelProperty(value = "模型ID", hidden = true)
    private String id;

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

    @ApiModelProperty(value = "流程设计文件xml")
    @JSONField(name = "json_xml")
    private String jsonXml;

    @ApiModelProperty(value = "流程设计图片svg")
    @JSONField(name = "svg_xml")
    private String svgXml;

}