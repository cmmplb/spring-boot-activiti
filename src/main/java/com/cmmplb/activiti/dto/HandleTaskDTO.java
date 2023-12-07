package com.cmmplb.activiti.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author penglibo
 * @date 2023-10-17 11:13:43
 * @since jdk 1.8
 */

@Data
@ApiModel(value = "HandleTaskDTO", description = "办理任务")
public class HandleTaskDTO {

    /**
     * 任务id
     */
    private String id;

    /**
     * 审核状态:1-同意;2-驳回;
     */
    private Byte status;

    /**
     * 审批意见
     */
    private String comment;
}
