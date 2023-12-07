package com.cmmplb.activiti.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author penglibo
 * @date 2023-10-17 11:13:43
 * @since jdk 1.8
 */

@Data
public class LeaveApplyDetailsVO {

    /**
     * 申请人姓名
     */
    @ApiModelProperty(value = "申请人姓名")
    private String userName;

    /**
     * 请假说明
     */
    @ApiModelProperty(value = "请假说明")
    private String reason;

    /**
     * 请假日期
     */
    @ApiModelProperty(value = "请假日期")
    private List<LeaveApplyDetailsVO.LeaveDate> leaveDateList;

    @Data
    public static class LeaveDate {

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
            if (null == this.type) {
                return "";
            }
            // 类型:1-事假;2-病假;3-年假;4-丧假;5-产假;
            switch (this.type) {
                case 1:
                    return "事假";
                case 2:
                    return "病假";
                case 3:
                    return "年假";
                case 4:
                    return "丧假";
                case 5:
                    return "产假";
                default:
                    return "";
            }
        }
    }
}
