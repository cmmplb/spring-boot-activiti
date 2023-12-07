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
@ApiModel(value = "ProcessHistoricVO", description = "流程历史信息")
public class ProcessHistoricVO {

    /**
     * id
     */
    @ApiModelProperty(value = "id", example = "1")
    private String id;

    /**
     * 流程实例id
     */
    @ApiModelProperty(value = "流程实例id", example = "1")
    private String processInstanceId;

    /**
     * 业务key
     */
    @ApiModelProperty(value = "业务key", example = "leave:1")
    private String businessKey;

    /**
     * 流程定义id
     */
    @ApiModelProperty(value = "流程定义id", example = "1")
    private String processDefinitionId;

    /**
     * 流程定义名称
     */
    @ApiModelProperty(value = "流程定义名称", example = "请假管理")
    private String processDefinitionName;

    /**
     * 流程定义key
     */
    @ApiModelProperty(value = "流程定义key", example = "leave")
    private String processDefinitionKey;

    /**
     * 流程定义版本
     */
    @ApiModelProperty(value = "流程定义版本", example = "1")
    private Integer processDefinitionVersion;

    /**
     * 部署id
     */
    @ApiModelProperty(value = "部署id", example = "1")
    private String deploymentId;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间", example = "2023-11-11 12:12:11")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间", example = "2023-11-11 12:12:11")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /**
     * 耗时
     */
    @ApiModelProperty(value = "耗时(毫秒)", example = "1")
    private Long durationInMillis;

    /**
     * 流程启动人id
     */
    @ApiModelProperty(value = "流程启动人id", example = "1")
    private String startUserId;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称", example = "1")
    private String name;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述", example = "1")
    private String description;

    /**
     * 任务名称
     */
    @ApiModelProperty(value = "任务名称", example = "领导审批")
    private String taskName;

    /**
     * 审批人姓名
     */
    @ApiModelProperty(value = "审批人姓名", example = "张三")
    private String assigneeName;

    /**
     * 审批意见
     */
    @ApiModelProperty(value = "审批意见", example = "同意")
    private String comment;
}
