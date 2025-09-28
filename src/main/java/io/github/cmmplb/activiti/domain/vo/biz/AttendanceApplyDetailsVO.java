package io.github.cmmplb.activiti.domain.vo.biz;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.cmmplb.activiti.constants.BizConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author penglibo
 * @date 2024-11-11 11:38:29
 * @since jdk 1.8
 */

@Data
@ApiModel(value = "AttendanceApplyDetailsVO", description = "考勤详情返回参数")
public class AttendanceApplyDetailsVO {

    @ApiModelProperty(value = "主键", example = "1")
    private Long id;

    @ApiModelProperty(value = "申请人id", example = "1")
    private Long userId;

    @ApiModelProperty(value = "申请人姓名", example = "小明")
    private String userName;

    @ApiModelProperty(value = "类型:1-请假; 2-出差;", example = "1")
    private Byte type;

    @ApiModelProperty(value = "类型名称", example = "年假")
    private String typeName;

    @ApiModelProperty(value = "流程状态:0-进行中;1-已完成;2-已驳回;3-已撤销;", example = "1")
    private Byte status;

    @ApiModelProperty(value = "流程状态名称", example = "进行中")
    private String statusName;

    @ApiModelProperty(value = "原因", example = "想回家")
    private String remark;

    @ApiModelProperty(value = "创建时间", example = "2023-11-11 12:12:11")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "更新时间", example = "2023-11-11 12:12:11")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty(value = "考勤申请请假列表")
    private List<AttendanceApplyLeaveVO> attendanceApplyLeaveList;

    @Data
    public static class AttendanceApplyLeaveVO {

        /**
         * 类型:1-事假;2-病假;3-年假;4-丧假;5-产假;
         */
        @ApiModelProperty(value = "类型")
        private Byte type;

        /**
         * 类型
         */
        @ApiModelProperty(value = "类型")
        private String typeName;

        /**
         * 开始时间
         */
        @ApiModelProperty(value = "开始时间")
        private Date startTime;

        /**
         * 结束时间
         */
        @ApiModelProperty(value = "结束时间")
        private Date endTime;

        public String getTypeName() {
            return BizConstant.AttendanceApply.LeaveTypeEnum.getName(this.type);
        }
    }

    public String getTypeName() {
        return BizConstant.AttendanceApply.TypeEnum.getName(this.type);
    }

    public String getStatusName() {
        return BizConstant.StatusEnum.getName(this.status);
    }
}
