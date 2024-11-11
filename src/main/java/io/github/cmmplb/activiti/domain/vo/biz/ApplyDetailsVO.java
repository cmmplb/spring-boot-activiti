package io.github.cmmplb.activiti.domain.vo.biz;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @ApiModelProperty(value = "类型:1-请假;2-出差;3...", example = "1")
    private Byte type;

    @ApiModelProperty(value = "类型", example = "请假")
    private String typeName;

    @ApiModelProperty(value = "流程状态:0-进行中;1-已完成;2-已驳回;3-已撤销;", example = "1")
    private Byte status;

    @ApiModelProperty(value = "流程状态", example = "进行中")
    private String statusName;

    @ApiModelProperty(value = "申请时间")
    private Date applyTime;

    @ApiModelProperty(value = "创建时间", example = "2023-11-11 12:12:11")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "申请人姓名")
    private String userName;

    // @ApiModelProperty(value = "请假信息")
    // private LeaveApplyDetailsVO leaveApplyDetails;

    // @ApiModelProperty(value = "出差信息")
    // private EvectionApplyDetailsVO evectionApplyDetails;

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
