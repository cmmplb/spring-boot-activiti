package com.cmmplb.activiti.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author penglibo
 * @date 2021-08-17 15:58:38
 * @since jdk 1.8
 */

@Data
public class UserInfoVO implements Serializable {

    private static final long serialVersionUID = -5029097111709615443L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键", example = "1")
    private Long id;

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名", example = "admin")
    private String username;

    /**
     * 姓名
     */
    @ApiModelProperty(value = "姓名", example = "小明")
    private String name;

    /**
     * 用户状态：1-正常；2-禁用；
     */
    @ApiModelProperty(value = "用户状态:0-正常;1-禁用", example = "0")
    private Byte status;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", example = "2021-01-01 12:00:00")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

}
