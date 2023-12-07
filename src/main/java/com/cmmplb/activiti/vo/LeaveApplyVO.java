package com.cmmplb.activiti.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author penglibo
 * @date 2023-10-17 11:13:43
 * @since jdk 1.8
 */

@Data
public class LeaveApplyVO {

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键", example = "1")
    private Long id;

    /**
     * 申请人id
     */
    @ApiModelProperty(value = "申请人id", example = "1")
    private Long userId;

    /**
     * 申请人姓名
     */
    @ApiModelProperty(value = "申请人姓名", example = "小明")
    private String userName;

    /**
     * 流程状态:0-进行中;1-已完成;2-已驳回;3-已撤销;
     */
    @ApiModelProperty(value = "流程状态:0-进行中;1-已完成;2-已驳回;3-已撤销;", example = "1")
    private Byte status;

    /**
     * 流程状态
     */
    @ApiModelProperty(value = "流程状态", example = "进行中")
    private String statusName;

    /**
     * 类型:1-事假;2-病假;3-年假;4-丧假;5-产假;
     */
    @ApiModelProperty(value = "类型:1-事假;2-病假;3-年假;4-丧假;5-产假;", example = "1")
    private Byte type;

    /**
     * 类型
     */
    @ApiModelProperty(value = "类型", example = "年假")
    private String typeName;

    /**
     * 原因
     */
    @ApiModelProperty(value = "原因", example = "想回家")
    private String reason;

    /**
     * 申请时间
     */
    @ApiModelProperty(value = "申请时间", example = "2023-11-11 12:12:11")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date applyTime;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", example = "2023-11-11 12:12:11")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间", example = "2023-11-11 12:12:11")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    public String getStatusName() {
        if (null == this.status) {
            return "";
        }
        // 流程状态:0-进行中;1-已完成;2-已驳回;3-已撤销;
        switch (this.status) {
            case 0:
                return "进行中";
            case 1:
                return "已完成";
            case 2:
                return "驳回";
            default:
                return "";
        }
    }

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
