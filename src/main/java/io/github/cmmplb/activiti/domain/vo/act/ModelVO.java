package io.github.cmmplb.activiti.domain.vo.act;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.cmmplb.activiti.domain.dto.act.ModelDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * @author penglibo
 * @date 2023-10-17 11:13:43
 * @since jdk 1.8
 */

@Data
@ApiModel(value = "ModelVO", description = "流程模型返回参数")
public class ModelVO {

    @ApiModelProperty(value = "主键", example = "1")
    private String id;

    @ApiModelProperty(value = "名称", example = "请假模型")
    private String name;

    @ApiModelProperty(value = "模型作者", example = "cmmplb")
    private String author;

    @ApiModelProperty(value = "模型关键字", example = "leave")
    private String key;

    @ApiModelProperty(value = "模型类型", example = "home")
    private String category;

    @ApiModelProperty(value = "创建时间", example = "2023-11-11 12:12:11")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "最后更新时间", example = "2023-11-11 12:12:11")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastUpdateTime;

    @ApiModelProperty(value = "版本，从1开始", example = "1")
    private Integer version;

    @ApiModelProperty(value = "模型描述", example = "请假申请流程")
    private String description;

    @ApiModelProperty(value = "以json格式保存流程定义的信息,数据源信息", example = "{\"name\":\"请假申请\",\"description\":\"请假申请流程\",\"revision\":1,\"designType\":1}")
    private String metaInfo;

    @ApiModelProperty(value = "部署ID", example = "1")
    private String deploymentId;

    @ApiModelProperty(value = "设计类型:1-activiti modeler;2-bpmn-js;")
    private Integer designType;

    // 重写方法
    public String getDescription() {
        if (StringUtils.isNotEmpty(metaInfo)) {
            // 获取 metaInfo 中的 description 信息
            return JSONObject.parseObject(metaInfo).getString("description");
        }
        return description;
    }

    // 重写方法
    public Integer getDesignType() {
        if (StringUtils.isNotEmpty(metaInfo)) {
            // 获取 metaInfo 中的 description 信息
            return JSONObject.parseObject(metaInfo).getInteger(ModelDTO.DESIGN_TYPE);
        }
        return designType;
    }
}