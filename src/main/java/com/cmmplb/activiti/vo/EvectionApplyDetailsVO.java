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
public class EvectionApplyDetailsVO {

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
