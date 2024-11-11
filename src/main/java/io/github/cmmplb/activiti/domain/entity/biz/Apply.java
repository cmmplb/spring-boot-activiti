package io.github.cmmplb.activiti.domain.entity.biz;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author penglibo
 * @date 2024-11-08 10:48:05
 * @since jdk 1.8
 * 事项申请表
 */

@Data
@TableName(value = "`biz_apply`")
public class Apply implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 标题
     */
    @TableField(value = "`title`")
    private String title;

    /**
     * 申请人 sys_user 用户表 id
     */
    @TableField(value = "`user_id`")
    private Long userId;

    /**
     * 类型: 1-考勤管理; 2-行政管理; 3-财务管理; 4-人事管理; 5-...
     */
    @TableField(value = "`type`")
    private Byte type;

    /**
     * 对应不同业务的子类型
     */
    @TableField(value = "`subtype`")
    private Byte subtype;

    /**
     * 类型对应的业务申请表 id
     */
    @TableField(value = "`business_id`")
    private Long businessId;

    /**
     * 流程状态: 0-进行中; 1-已完成; 2-已驳回; 3-已撤销;
     */
    @TableField(value = "`status`")
    private Byte status;

    /**
     * act_re_procdef 流程定义表 key
     */
    @TableField(value = "`def_key`")
    private String defKey;

    /**
     * 创建时间
     */
    @TableField(value = "`create_time`")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "`update_time`")
    private Date updateTime;
}