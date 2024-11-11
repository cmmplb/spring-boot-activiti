package io.github.cmmplb.activiti.domain.dto.biz;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author penglibo
 * @date 2024-11-08 14:20:30
 * @since jdk 1.8
 */

@Data
@ApiModel(value = "ApplyDTO", description = "添加申请请求参数")
public class ApplyDTO {

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "类型: 1-考勤管理; 2-行政管理; 3-财务管理; 4-人事管理; 5-...", example = "1")
    private Byte type;

    @ApiModelProperty(value = "对应不同业务的子类型, ps: 请假, 办公物品领用, 费用报销, 人员离职...", example = "1")
    private Byte subtype;

    @ApiModelProperty(value = "流程状态:0-进行中;1-已完成;2-已驳回;3-已撤销;", example = "1")
    private Byte status;

    @ApiModelProperty(value = "申请人id", example = "1")
    private Long userId;

    @ApiModelProperty(value = "请假申请参数")
    private LeaveApplyDTO leaveApply;
}

