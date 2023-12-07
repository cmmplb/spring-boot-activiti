package com.cmmplb.activiti.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author penglibo
 * @date 2023-10-17 11:13:43
 * @since jdk 1.8
 */

@Data
@ApiModel(value = "EvectionApplyDTO", description = "出差申请参数")
public class EvectionApplyDTO {

    /**
     * 标题
     */
    @ApiModelProperty(value = "标题")
    private String title;

    /**
     * 申请时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date applyTime;

    /**
     * 申请时间
     */
    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    /**
     * 申请时间
     */
    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    /**
     * 出差说明
     */
    @ApiModelProperty(value = "出差说明")
    private String reason;
}
