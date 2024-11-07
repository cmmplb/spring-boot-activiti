package io.github.cmmplb.activiti.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author penglibo
 * @date 2024-11-06 16:26:30
 * @since jdk 1.8
 */

@Data
@TableName(value = "sys_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    @TableField(value = "username")
    private String username;

    /**
     * 密码
     */
    @TableField(value = "password")
    private String password;

    /**
     * 姓名
     */
    @TableField(value = "name")
    private String name;

    /**
     * 头像 base64 存储
     */
    @TableField(value = "avatar")
    private String avatar;

    /**
     * 状态:0-启用;1-禁用;
     */
    @TableField(value = "enabled")
    private Byte enabled;

    /**
     * 租户id
     */
    @TableField(value = "tenant_id")
    private Byte tenantId;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;
}
