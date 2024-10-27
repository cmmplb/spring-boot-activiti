package io.github.cmmplb.activiti.domain.vo;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author penglibo
 * @date 2023-10-17 11:13:43
 * @since jdk 1.8
 */

@Data
@ApiModel(value = "ModelVO", description = "流程设计模型")
public class ModelVO {

    @ApiModelProperty(value = "主键", example = "1")
    String id;

    @ApiModelProperty(value = "名称", example = "请假模型")
    String name;

    @ApiModelProperty(value = "模型关键字", example = "leave")
    String key;

    @ApiModelProperty(value = "模型类型", example = "home")
    String category;

    @ApiModelProperty(value = "创建时间", example = "2023-11-11 12:12:11")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "最后更新时间", example = "2023-11-11 12:12:11")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastUpdateTime;

    @ApiModelProperty(value = "版本，从1开始", example = "1")
    private Integer version;

    @ApiModelProperty(value = "以json格式保存流程定义的信息,数据源信息", example = "{\"name\":\"请假申请\",\"description\":\"请假申请流程\",\"revision\":1}")
    private String metaInfo;

    @ApiModelProperty(value = "部署ID", example = "1")
    private String deploymentId;

    @ApiModelProperty(value = "流程设计 json 信息")
    private JSONObject model;
}
