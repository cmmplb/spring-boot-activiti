package com.cmmplb.activiti.vo;

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

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键", example = "1")
    String id;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称", example = "请假模型")
    String name;

    /**
     * 模型关键字
     */
    @ApiModelProperty(value = "模型关键字", example = "leave")
    String key;

    /**
     * 模型类型
     */
    @ApiModelProperty(value = "模型类型", example = "home")
    String category;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", example = "2023-11-11 12:12:11")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "最后更新时间", example = "2023-11-11 12:12:11")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastUpdateTime;

    /**
     * 版本，从1开始
     */
    @ApiModelProperty(value = "版本，从1开始", example = "1")
    private Integer version;

    /**
     * 以json格式保存流程定义的信息,数据源信息
     */
    @ApiModelProperty(value = "以json格式保存流程定义的信息,数据源信息", example = "{\"description\":\"请假\"}")
    private String metaInfo;

    /**
     * 部署ID
     */
    @ApiModelProperty(value = "部署ID", example = "1")
    private String deploymentId;
}
