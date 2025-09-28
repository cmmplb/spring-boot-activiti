package io.github.cmmplb.activiti.domain.vo.biz;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.cmmplb.activiti.constants.BizConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author penglibo
 * @date 2024-11-08 14:43:42
 * @since jdk 1.8
 */

@Data
@ApiModel(value = "ApplyDetailsVO", description = "申请详情返回参数")
public class ApplyDetailsVO {

    @ApiModelProperty(value = "主键", example = "1")
    private Long id;

    @ApiModelProperty(value = "业务id", example = "1")
    private Long businessId;

    @ApiModelProperty(value = "标题", example = "请假申请")
    private String title;

    @ApiModelProperty(value = "类型: 1-考勤管理; 2-行政管理; 3-财务管理; 4-人事管理; 5-...", example = "1")
    private Byte type;

    @ApiModelProperty(value = "对应不同业务的子类型, ps: 请假, 办公物品领用, 费用报销, 人员离职...", example = "1")
    private Byte subtype;

    @ApiModelProperty(value = "类型名称", example = "考勤管理")
    private String typeName;

    @ApiModelProperty(value = "子类型名称", example = "请假")
    private String subtypeName;

    @ApiModelProperty(value = "流程状态:0-进行中;1-已完成;2-已驳回;3-已撤销;", example = "1")
    private Byte status;

    @ApiModelProperty(value = "流程状态名称", example = "进行中")
    private String statusName;

    @ApiModelProperty(value = "创建时间", example = "2023-11-11 12:12:11")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "申请人姓名")
    private String userName;

    @ApiModelProperty(value = "考勤信息")
    private AttendanceApplyDetailsVO attendanceApplyDetails;

    // @ApiModelProperty(value = "出差信息")
    // private EvectionApplyDetailsVO evectionApplyDetails;

    public String getStatusName() {
        return BizConstant.StatusEnum.getName(this.status);
    }

    public String getTypeName() {
        return BizConstant.ApplyTypeEnum.getName(this.type);
    }

    public String getSubtypeName() {
        return BizConstant.AttendanceApply.TypeEnum.getName(this.subtype);
    }
}
