package com.cmmplb.activiti.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author penglibo
 * @date 2023-10-17 13:55:18
 * @since jdk 1.8
 */

@Data
public class IncompleteTaskVO {

    /**
     * id
     */
    @ApiModelProperty(value = "任务id", example = "1")
    private String id;

    /**
     * 申请id
     */
    @ApiModelProperty(value = "申请id", example = "1")
    private Long applyId;


    /**
     * 任务名称
     */
    @ApiModelProperty(value = "任务名称", example = "请假")
    private String taskName;

    /**
     * 业务key
     */
    @ApiModelProperty(value = "业务key", example = "1")
    private String businessKey;

    /**
     * 负责人id
     */
    @ApiModelProperty(value = "负责人id", example = "1")
    private String assigneeId;

    /**
     * 负责人名称
     */
    @ApiModelProperty(value = "负责人名称", example = "1")
    private String assigneeName;

    /**
     * 流程实例id
     */
    @ApiModelProperty(value = "流程实例id", example = "1")
    private String processInstanceId;

    /**
     * 执行实例id（外键EXECUTION_ID_）
     */
    @ApiModelProperty(value = "执行实例id（外键EXECUTION_ID_）", example = "1")
    private String executionId;

    /**
     * 流程定义id
     */
    @ApiModelProperty(value = "流程定义id", example = "1")
    private String processDefinitionId;

    /**
     * 类型:1-请假;2-出差;3...
     */
    @ApiModelProperty(value = "类型:1-请假;2-出差;3...", example = "1")
    private Byte type;

    /**
     * 类型:1-请假;2-出差;3...
     */
    @ApiModelProperty(value = "类型", example = "请假")
    private String typeName;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", example = "2021-01-01 12:00:00")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 流程定义名称
     */
    @ApiModelProperty(value = "流程定义名称", example = "请假")
    private String processDefinitionName;

    /**
     * 流程启动/发起人id
     */
    @ApiModelProperty(value = "流程启动/发起人id", example = "1")
    private String startUserId;

    /**
     * 流程启动/发起人名称
     */
    @ApiModelProperty(value = "流程启动/发起人名称", example = "张三")
    private String startUserName;

    /**
     * 流程启动时间
     */
    @ApiModelProperty(value = "流程启动时间", example = "2021-01-01 12:00:00")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    public String getTypeName() {
        if (null == this.type) {
            return "";
        }
        // 类型:1-请假;2-出差;3...
        switch (this.type) {
            case 1:
                return "请假";
            case 2:
                return "出差";
            default:
                return "";
        }
    }
}
