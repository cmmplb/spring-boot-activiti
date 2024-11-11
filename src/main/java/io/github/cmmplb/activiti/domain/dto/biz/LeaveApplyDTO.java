package io.github.cmmplb.activiti.domain.dto.biz;

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
@ApiModel(value = "LeaveApplyDTO", description = "请假申请请求参数")
public class LeaveApplyDTO {

    /**
     * 标题
     */
    @ApiModelProperty(value = "标题")
    private String title;

    /**
     * 申请时间
     */
    @ApiModelProperty(value = "申请时间")
    private Date applyTime;

    /**
     * 请假说明
     */
    @ApiModelProperty(value = "请假说明")
    private String reason;

    /**
     * 请假日期
     */
    @ApiModelProperty(value = "请假日期")
    private List<LeaveDate> leaveDateList;

    @Data
    public static class LeaveDate {

        /**
         * 类型:1-事假;2-病假;3-年假;4-丧假;5-产假;
         */
        @ApiModelProperty(value = "类型")
        private Byte type;

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
    }
}