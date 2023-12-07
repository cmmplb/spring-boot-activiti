package com.cmmplb.activiti.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author penglibo
 * @date 2023-12-06 13:44:34
 * @since jdk 1.8
 */

@Data
public class ItemCountVO {

    /**
     * 代办数量
     */
    @ApiModelProperty(value = "代办数量", example = "1")
    private Integer incompleteCount;

    /**
     * 已办数量
     */
    @ApiModelProperty(value = "已办数量", example = "1")
    private Integer completedCount;

    /**
     * 请假数量
     */
    @ApiModelProperty(value = "请假数量", example = "1")
    private Integer leaveCount;

    /**
     * 出差数量
     */
    @ApiModelProperty(value = "出差数量", example = "1")
    private Integer evectionCount;

}
