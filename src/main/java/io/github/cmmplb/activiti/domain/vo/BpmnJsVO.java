package io.github.cmmplb.activiti.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author penglibo
 * @date 2023-12-14 10:10:39
 * @since jdk 1.8
 */

@Data
@ApiModel(value = "BpmnJsVO", description = "BpmnJsVO 返回参数")
public class BpmnJsVO {

    @ApiModelProperty(value = "设计页面中使用的字段为 modelId", example = "1")
    private String modelId;

    @ApiModelProperty(value = "名称", example = "请假模型")
    private String name;

    @ApiModelProperty(value = "流程 xml 字符串", example = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<definitions xmlns=\"http://www.omg.org/")
    private String xml;
}