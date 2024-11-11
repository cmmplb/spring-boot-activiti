package io.github.cmmplb.activiti.domain.dto.act;

import com.alibaba.fastjson.JSONObject;
import io.github.cmmplb.activiti.utils.SecurityUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author penglibo
 * @date 2023-10-17 11:13:43
 * @since jdk 1.8
 * 模型参数
 */

@Data
@ApiModel(value = "ModelDTO", description = "模型请求参数")
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

    @ApiModelProperty(value = "设计类型:1-activiti modeler;2-bpmn-js;")
    private Integer designType;

    @ApiModelProperty(value = "是否生成流程文件", hidden = true)
    private Boolean generateProcess;

    // 设计类型:1-activiti modeler;2-bpmn-js;
    public static final String DESIGN_TYPE = "designType";

    public static final String CATEGORY = "designType";

    public Boolean getGenerateProcess() {
        return null == generateProcess || generateProcess;
    }

    public String getAuthor() {
        return StringUtils.isEmpty(author) ? SecurityUtil.getUser().getName() : author;
    }
}