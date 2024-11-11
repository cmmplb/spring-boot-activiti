package io.github.cmmplb.activiti.domain.entity.biz;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * @author penglibo
 * @date 2024-11-08 10:48:05
 * @since jdk 1.8
 * 行政管理申请表
 */

@Data
@TableName(value = "`biz_administration_apply`")
public class AdministrationApply implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 申请人 sys_user 用户表 id
     */
    @TableField(value = "`user_id`")
    private Long userId;

    /**
     * 类型:1-入库; 2-出库
     */
    @TableField(value = "`type`")
    private Byte type;

    /**
     * 流程状态: 0-进行中; 1-已完成; 2-已驳回; 3-已撤销;
     */
    @TableField(value = "`status`")
    private Byte status;

    /**
     * 备注
     */
    @TableField(value = "`remark`")
    private String remark;

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