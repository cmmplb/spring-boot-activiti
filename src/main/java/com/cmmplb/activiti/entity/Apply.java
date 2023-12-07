package com.cmmplb.activiti.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author penglibo
 * @date 2023-11-24 09:11:02
 * @since jdk 1.8
 */

@Data
@TableName(value = "biz_apply")
public class Apply {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 申请人id
     */
    private Long userId;


    /**
     * 类型:1-请假;2-出差;3...
     */
    private Byte type;

    /**
     * 类型对应的业务id
     */
    private Long businessId;

    /**
     * 流程状态:0-进行中;1-已完成;2-已驳回;3-已撤销;
     */
    private Byte status;

    /**
     * 流程(act_re_procdef表)key
     */
    private String defKey;

    /**
     * 申请时间
     */
    @TableField(value = "apply_time")
    private Date applyTime;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}
