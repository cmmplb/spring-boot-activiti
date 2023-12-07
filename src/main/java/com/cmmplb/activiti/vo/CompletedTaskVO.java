package com.cmmplb.activiti.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author penglibo
 * @date 2023-11-17 15:32:52
 * @since jdk 1.8
 */

@Data
@ApiModel(value = "CompletedTaskVO", description = "已办任务信息")
public class CompletedTaskVO {

    /**
     * 实例id
     */
    @ApiModelProperty(value = "实例id", example = "1")
    private String instanceId;

    /**
     * 申请id
     */
    @ApiModelProperty(value = "申请id", example = "1")
    private Long applyId;

    /**
     * 业务key
     */
    @ApiModelProperty(value = "业务key", example = "leave")
    private String businessKey;

    /**
     * 名称
     */
    @ApiModelProperty(value = "流程定义名称", example = "1")
    private String processDefinitionName;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间", example = "2021-01-01 12:00:00")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**
     * 流程启动/发起人id
     */
    @ApiModelProperty(value = "流程启动/发起人id", example = "1")
    private String startUserId;

    /**
     * 流程启动/发起人姓名
     */
    @ApiModelProperty(value = "流程启动/发起人姓名", example = "张三")
    private String startUserName;

    /**
     * 当前任务节点
     */
    @ApiModelProperty(value = "当前任务节点", example = "1")
    private String taskName;

    /**
     * 流程负责人姓名
     */
    @ApiModelProperty(value = "流程负责人姓名", example = "1")
    private String assigneeName;

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
     * 审批意见
     */
    @ApiModelProperty(value = "审批意见", example = "同意")
    private String comment;

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
