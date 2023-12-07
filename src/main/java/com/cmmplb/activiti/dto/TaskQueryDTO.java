package com.cmmplb.activiti.dto;

import com.cmmplb.activiti.beans.QueryPageBean;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author penglibo
 * @date 2023-10-17 11:13:43
 * @since jdk 1.8
 */

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "TaskDTO", description = "办理信息")
public class TaskQueryDTO extends QueryPageBean {

    /**
     * 类型:1-请假;2-出差;3...
     */
    @ApiModelProperty(value = "类型:1-请假;2-出差;3...", example = "1")
    private Byte type;

    /**
     * 申请人id
     */
    @ApiModelProperty(value = "申请人id", example = "1")
    private Long userId;
}
