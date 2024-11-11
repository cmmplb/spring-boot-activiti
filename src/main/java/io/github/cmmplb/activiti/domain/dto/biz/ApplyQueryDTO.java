package io.github.cmmplb.activiti.domain.dto.biz;

import io.github.cmmplb.activiti.beans.QueryPageBean;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author penglibo
 * @date 2024-11-08 14:20:30
 * @since jdk 1.8
 */

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "ApplyQueryDTO", description = "分页查询申请列表请求参数")
public class ApplyQueryDTO extends QueryPageBean {

    @ApiModelProperty(value = "类型: 1-考勤管理; 2-行政管理; 3-财务管理; 4-人事管理; 5-...", example = "1")
    private Byte type;

    @ApiModelProperty(value = "流程状态:0-进行中;1-已完成;2-已驳回;3-已撤销;", example = "1")
    private Byte status;

    @ApiModelProperty(value = "申请人id", example = "1")
    private Long userId;
}

