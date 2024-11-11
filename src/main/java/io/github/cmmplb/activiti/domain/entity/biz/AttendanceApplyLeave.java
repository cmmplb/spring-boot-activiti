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
 * 考勤申请请假表
 */

@Data
@TableName(value = "`biz_attendance_apply_leave`")
public class AttendanceApplyLeave implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 考勤申请表 id
     */
    @TableField(value = "`attendance_apply_id`")
    private Long attendanceApplyId;

    /**
     * 类型:1-事假; 2-病假; 3-年假; 4-丧假; 5-产假;
     */
    @TableField(value = "`type`")
    private Byte type;

    /**
     * 开始时间
     */
    @TableField(value = "`start_time`")
    private Date startTime;

    /**
     * 结束时间
     */
    @TableField(value = "`end_time`")
    private Date endTime;

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