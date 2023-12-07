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
public class ApplyVO {

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键", example = "1")
    private Long id;

    /**
     * 标题
     */
    @ApiModelProperty(value = "标题", example = "请假申请")
    private String title;

    /**
     * 流程名称
     */
    @ApiModelProperty(value = "流程名称", example = "请假申请")
    private String defName;

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
     * 类型:1-请假;2-出差;3...
     */
    @ApiModelProperty(value = "类型:1-请假;2-出差;3...", example = "1")
    private Byte type;

    /**
     * 类型
     */
    @ApiModelProperty(value = "类型", example = "请假")
    private String typeName;

    /**
     * 类型对应的业务id
     */
    @ApiModelProperty(value = "申请人id", example = "1")
    private Long businessId;

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
                return "已驳回";
            case 3:
                return "已撤销";
            default:
                return "";
        }
    }

    public String getTypeName() {
        if (null == this.type) {
            return "";
        }
        // 类型:1-请假;2-出差;3...
        switch (this.type) {
            case 1:
                return "请假";
            case 2:
                return "出差";
            default:
                return "";
        }
    }
}
