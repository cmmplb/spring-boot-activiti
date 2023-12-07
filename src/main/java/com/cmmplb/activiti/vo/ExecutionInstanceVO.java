package com.cmmplb.activiti.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * @author penglibo
 * @date 2023-11-17 15:32:52
 * @since jdk 1.8
 */

@Data
@ApiModel(value = "ExecutionInstanceVO", description = "执行实例信息")
public class ExecutionInstanceVO {

    /**
     * id
     */
    @ApiModelProperty(value = "id", example = "1")
    private String id;

    /**
     * 执行实例id（外键EXECUTION_ID_）
     */
    @ApiModelProperty(value = "执行实例id", example = "1")
    private String executionId;

    /**
     * 父级执行实例id（外键EXECUTION_ID_）
     */
    @ApiModelProperty(value = "父级执行实例id", example = "1")
    private String parentExecutionId;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称", example = "1")
    private String processDefinitionName;

    /**
     * 是否挂起/激活
     */
    @ApiModelProperty(value = "是否挂起/激活", example = "false")
    private String suspended;

    /**
     * 是否激活
     */
    @ApiModelProperty(value = "是否激活", example = "1")
    private String active;

    /**
     * 流程启动/发起人id
     */
    @ApiModelProperty(value = "流程启动/发起人id", example = "1")
    private String startUserId;

    /**
     * 当前任务节点
     */
    @ApiModelProperty(value = "当前任务节点", example = "1")
    private String taskName;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间", example = "2021-01-01 12:00:00")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**
     * 子实例
     */
    @ApiModelProperty(value = "子实例")
    List<ExecutionInstanceVO> children;
}
