package io.github.cmmplb.activiti.domain.dto.biz;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author penglibo
 * @date 2024-11-08 14:32:11
 * @since jdk 1.8
 */

@Data
@ApiModel(value = "AttendanceApplyDTO", description = "考勤申请请求参数")
public class AttendanceApplyDTO {

    @ApiModelProperty(value = "申请时间")
    private Date applyTime;

    @ApiModelProperty(value = "请假说明")
    private String remark;

    @ApiModelProperty(value = "类型:1-请假; 2-出差;")
    private Byte type;

    @ApiModelProperty(value = "请假日期参数")
    private List<LeaveDate> leaveDateList;

    @Data
    public static class LeaveDate {

        @ApiModelProperty(value = "类型:1-事假;2-病假;3-年假;4-丧假;5-产假")
        private Byte type;

        @ApiModelProperty(value = "开始时间", example = "2023-11-11 12:12:11")
        @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
        private Date startTime;

        @ApiModelProperty(value = "结束时间", example = "2023-11-11 12:12:11")
        @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
        private Date endTime;
    }
}