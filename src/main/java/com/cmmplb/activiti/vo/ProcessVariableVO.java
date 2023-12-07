package com.cmmplb.activiti.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author penglibo
 * @date 2023-10-17 11:13:43
 * @since jdk 1.8
 */

@Data
@ApiModel(value = "ProcessVariableVO", description = "流程变量信息")
public class ProcessVariableVO {

    /**
     * id
     */
    @ApiModelProperty(value = "id", example = "1")
    private String id;

    /**
     * 变量名称
     */
    @ApiModelProperty(value = "变量名称")
    private String variableName;

    /**
     * 变量类型名称
     */
    @ApiModelProperty(value = "变量类型名称")
    private String variableTypeName;

    /**
     * 变量值
     */
    @ApiModelProperty(value = "变量值")
    private String value;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 最后更新时间
     */
    @ApiModelProperty(value = "最后更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastUpdatedTime;
}
