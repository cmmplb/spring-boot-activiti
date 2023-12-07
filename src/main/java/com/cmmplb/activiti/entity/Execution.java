package com.cmmplb.activiti.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * @author penglibo
 * @date 2023-11-20 09:31:29
 * @since jdk 1.8
 */

@Data
@TableName(value = "act_ru_execution")
public class Execution {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "ID_", type = IdType.NONE)
    private String id;

    /**
     * 乐观锁版本号
     */
    @TableField(value = "REV_")
    private Integer rev;

    /**
     * 流程实例编号,流程实例ID
     */
    @TableField(value = "PROC_INST_ID_")
    private String procInstId;

    /**
     * 业务编号,业务主键ID
     */
    @TableField(value = "BUSINESS_KEY_")
    private String businessKey;

    /**
     * 父执行流程,父节点实例ID
     */
    @TableField(value = "PARENT_ID_")
    private String parentId;

    /**
     * 流程定义Id
     */
    @TableField(value = "PROC_DEF_ID_")
    private String procDefId;

    @TableField(value = "SUPER_EXEC_")
    private String superExec;

    @TableField(value = "ROOT_PROC_INST_ID_")
    private String rootProcInstId;

    /**
     * 节点实例ID即ACT_HI_ACTINST中ID
     */
    @TableField(value = "ACT_ID_")
    private String actId;

    /**
     * 激活状态,是否存活
     */
    @TableField(value = "IS_ACTIVE_")
    private Byte isActive;

    /**
     * 并发状态,是否为并行(true/false）
     */
    @TableField(value = "IS_CONCURRENT_")
    private Byte isConcurrent;

    @TableField(value = "IS_SCOPE_")
    private Byte isScope;

    @TableField(value = "IS_EVENT_SCOPE_")
    private Byte isEventScope;

    @TableField(value = "IS_MI_ROOT_")
    private Byte isMiRoot;

    /**
     * 暂停状态,挂起状态:1-激活;2-挂起;
     */
    @TableField(value = "SUSPENSION_STATE_")
    private Integer suspensionState;

    /**
     * 缓存结束状态
     */
    @TableField(value = "CACHED_ENT_STATE_")
    private Integer cachedEntState;

    @TableField(value = "TENANT_ID_")
    private String tenantId;

    @TableField(value = "NAME_")
    private String name;

    @TableField(value = "START_TIME_")
    private Date startTime;

    @TableField(value = "START_USER_ID_")
    private String startUserId;

    @TableField(value = "LOCK_TIME_")
    private Timestamp lockTime;

    @TableField(value = "IS_COUNT_ENABLED_")
    private Byte isCountEnabled;

    @TableField(value = "EVT_SUBSCR_COUNT_")
    private Integer evtSubscrCount;

    @TableField(value = "TASK_COUNT_")
    private Integer taskCount;

    @TableField(value = "JOB_COUNT_")
    private Integer jobCount;

    @TableField(value = "TIMER_JOB_COUNT_")
    private Integer timerJobCount;

    @TableField(value = "SUSP_JOB_COUNT_")
    private Integer suspJobCount;

    @TableField(value = "DEADLETTER_JOB_COUNT_")
    private Integer deadLetterJobCount;

    @TableField(value = "VAR_COUNT_")
    private Integer varCount;

    @TableField(value = "ID_LINK_COUNT_")
    private Integer idLinkCount;

    @TableField(value = "APP_VERSION_")
    private Integer appVersion;
}
