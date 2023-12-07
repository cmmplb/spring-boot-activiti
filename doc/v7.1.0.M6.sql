-- 设置外键约束检查关闭   首先执行
SET FOREIGN_KEY_CHECKS = 0;
-- ================================================================================================================================
-- 流程引擎通用事件日志记录表
drop table if exists `act_evt_log`;
CREATE TABLE `act_evt_log`
(
    `LOG_NR_`       bigint(20) NOT NULL AUTO_INCREMENT COMMENT '',
    `TYPE_`         varchar(64)         DEFAULT NULL COMMENT '',
    `PROC_DEF_ID_`  varchar(64)         DEFAULT NULL COMMENT '流程定义ID',
    `PROC_INST_ID_` varchar(64)         DEFAULT NULL COMMENT '流程实例Id',
    `EXECUTION_ID_` varchar(64)         DEFAULT NULL COMMENT '执行实例ID',
    `TASK_ID_`      varchar(64)         DEFAULT NULL COMMENT '',
    `TIME_STAMP_`   timestamp  NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '',
    `USER_ID_`      varchar(255)        DEFAULT NULL COMMENT '',
    `DATA_`         longblob            DEFAULT NULL COMMENT '',
    `LOCK_OWNER_`   varchar(255)        DEFAULT NULL COMMENT '',
    `LOCK_TIME_`    timestamp  NULL     DEFAULT NULL COMMENT '',
    `IS_PROCESSED_` tinyint(4)          DEFAULT 0 COMMENT '',
    PRIMARY KEY (`LOG_NR_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3
  COLLATE = utf8mb3_bin COMMENT ='流程引擎通用事件日志记录表';
-- ================================================================================================================================
-- 通用的流程定义和流程资源表
drop table if exists `act_ge_bytearray`;
CREATE TABLE `act_ge_bytearray`
(
    `ID_`            varchar(64) NOT NULL COMMENT '主键',
    `REV_`           int(11)      DEFAULT NULL COMMENT '版本号',
    `NAME_`          varchar(255) DEFAULT NULL COMMENT '部署的文件名称,mail.bpmn、mail.png 、mail.bpmn20.xml',
    `DEPLOYMENT_ID_` varchar(64)  DEFAULT NULL COMMENT '来自于父表ACT_RE_DEPLOYMENT的主键,部署的ID',
    `BYTES_`         longblob     DEFAULT NULL COMMENT '大文本类型，存储文本字节流',
    `GENERATED_`     tinyint(4)   DEFAULT NULL COMMENT '是否是引擎生成,0为用户生成,1为Activiti生成',
    PRIMARY KEY (`ID_`),
    KEY `ACT_FK_BYTEARR_DEPL` (`DEPLOYMENT_ID_`),
    CONSTRAINT `ACT_FK_BYTEARR_DEPL` FOREIGN KEY (`DEPLOYMENT_ID_`) REFERENCES `act_re_deployment` (`ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3
  COLLATE = utf8mb3_bin COMMENT ='通用的流程定义和流程资源表';
-- ================================================================================================================================
-- 系统相关属性表
drop table if exists `act_ge_property`;
CREATE TABLE `act_ge_property`
(
    `NAME_`  varchar(64) NOT NULL COMMENT '属性名称,schema.version,schema.history,next.dbid',
    `VALUE_` varchar(300) DEFAULT NULL COMMENT '属性值,5.*,create(5.*)',
    `REV_`   int(11)      DEFAULT NULL COMMENT '乐观锁版本号',
    PRIMARY KEY (`NAME_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3
  COLLATE = utf8mb3_bin COMMENT ='系统相关属性表';
-- ================================================================================================================================
-- 历史节点表
drop table if exists `act_hi_actinst`;
CREATE TABLE `act_hi_actinst`
(
    `ID_`                varchar(64)  NOT NULL COMMENT '主键',
    `PROC_DEF_ID_`       varchar(64)  NOT NULL COMMENT '流程定义ID',
    `PROC_INST_ID_`      varchar(64)  NOT NULL COMMENT '流程实例ID',
    `EXECUTION_ID_`      varchar(64)  NOT NULL COMMENT '流程执行ID',
    `ACT_ID_`            varchar(255) NOT NULL COMMENT '活动ID,节点定义ID',
    `TASK_ID_`           varchar(64)   DEFAULT NULL COMMENT '任务ID,任务实例ID 其他节点类型实例ID在这里为空',
    `CALL_PROC_INST_ID_` varchar(64)   DEFAULT NULL COMMENT '请求流程实例ID,调用外部流程的流程实例ID',
    `ACT_NAME_`          varchar(255)  DEFAULT NULL COMMENT '活动名称,节点定义名称',
    `ACT_TYPE_`          varchar(255) NOT NULL COMMENT '活动类型,如startEvent、userTask',
    `ASSIGNEE_`          varchar(255)  DEFAULT NULL COMMENT '代理人员,节点签收人',
    `START_TIME_`        datetime     NOT NULL COMMENT '开始时间',
    `END_TIME_`          datetime      DEFAULT NULL COMMENT '结束时间',
    `DURATION_`          bigint(20)    DEFAULT NULL COMMENT '时长，耗时,毫秒值',
    `DELETE_REASON_`     varchar(4000) DEFAULT NULL COMMENT '',
    `TENANT_ID_`         varchar(255)  DEFAULT '' COMMENT '',
    PRIMARY KEY (`ID_`),
    KEY `ACT_IDX_HI_ACT_INST_START` (`START_TIME_`),
    KEY `ACT_IDX_HI_ACT_INST_END` (`END_TIME_`),
    KEY `ACT_IDX_HI_ACT_INST_PROCINST` (`PROC_INST_ID_`, `ACT_ID_`),
    KEY `ACT_IDX_HI_ACT_INST_EXEC` (`EXECUTION_ID_`, `ACT_ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3
  COLLATE = utf8mb3_bin COMMENT ='历史节点表';
-- ================================================================================================================================
-- 附件信息表
drop table if exists `act_hi_attachment`;
CREATE TABLE `act_hi_attachment`
(
    `ID_`           varchar(64) NOT NULL COMMENT '',
    `REV_`          int(11)       DEFAULT NULL COMMENT '乐观锁Version',
    `USER_ID_`      varchar(255)  DEFAULT NULL COMMENT '用户id',
    `NAME_`         varchar(255)  DEFAULT NULL COMMENT '名称',
    `DESCRIPTION_`  varchar(4000) DEFAULT NULL COMMENT '描述',
    `TYPE_`         varchar(255)  DEFAULT NULL COMMENT '类型',
    `TASK_ID_`      varchar(64)   DEFAULT NULL COMMENT '任务Id,节点实例ID',
    `PROC_INST_ID_` varchar(64)   DEFAULT NULL COMMENT '流程实例ID',
    `URL_`          varchar(4000) DEFAULT NULL COMMENT '附件地址',
    `CONTENT_ID_`   varchar(64)   DEFAULT NULL COMMENT '内容Id,字节表的ID,ACT_GE_BYTEARRAY的ID',
    `TIME_`         datetime      DEFAULT NULL COMMENT '',
    PRIMARY KEY (`ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3
  COLLATE = utf8mb3_bin COMMENT ='附件信息表';
-- ================================================================================================================================
-- 历史审批意见表
drop table if exists `act_hi_comment`;
CREATE TABLE `act_hi_comment`
(
    `ID_`           varchar(64) NOT NULL COMMENT '',
    `TYPE_`         varchar(255)  DEFAULT NULL COMMENT '意见记录类型，为comment时，为处理意见，类型：event（事件）comment（意见）',
    `TIME_`         datetime    NOT NULL COMMENT '记录时间',
    `USER_ID_`      varchar(255)  DEFAULT NULL COMMENT '用户Id，填写人',
    `TASK_ID_`      varchar(64)   DEFAULT NULL COMMENT '任务Id，节点实例ID',
    `PROC_INST_ID_` varchar(64)   DEFAULT NULL COMMENT '流程实例Id',
    `ACTION_`       varchar(255)  DEFAULT NULL COMMENT '行为类型。为addcomment时，为处理意见，值为下列内容中的一种：　　　　AddUserLink、DeleteUserLink、AddGroupLink、DeleteGroupLink、AddComment、AddAttachment、DeleteAttachment',
    `MESSAGE_`      varchar(4000) DEFAULT NULL COMMENT '处理意见，用于存放流程产生的信息，比如审批意见',
    `FULL_MSG_`     longblob      DEFAULT NULL COMMENT '全部消息',
    PRIMARY KEY (`ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3
  COLLATE = utf8mb3_bin COMMENT ='历史审批意见表';
-- ================================================================================================================================
-- 历史详细信息表
drop table if exists `act_hi_detail`;
CREATE TABLE `act_hi_detail`
(
    `ID_`           varchar(64)  NOT NULL COMMENT '主键',
    `TYPE_`         varchar(255) NOT NULL COMMENT '数据类型，FormProperty-表单;VariableUpdate-参数',
    `PROC_INST_ID_` varchar(64)   DEFAULT NULL COMMENT '流程实例ID',
    `EXECUTION_ID_` varchar(64)   DEFAULT NULL COMMENT '执行实例Id',
    `TASK_ID_`      varchar(64)   DEFAULT NULL COMMENT '任务Id',
    `ACT_INST_ID_`  varchar(64)   DEFAULT NULL COMMENT '活动实例Id,ACT_HI_ACTINST表的ID',
    `NAME_`         varchar(255) NOT NULL COMMENT '名称',
    `VAR_TYPE_`     varchar(255)  DEFAULT NULL COMMENT '变量类型,备注：VAR_TYPE_类型说明: jpa-entity、boolean、bytes、serializable(可序列化)、自定义type(根据你自身配置)、 CustomVariableType、date、double、integer、long、null、short、string',
    `REV_`          int(11)       DEFAULT NULL COMMENT '乐观锁Version',
    `TIME_`         datetime     NOT NULL COMMENT '创建时间',
    `BYTEARRAY_ID_` varchar(64)   DEFAULT NULL COMMENT '字节数组Id,ACT_GE_BYTEARRAY表的ID',
    `DOUBLE_`       double        DEFAULT NULL COMMENT 'DOUBLE_',
    `LONG_`         bigint(20)    DEFAULT NULL COMMENT 'LONG_,存储变量类型为long',
    `TEXT_`         varchar(4000) DEFAULT NULL COMMENT '值,存储变量值类型为String',
    `TEXT2_`        varchar(4000) DEFAULT NULL COMMENT '值2,此处存储的是JPA持久化对象时，才会有值。此值为对象ID',
    PRIMARY KEY (`ID_`),
    KEY `ACT_IDX_HI_DETAIL_PROC_INST` (`PROC_INST_ID_`),
    KEY `ACT_IDX_HI_DETAIL_ACT_INST` (`ACT_INST_ID_`),
    KEY `ACT_IDX_HI_DETAIL_TIME` (`TIME_`),
    KEY `ACT_IDX_HI_DETAIL_NAME` (`NAME_`),
    KEY `ACT_IDX_HI_DETAIL_TASK_ID` (`TASK_ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3
  COLLATE = utf8mb3_bin COMMENT ='历史详细信息表';
-- ================================================================================================================================
-- 历史流程人员表
drop table if exists `act_hi_identitylink`;
CREATE TABLE `act_hi_identitylink`
(
    `ID_`           varchar(64) NOT NULL,
    `GROUP_ID_`     varchar(255) DEFAULT NULL COMMENT '用户组ID',
    `TYPE_`         varchar(255) DEFAULT NULL COMMENT '用户组类型,assignee、candidate、owner、starter 、participant',
    `USER_ID_`      varchar(255) DEFAULT NULL COMMENT '用户ID',
    `TASK_ID_`      varchar(64)  DEFAULT NULL COMMENT '任务Id',
    `PROC_INST_ID_` varchar(64)  DEFAULT NULL COMMENT '流程实例Id',
    PRIMARY KEY (`ID_`),
    KEY `ACT_IDX_HI_IDENT_LNK_USER` (`USER_ID_`),
    KEY `ACT_IDX_HI_IDENT_LNK_TASK` (`TASK_ID_`),
    KEY `ACT_IDX_HI_IDENT_LNK_PROCINST` (`PROC_INST_ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3
  COLLATE = utf8mb3_bin COMMENT ='历史流程人员表';
-- ================================================================================================================================
-- 历史流程实例信息表
drop table if exists `act_hi_procinst`;
CREATE TABLE `act_hi_procinst`
(
    `ID_`                        varchar(64) NOT NULL COMMENT '',
    `PROC_INST_ID_`              varchar(64) NOT NULL COMMENT '流程实例ID',
    `BUSINESS_KEY_`              varchar(255)  DEFAULT NULL COMMENT '业务Key',
    `PROC_DEF_ID_`               varchar(64) NOT NULL COMMENT '流程定义Id',
    `START_TIME_`                datetime    NOT NULL COMMENT '开始时间',
    `END_TIME_`                  datetime      DEFAULT NULL COMMENT '结束时间',
    `DURATION_`                  bigint(20)    DEFAULT NULL COMMENT '时长',
    `START_USER_ID_`             varchar(255)  DEFAULT NULL COMMENT '发起人员Id',
    `START_ACT_ID_`              varchar(255)  DEFAULT NULL COMMENT '开始节点',
    `END_ACT_ID_`                varchar(255)  DEFAULT NULL COMMENT '结束节点',
    `SUPER_PROCESS_INSTANCE_ID_` varchar(64)   DEFAULT NULL COMMENT '超级流程实例Id',
    `DELETE_REASON_`             varchar(4000) DEFAULT NULL COMMENT '删除理由',
    `TENANT_ID_`                 varchar(255)  DEFAULT '' COMMENT '',
    `NAME_`                      varchar(255)  DEFAULT NULL COMMENT '',
    PRIMARY KEY (`ID_`),
    UNIQUE KEY `PROC_INST_ID_` (`PROC_INST_ID_`),
    KEY `ACT_IDX_HI_PRO_INST_END` (`END_TIME_`),
    KEY `ACT_IDX_HI_PRO_I_BUSKEY` (`BUSINESS_KEY_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3
  COLLATE = utf8mb3_bin COMMENT ='历史流程实例信息表';
-- ================================================================================================================================
-- 历史任务流程实例信息表
drop table if exists `act_hi_taskinst`;
CREATE TABLE `act_hi_taskinst`
(
    `ID_`             varchar(64) NOT NULL,
    `PROC_DEF_ID_`    varchar(64)   DEFAULT NULL COMMENT '流程定义Id',
    `TASK_DEF_KEY_`   varchar(255)  DEFAULT NULL COMMENT '任务定义Key',
    `PROC_INST_ID_`   varchar(64)   DEFAULT NULL COMMENT '流程实例ID',
    `EXECUTION_ID_`   varchar(64)   DEFAULT NULL COMMENT '执行ID',
    `NAME_`           varchar(255)  DEFAULT NULL COMMENT '名称',
    `PARENT_TASK_ID_` varchar(64)   DEFAULT NULL COMMENT '父任务iD',
    `DESCRIPTION_`    varchar(4000) DEFAULT NULL COMMENT '描述',
    `OWNER_`          varchar(255)  DEFAULT NULL COMMENT '实际签收人 任务的拥有者,签收人（默认为空，只有在委托时才有值）',
    `ASSIGNEE_`       varchar(255)  DEFAULT NULL COMMENT '代理人,签收人或被委托',
    `START_TIME_`     datetime    NOT NULL COMMENT '开始时间',
    `CLAIM_TIME_`     datetime      DEFAULT NULL COMMENT '提醒时间',
    `END_TIME_`       datetime      DEFAULT NULL COMMENT '结束时间',
    `DURATION_`       bigint(20)    DEFAULT NULL COMMENT '时长',
    `DELETE_REASON_`  varchar(4000) DEFAULT NULL COMMENT '删除理由,删除原因(completed,deleted)',
    `PRIORITY_`       int(11)       DEFAULT NULL COMMENT '优先级',
    `DUE_DATE_`       datetime      DEFAULT NULL COMMENT '应完成时间,过期时间，表明任务应在多长时间内完成',
    `FORM_KEY_`       varchar(255)  DEFAULT NULL COMMENT '表单key,desinger节点定义的form_key属性',
    `CATEGORY_`       varchar(255)  DEFAULT NULL COMMENT '',
    `TENANT_ID_`      varchar(255)  DEFAULT '' COMMENT '',
    PRIMARY KEY (`ID_`),
    KEY `ACT_IDX_HI_TASK_INST_PROCINST` (`PROC_INST_ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3
  COLLATE = utf8mb3_bin COMMENT ='历史任务流程实例信息表';
-- ================================================================================================================================
-- 历史变量信息表
drop table if exists `act_hi_varinst`;
CREATE TABLE `act_hi_varinst`
(
    `ID_`                varchar(64)  NOT NULL,
    `PROC_INST_ID_`      varchar(64)   DEFAULT NULL COMMENT '流程实例ID',
    `EXECUTION_ID_`      varchar(64)   DEFAULT NULL COMMENT '执行ID',
    `TASK_ID_`           varchar(64)   DEFAULT NULL COMMENT '任务Id',
    `NAME_`              varchar(255) NOT NULL COMMENT '名称',
    `VAR_TYPE_`          varchar(100)  DEFAULT NULL COMMENT '变量类型,备注：VAR_TYPE_类型说明: jpa-entity、boolean、bytes、serializable(可序列化)、自定义type(根据你自身配置)、 CustomVariableType、date、double、integer、long、null、short、string',
    `REV_`               int(11)       DEFAULT NULL COMMENT '乐观锁Version',
    `BYTEARRAY_ID_`      varchar(64)   DEFAULT NULL COMMENT '字节数组ID,ACT_GE_BYTEARRAY表的主键',
    `DOUBLE_`            double        DEFAULT NULL COMMENT '存储DoubleType类型的数据',
    `LONG_`              bigint(20)    DEFAULT NULL COMMENT '存储LongType类型的数据',
    `TEXT_`              varchar(4000) DEFAULT NULL COMMENT '存储变量值类型为String，如此处存储持久化对象时，值jpa对象的class',
    `TEXT2_`             varchar(4000) DEFAULT NULL COMMENT '此处存储的是JPA持久化对象时，才会有值。此值为对象ID',
    `CREATE_TIME_`       datetime      DEFAULT NULL COMMENT '',
    `LAST_UPDATED_TIME_` datetime      DEFAULT NULL COMMENT '',
    PRIMARY KEY (`ID_`),
    KEY `ACT_IDX_HI_PROCVAR_PROC_INST` (`PROC_INST_ID_`),
    KEY `ACT_IDX_HI_PROCVAR_NAME_TYPE` (`NAME_`, `VAR_TYPE_`),
    KEY `ACT_IDX_HI_PROCVAR_TASK_ID` (`TASK_ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3
  COLLATE = utf8mb3_bin COMMENT ='历史变量信息表';
-- ================================================================================================================================
-- 已部署的流程定义表
drop table if exists `act_procdef_info`;
CREATE TABLE `act_procdef_info`
(
    `ID_`           varchar(64) NOT NULL,
    `PROC_DEF_ID_`  varchar(64) NOT NULL COMMENT '流程定义ID',
    `REV_`          int(11)     DEFAULT NULL COMMENT '版本',
    `INFO_JSON_ID_` varchar(64) DEFAULT NULL COMMENT '',
    PRIMARY KEY (`ID_`),
    UNIQUE KEY `ACT_UNIQ_INFO_PROCDEF` (`PROC_DEF_ID_`),
    KEY `ACT_IDX_INFO_PROCDEF` (`PROC_DEF_ID_`),
    KEY `ACT_FK_INFO_JSON_BA` (`INFO_JSON_ID_`),
    CONSTRAINT `ACT_FK_INFO_JSON_BA` FOREIGN KEY (`INFO_JSON_ID_`) REFERENCES `act_ge_bytearray` (`ID_`),
    CONSTRAINT `ACT_FK_INFO_PROCDEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `act_re_procdef` (`ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3
  COLLATE = utf8mb3_bin COMMENT ='已部署的流程定义表';
-- ================================================================================================================================
-- 部署信息表
drop table if exists `act_re_deployment`;
CREATE TABLE `act_re_deployment`
(
    `ID_`                      varchar(64) NOT NULL,
    `NAME_`                    varchar(255)     DEFAULT NULL COMMENT '部署包的名称',
    `CATEGORY_`                varchar(255)     DEFAULT NULL COMMENT '类型',
    `KEY_`                     varchar(255)     DEFAULT NULL COMMENT '',
    `TENANT_ID_`               varchar(255)     DEFAULT '' COMMENT '租户,多租户通常是在软件需要为多个不同组织服务时产生的概念',
    `DEPLOY_TIME_`             timestamp   NULL DEFAULT NULL COMMENT '部署时间',
    `ENGINE_VERSION_`          varchar(255)     DEFAULT NULL COMMENT '',
    `VERSION_`                 int(11)          DEFAULT 1 COMMENT '',
    `PROJECT_RELEASE_VERSION_` varchar(255)     DEFAULT NULL COMMENT '',
    PRIMARY KEY (`ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3
  COLLATE = utf8mb3_bin COMMENT ='部署信息表';
-- ================================================================================================================================
-- 流程设计模型表
drop table if exists `act_re_model`;
CREATE TABLE `act_re_model`
(
    `ID_`                           varchar(64) NOT NULL,
    `REV_`                          int(11)          DEFAULT NULL COMMENT '乐观锁版本',
    `NAME_`                         varchar(255)     DEFAULT NULL COMMENT '模型的名称：比如：收文管理',
    `KEY_`                          varchar(255)     DEFAULT NULL COMMENT '模型的关键字，流程引擎用到。比如：FTOA_SWGL',
    `CATEGORY_`                     varchar(255)     DEFAULT NULL COMMENT '类型，用户自己对流程模型的分类。',
    `CREATE_TIME_`                  timestamp   NULL DEFAULT NULL COMMENT '创建时间',
    `LAST_UPDATE_TIME_`             timestamp   NULL DEFAULT NULL COMMENT '最后修改时间',
    `VERSION_`                      int(11)          DEFAULT NULL COMMENT '版本，从1开始。',
    `META_INFO_`                    varchar(4000)    DEFAULT NULL COMMENT '以json格式保存流程定义的信息,数据源信息，比如：{"name":"FTOA_SWGL","revision":1,"description":"丰台财政局OA，收文管理流程"}',
    `DEPLOYMENT_ID_`                varchar(64)      DEFAULT NULL COMMENT '部署ID',
    `EDITOR_SOURCE_VALUE_ID_`       varchar(64)      DEFAULT NULL COMMENT '编辑源值ID,是 ACT_GE_BYTEARRAY 表中的ID_值。',
    `EDITOR_SOURCE_EXTRA_VALUE_ID_` varchar(64)      DEFAULT NULL COMMENT '编辑源额外值ID（外键ACT_GE_BYTEARRAY ）,是 ACT_GE_BYTEARRAY 表中的ID_值。',
    `TENANT_ID_`                    varchar(255)     DEFAULT '' COMMENT '租户',
    PRIMARY KEY (`ID_`),
    KEY `ACT_FK_MODEL_SOURCE` (`EDITOR_SOURCE_VALUE_ID_`),
    KEY `ACT_FK_MODEL_SOURCE_EXTRA` (`EDITOR_SOURCE_EXTRA_VALUE_ID_`),
    KEY `ACT_FK_MODEL_DEPLOYMENT` (`DEPLOYMENT_ID_`),
    CONSTRAINT `ACT_FK_MODEL_DEPLOYMENT` FOREIGN KEY (`DEPLOYMENT_ID_`) REFERENCES `act_re_deployment` (`ID_`),
    CONSTRAINT `ACT_FK_MODEL_SOURCE` FOREIGN KEY (`EDITOR_SOURCE_VALUE_ID_`) REFERENCES `act_ge_bytearray` (`ID_`),
    CONSTRAINT `ACT_FK_MODEL_SOURCE_EXTRA` FOREIGN KEY (`EDITOR_SOURCE_EXTRA_VALUE_ID_`) REFERENCES `act_ge_bytearray` (`ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3
  COLLATE = utf8mb3_bin COMMENT ='流程设计模型表';
-- ================================================================================================================================
-- 流程定义解析表
drop table if exists `act_re_procdef`;
CREATE TABLE `act_re_procdef`
(
    `ID_`                     varchar(64)  NOT NULL,
    `REV_`                    int(11)       DEFAULT NULL COMMENT '乐观锁',
    `CATEGORY_`               varchar(255)  DEFAULT NULL COMMENT '流程命名空间（该编号就是流程文件targetNamespace的属性值）流程定义的Namespace就是类别',
    `NAME_`                   varchar(255)  DEFAULT NULL COMMENT '流程名称（该编号就是流程文件process元素的name属性值）',
    `KEY_`                    varchar(255) NOT NULL COMMENT '流程编号（该编号就是流程文件process元素的id属性值）',
    `VERSION_`                int(11)      NOT NULL COMMENT '流程版本号（由程序控制，新增即为1，修改后依次加1来完成的）',
    `DEPLOYMENT_ID_`          varchar(64)   DEFAULT NULL COMMENT '部署编号',
    `RESOURCE_NAME_`          varchar(4000) DEFAULT NULL COMMENT '资源文件名称,流程bpmn文件名称',
    `DGRM_RESOURCE_NAME_`     varchar(4000) DEFAULT NULL COMMENT '图片资源文件名称,png流程图片名称',
    `DESCRIPTION_`            varchar(4000) DEFAULT NULL COMMENT '描述信息',
    `HAS_START_FORM_KEY_`     tinyint(4)    DEFAULT NULL COMMENT '是否从key启动,start节点是否存在formKey0否  1是',
    `HAS_GRAPHICAL_NOTATION_` tinyint(4)    DEFAULT NULL COMMENT '',
    `SUSPENSION_STATE_`       int(11)       DEFAULT NULL COMMENT '是否挂起,1激活 2挂起',
    `TENANT_ID_`              varchar(255)  DEFAULT '' COMMENT '',
    `ENGINE_VERSION_`         varchar(255)  DEFAULT NULL COMMENT '',
    `APP_VERSION_`            int(11)       DEFAULT NULL COMMENT '',
    PRIMARY KEY (`ID_`),
    UNIQUE KEY `ACT_UNIQ_PROCDEF` (`KEY_`, `VERSION_`, `TENANT_ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3
  COLLATE = utf8mb3_bin COMMENT ='流程定义解析表';
-- ================================================================================================================================
-- 作业死亡信息表
drop table if exists `act_ru_deadletter_job`;
CREATE TABLE `act_ru_deadletter_job`
(
    `ID_`                  varchar(64)  NOT NULL,
    `REV_`                 int(11)           DEFAULT NULL COMMENT '版本',
    `TYPE_`                varchar(255) NOT NULL COMMENT '',
    `EXCLUSIVE_`           tinyint(1)        DEFAULT NULL COMMENT '',
    `EXECUTION_ID_`        varchar(64)       DEFAULT NULL COMMENT '执行实例ID',
    `PROCESS_INSTANCE_ID_` varchar(64)       DEFAULT NULL COMMENT '流程实例Id',
    `PROC_DEF_ID_`         varchar(64)       DEFAULT NULL COMMENT '流程定义ID',
    `EXCEPTION_STACK_ID_`  varchar(64)       DEFAULT NULL COMMENT '异常信息ID',
    `EXCEPTION_MSG_`       varchar(4000)     DEFAULT NULL COMMENT '异常信息',
    `DUEDATE_`             timestamp    NULL DEFAULT NULL COMMENT '到期时间',
    `REPEAT_`              varchar(255)      DEFAULT NULL COMMENT '重复',
    `HANDLER_TYPE_`        varchar(255)      DEFAULT NULL COMMENT '处理类型',
    `HANDLER_CFG_`         varchar(4000)     DEFAULT NULL COMMENT '标识',
    `TENANT_ID_`           varchar(255)      DEFAULT '' COMMENT '',
    PRIMARY KEY (`ID_`),
    KEY `ACT_FK_DEADLETTER_JOB_EXECUTION` (`EXECUTION_ID_`),
    KEY `ACT_FK_DEADLETTER_JOB_PROCESS_INSTANCE` (`PROCESS_INSTANCE_ID_`),
    KEY `ACT_FK_DEADLETTER_JOB_PROC_DEF` (`PROC_DEF_ID_`),
    KEY `ACT_FK_DEADLETTER_JOB_EXCEPTION` (`EXCEPTION_STACK_ID_`),
    CONSTRAINT `ACT_FK_DEADLETTER_JOB_EXCEPTION` FOREIGN KEY (`EXCEPTION_STACK_ID_`) REFERENCES `act_ge_bytearray` (`ID_`),
    CONSTRAINT `ACT_FK_DEADLETTER_JOB_EXECUTION` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `act_ru_execution` (`ID_`),
    CONSTRAINT `ACT_FK_DEADLETTER_JOB_PROCESS_INSTANCE` FOREIGN KEY (`PROCESS_INSTANCE_ID_`) REFERENCES `act_ru_execution` (`ID_`),
    CONSTRAINT `ACT_FK_DEADLETTER_JOB_PROC_DEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `act_re_procdef` (`ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3
  COLLATE = utf8mb3_bin COMMENT ='作业死亡信息表';
-- ================================================================================================================================
-- 运行时事件表
drop table if exists `act_ru_event_subscr`;
CREATE TABLE `act_ru_event_subscr`
(
    `ID_`            varchar(64)  NOT NULL,
    `REV_`           int(11)               DEFAULT NULL COMMENT '乐观锁版本号',
    `EVENT_TYPE_`    varchar(255) NOT NULL COMMENT '事件类型',
    `EVENT_NAME_`    varchar(255)          DEFAULT NULL COMMENT '事件名称',
    `EXECUTION_ID_`  varchar(64)           DEFAULT NULL COMMENT '流程执行ID',
    `PROC_INST_ID_`  varchar(64)           DEFAULT NULL COMMENT '流程实例ID',
    `ACTIVITY_ID_`   varchar(64)           DEFAULT NULL COMMENT '活动ID',
    `CONFIGURATION_` varchar(255)          DEFAULT NULL COMMENT '配置信息',
    `CREATED_`       timestamp    NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
    `PROC_DEF_ID_`   varchar(64)           DEFAULT NULL COMMENT '',
    `TENANT_ID_`     varchar(255)          DEFAULT '' COMMENT '',
    PRIMARY KEY (`ID_`),
    KEY `ACT_IDX_EVENT_SUBSCR_CONFIG_` (`CONFIGURATION_`),
    KEY `ACT_FK_EVENT_EXEC` (`EXECUTION_ID_`),
    CONSTRAINT `ACT_FK_EVENT_EXEC` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `act_ru_execution` (`ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3
  COLLATE = utf8mb3_bin COMMENT ='运行时事件表';
-- ================================================================================================================================
-- 运行时流程执行实例表
drop table if exists `act_ru_execution`;
CREATE TABLE `act_ru_execution`
(
    `ID_`                   varchar(64) NOT NULL,
    `REV_`                  int(11)          DEFAULT NULL COMMENT '乐观锁版本号',
    `PROC_INST_ID_`         varchar(64)      DEFAULT NULL COMMENT '流程实例编号,流程实例ID',
    `BUSINESS_KEY_`         varchar(255)     DEFAULT NULL COMMENT '业务编号,业务主键ID',
    `PARENT_ID_`            varchar(64)      DEFAULT NULL COMMENT '父执行流程,父节点实例ID',
    `PROC_DEF_ID_`          varchar(64)      DEFAULT NULL COMMENT '流程定义Id',
    `SUPER_EXEC_`           varchar(64)      DEFAULT NULL COMMENT '',
    `ROOT_PROC_INST_ID_`    varchar(64)      DEFAULT NULL COMMENT '',
    `ACT_ID_`               varchar(255)     DEFAULT NULL COMMENT '节点实例ID即ACT_HI_ACTINST中ID',
    `IS_ACTIVE_`            tinyint(4)       DEFAULT NULL COMMENT '激活状态,是否存活',
    `IS_CONCURRENT_`        tinyint(4)       DEFAULT NULL COMMENT '并发状态,是否为并行(true/false）',
    `IS_SCOPE_`             tinyint(4)       DEFAULT NULL COMMENT '',
    `IS_EVENT_SCOPE_`       tinyint(4)       DEFAULT NULL COMMENT '',
    `IS_MI_ROOT_`           tinyint(4)       DEFAULT NULL COMMENT '',
    `SUSPENSION_STATE_`     int(11)          DEFAULT NULL COMMENT '暂停状态_,挂起状态   1激活 2挂起',
    `CACHED_ENT_STATE_`     int(11)          DEFAULT NULL COMMENT '缓存结束状态_',
    `TENANT_ID_`            varchar(255)     DEFAULT '' COMMENT '',
    `NAME_`                 varchar(255)     DEFAULT NULL COMMENT '',
    `START_TIME_`           datetime         DEFAULT NULL COMMENT '',
    `START_USER_ID_`        varchar(255)     DEFAULT NULL COMMENT '',
    `LOCK_TIME_`            timestamp   NULL DEFAULT NULL COMMENT '',
    `IS_COUNT_ENABLED_`     tinyint(4)       DEFAULT NULL COMMENT '',
    `EVT_SUBSCR_COUNT_`     int(11)          DEFAULT NULL COMMENT '',
    `TASK_COUNT_`           int(11)          DEFAULT NULL COMMENT '',
    `JOB_COUNT_`            int(11)          DEFAULT NULL COMMENT '',
    `TIMER_JOB_COUNT_`      int(11)          DEFAULT NULL COMMENT '',
    `SUSP_JOB_COUNT_`       int(11)          DEFAULT NULL COMMENT '',
    `DEADLETTER_JOB_COUNT_` int(11)          DEFAULT NULL COMMENT '',
    `VAR_COUNT_`            int(11)          DEFAULT NULL COMMENT '',
    `ID_LINK_COUNT_`        int(11)          DEFAULT NULL COMMENT '',
    `APP_VERSION_`          int(11)          DEFAULT NULL COMMENT '',
    PRIMARY KEY (`ID_`),
    KEY `ACT_IDX_EXEC_BUSKEY` (`BUSINESS_KEY_`),
    KEY `ACT_IDC_EXEC_ROOT` (`ROOT_PROC_INST_ID_`),
    KEY `ACT_FK_EXE_PROCINST` (`PROC_INST_ID_`),
    KEY `ACT_FK_EXE_PARENT` (`PARENT_ID_`),
    KEY `ACT_FK_EXE_SUPER` (`SUPER_EXEC_`),
    KEY `ACT_FK_EXE_PROCDEF` (`PROC_DEF_ID_`),
    CONSTRAINT `ACT_FK_EXE_PARENT` FOREIGN KEY (`PARENT_ID_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE CASCADE,
    CONSTRAINT `ACT_FK_EXE_PROCDEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `act_re_procdef` (`ID_`),
    CONSTRAINT `ACT_FK_EXE_PROCINST` FOREIGN KEY (`PROC_INST_ID_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `ACT_FK_EXE_SUPER` FOREIGN KEY (`SUPER_EXEC_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3
  COLLATE = utf8mb3_bin COMMENT ='运行时流程执行实例表';
-- ================================================================================================================================
-- 身份联系表
drop table if exists `act_ru_identitylink`;
CREATE TABLE `act_ru_identitylink`
(
    `ID_`           varchar(64) NOT NULL,
    `REV_`          int(11)      DEFAULT NULL COMMENT '乐观锁版本号',
    `GROUP_ID_`     varchar(255) DEFAULT NULL COMMENT '用户组id',
    `TYPE_`         varchar(255) DEFAULT NULL COMMENT '用户组类型,主要分为以下几种：assignee、candidate、owner、starter、participant。即：受让人,候选人,所有者、起动器、参与者',
    `USER_ID_`      varchar(255) DEFAULT NULL COMMENT '用户ID',
    `TASK_ID_`      varchar(64)  DEFAULT NULL COMMENT '任务Id',
    `PROC_INST_ID_` varchar(64)  DEFAULT NULL COMMENT '流程实例ID',
    `PROC_DEF_ID_`  varchar(64)  DEFAULT NULL COMMENT '流程定义Id',
    PRIMARY KEY (`ID_`),
    KEY `ACT_IDX_IDENT_LNK_USER` (`USER_ID_`),
    KEY `ACT_IDX_IDENT_LNK_GROUP` (`GROUP_ID_`),
    KEY `ACT_IDX_ATHRZ_PROCEDEF` (`PROC_DEF_ID_`),
    KEY `ACT_FK_TSKASS_TASK` (`TASK_ID_`),
    KEY `ACT_FK_IDL_PROCINST` (`PROC_INST_ID_`),
    CONSTRAINT `ACT_FK_ATHRZ_PROCEDEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `act_re_procdef` (`ID_`),
    CONSTRAINT `ACT_FK_IDL_PROCINST` FOREIGN KEY (`PROC_INST_ID_`) REFERENCES `act_ru_execution` (`ID_`),
    CONSTRAINT `ACT_FK_TSKASS_TASK` FOREIGN KEY (`TASK_ID_`) REFERENCES `act_ru_task` (`ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3
  COLLATE = utf8mb3_bin COMMENT ='身份联系表';
-- ================================================================================================================================
-- 运行时综合表
drop table if exists `act_ru_integration`;
CREATE TABLE `act_ru_integration`
(
    `ID_`                  varchar(64) NOT NULL,
    `EXECUTION_ID_`        varchar(64)          DEFAULT NULL COMMENT '执行实例ID',
    `PROCESS_INSTANCE_ID_` varchar(64)          DEFAULT NULL COMMENT '流程实例Id',
    `PROC_DEF_ID_`         varchar(64)          DEFAULT NULL COMMENT '流程定义ID',
    `FLOW_NODE_ID_`        varchar(64)          DEFAULT NULL COMMENT '',
    `CREATED_DATE_`        timestamp   NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '',
    PRIMARY KEY (`ID_`),
    KEY `ACT_FK_INT_EXECUTION` (`EXECUTION_ID_`),
    KEY `ACT_FK_INT_PROC_INST` (`PROCESS_INSTANCE_ID_`),
    KEY `ACT_FK_INT_PROC_DEF` (`PROC_DEF_ID_`),
    CONSTRAINT `ACT_FK_INT_EXECUTION` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE CASCADE,
    CONSTRAINT `ACT_FK_INT_PROC_DEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `act_re_procdef` (`ID_`),
    CONSTRAINT `ACT_FK_INT_PROC_INST` FOREIGN KEY (`PROCESS_INSTANCE_ID_`) REFERENCES `act_ru_execution` (`ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3
  COLLATE = utf8mb3_bin COMMENT ='运行时综合表';
-- ================================================================================================================================
-- 运行中的任务表
drop table if exists `act_ru_job`;
CREATE TABLE `act_ru_job`
(
    `ID_`                  varchar(64)  NOT NULL,
    `REV_`                 int(11)           DEFAULT NULL COMMENT '乐观锁版本号',
    `TYPE_`                varchar(255) NOT NULL COMMENT '类型',
    `LOCK_EXP_TIME_`       timestamp    NULL DEFAULT NULL COMMENT '锁定释放时间',
    `LOCK_OWNER_`          varchar(255)      DEFAULT NULL COMMENT '挂起者',
    `EXCLUSIVE_`           tinyint(1)        DEFAULT NULL COMMENT '',
    `EXECUTION_ID_`        varchar(64)       DEFAULT NULL COMMENT '执行实例ID',
    `PROCESS_INSTANCE_ID_` varchar(64)       DEFAULT NULL COMMENT '流程实例ID',
    `PROC_DEF_ID_`         varchar(64)       DEFAULT NULL COMMENT '流程定义ID',
    `RETRIES_`             int(11)           DEFAULT NULL COMMENT '',
    `EXCEPTION_STACK_ID_`  varchar(64)       DEFAULT NULL COMMENT '异常信息ID',
    `EXCEPTION_MSG_`       varchar(4000)     DEFAULT NULL COMMENT '异常信息',
    `DUEDATE_`             timestamp    NULL DEFAULT NULL COMMENT '到期时间',
    `REPEAT_`              varchar(255)      DEFAULT NULL COMMENT '重复',
    `HANDLER_TYPE_`        varchar(255)      DEFAULT NULL COMMENT '处理类型',
    `HANDLER_CFG_`         varchar(4000)     DEFAULT NULL COMMENT '标识',
    `TENANT_ID_`           varchar(255)      DEFAULT '' COMMENT '',
    PRIMARY KEY (`ID_`),
    KEY `ACT_FK_JOB_EXECUTION` (`EXECUTION_ID_`),
    KEY `ACT_FK_JOB_PROCESS_INSTANCE` (`PROCESS_INSTANCE_ID_`),
    KEY `ACT_FK_JOB_PROC_DEF` (`PROC_DEF_ID_`),
    KEY `ACT_FK_JOB_EXCEPTION` (`EXCEPTION_STACK_ID_`),
    CONSTRAINT `ACT_FK_JOB_EXCEPTION` FOREIGN KEY (`EXCEPTION_STACK_ID_`) REFERENCES `act_ge_bytearray` (`ID_`),
    CONSTRAINT `ACT_FK_JOB_EXECUTION` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `act_ru_execution` (`ID_`),
    CONSTRAINT `ACT_FK_JOB_PROCESS_INSTANCE` FOREIGN KEY (`PROCESS_INSTANCE_ID_`) REFERENCES `act_ru_execution` (`ID_`),
    CONSTRAINT `ACT_FK_JOB_PROC_DEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `act_re_procdef` (`ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3
  COLLATE = utf8mb3_bin COMMENT ='运行中的任务表';
-- ================================================================================================================================
-- 运行时作业暂停表
drop table if exists `act_ru_suspended_job`;
CREATE TABLE `act_ru_suspended_job`
(
    `ID_`                  varchar(64)  NOT NULL,
    `REV_`                 int(11)           DEFAULT NULL COMMENT '版本',
    `TYPE_`                varchar(255) NOT NULL COMMENT '类型',
    `EXCLUSIVE_`           tinyint(1)        DEFAULT NULL COMMENT '',
    `EXECUTION_ID_`        varchar(64)       DEFAULT NULL COMMENT '执行实例ID',
    `PROCESS_INSTANCE_ID_` varchar(64)       DEFAULT NULL COMMENT '流程实例Id',
    `PROC_DEF_ID_`         varchar(64)       DEFAULT NULL COMMENT '流程定义ID',
    `RETRIES_`             int(11)           DEFAULT NULL COMMENT '',
    `EXCEPTION_STACK_ID_`  varchar(64)       DEFAULT NULL COMMENT '',
    `EXCEPTION_MSG_`       varchar(4000)     DEFAULT NULL COMMENT '',
    `DUEDATE_`             timestamp    NULL DEFAULT NULL COMMENT '',
    `REPEAT_`              varchar(255)      DEFAULT NULL COMMENT '',
    `HANDLER_TYPE_`        varchar(255)      DEFAULT NULL COMMENT '处理类型',
    `HANDLER_CFG_`         varchar(4000)     DEFAULT NULL COMMENT '',
    `TENANT_ID_`           varchar(255)      DEFAULT '' COMMENT '',
    PRIMARY KEY (`ID_`),
    KEY `ACT_FK_SUSPENDED_JOB_EXECUTION` (`EXECUTION_ID_`),
    KEY `ACT_FK_SUSPENDED_JOB_PROCESS_INSTANCE` (`PROCESS_INSTANCE_ID_`),
    KEY `ACT_FK_SUSPENDED_JOB_PROC_DEF` (`PROC_DEF_ID_`),
    KEY `ACT_FK_SUSPENDED_JOB_EXCEPTION` (`EXCEPTION_STACK_ID_`),
    CONSTRAINT `ACT_FK_SUSPENDED_JOB_EXCEPTION` FOREIGN KEY (`EXCEPTION_STACK_ID_`) REFERENCES `act_ge_bytearray` (`ID_`),
    CONSTRAINT `ACT_FK_SUSPENDED_JOB_EXECUTION` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `act_ru_execution` (`ID_`),
    CONSTRAINT `ACT_FK_SUSPENDED_JOB_PROCESS_INSTANCE` FOREIGN KEY (`PROCESS_INSTANCE_ID_`) REFERENCES `act_ru_execution` (`ID_`),
    CONSTRAINT `ACT_FK_SUSPENDED_JOB_PROC_DEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `act_re_procdef` (`ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3
  COLLATE = utf8mb3_bin COMMENT ='运行时作业暂停表';
-- ================================================================================================================================
-- 运行时任务数据表
drop table if exists `act_ru_task`;
CREATE TABLE `act_ru_task`
(
    `ID_`               varchar(64) NOT NULL,
    `REV_`              int(11)          DEFAULT NULL COMMENT '乐观锁版本号',
    `EXECUTION_ID_`     varchar(64)      DEFAULT NULL COMMENT '实例id（外键EXECUTION_ID_）',
    `PROC_INST_ID_`     varchar(64)      DEFAULT NULL COMMENT '流程实例ID（外键PROC_INST_ID_）',
    `PROC_DEF_ID_`      varchar(64)      DEFAULT NULL COMMENT '流程定义ID',
    `NAME_`             varchar(255)     DEFAULT NULL COMMENT '任务名称',
    `BUSINESS_KEY_`     varchar(255)     DEFAULT NULL COMMENT '',
    `PARENT_TASK_ID_`   varchar(64)      DEFAULT NULL COMMENT '父节任务ID',
    `DESCRIPTION_`      varchar(4000)    DEFAULT NULL COMMENT '任务描述',
    `TASK_DEF_KEY_`     varchar(255)     DEFAULT NULL COMMENT '任务定义key,任务定义的ID',
    `OWNER_`            varchar(255)     DEFAULT NULL COMMENT '所属人(老板),拥有者（一般情况下为空，只有在委托时才有值）',
    `ASSIGNEE_`         varchar(255)     DEFAULT NULL COMMENT '代理人员(受让人),签收人或委托人',
    `DELEGATION_`       varchar(64)      DEFAULT NULL COMMENT '代理团,委托类型，DelegationState分为两种：PENDING，RESOLVED。如无委托则为空',
    `PRIORITY_`         int(11)          DEFAULT NULL COMMENT '优先权,优先级别，默认为：50',
    `CREATE_TIME_`      timestamp   NULL DEFAULT NULL COMMENT '创建时间',
    `DUE_DATE_`         datetime         DEFAULT NULL COMMENT '执行时间',
    `CATEGORY_`         varchar(255)     DEFAULT NULL COMMENT '',
    `SUSPENSION_STATE_` int(11)          DEFAULT NULL COMMENT '暂停状态,1代表激活 2代表挂起',
    `TENANT_ID_`        varchar(255)     DEFAULT '' COMMENT '',
    `FORM_KEY_`         varchar(255)     DEFAULT NULL COMMENT '',
    `CLAIM_TIME_`       datetime         DEFAULT NULL COMMENT '',
    `APP_VERSION_`      int(11)          DEFAULT NULL COMMENT '',
    PRIMARY KEY (`ID_`),
    KEY `ACT_IDX_TASK_CREATE` (`CREATE_TIME_`),
    KEY `ACT_FK_TASK_EXE` (`EXECUTION_ID_`),
    KEY `ACT_FK_TASK_PROCINST` (`PROC_INST_ID_`),
    KEY `ACT_FK_TASK_PROCDEF` (`PROC_DEF_ID_`),
    CONSTRAINT `ACT_FK_TASK_EXE` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `act_ru_execution` (`ID_`),
    CONSTRAINT `ACT_FK_TASK_PROCDEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `act_re_procdef` (`ID_`),
    CONSTRAINT `ACT_FK_TASK_PROCINST` FOREIGN KEY (`PROC_INST_ID_`) REFERENCES `act_ru_execution` (`ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3
  COLLATE = utf8mb3_bin COMMENT ='运行时任务数据表';
-- ================================================================================================================================
-- 运行时定时器作业表
drop table if exists `act_ru_timer_job`;
CREATE TABLE `act_ru_timer_job`
(
    `ID_`                  varchar(64)  NOT NULL,
    `REV_`                 int(11)           DEFAULT NULL COMMENT '版本',
    `TYPE_`                varchar(255) NOT NULL COMMENT '类型',
    `LOCK_EXP_TIME_`       timestamp    NULL DEFAULT NULL COMMENT '',
    `LOCK_OWNER_`          varchar(255)      DEFAULT NULL COMMENT '',
    `EXCLUSIVE_`           tinyint(1)        DEFAULT NULL COMMENT '',
    `EXECUTION_ID_`        varchar(64)       DEFAULT NULL COMMENT '执行实例ID',
    `PROCESS_INSTANCE_ID_` varchar(64)       DEFAULT NULL COMMENT '流程实例Id',
    `PROC_DEF_ID_`         varchar(64)       DEFAULT NULL COMMENT '流程定义ID',
    `RETRIES_`             int(11)           DEFAULT NULL COMMENT '',
    `EXCEPTION_STACK_ID_`  varchar(64)       DEFAULT NULL COMMENT '',
    `EXCEPTION_MSG_`       varchar(4000)     DEFAULT NULL COMMENT '',
    `DUEDATE_`             timestamp    NULL DEFAULT NULL COMMENT '',
    `REPEAT_`              varchar(255)      DEFAULT NULL COMMENT '重复',
    `HANDLER_TYPE_`        varchar(255)      DEFAULT NULL COMMENT '处理类型',
    `HANDLER_CFG_`         varchar(4000)     DEFAULT NULL COMMENT '',
    `TENANT_ID_`           varchar(255)      DEFAULT '' COMMENT '',
    PRIMARY KEY (`ID_`),
    KEY `ACT_FK_TIMER_JOB_EXECUTION` (`EXECUTION_ID_`),
    KEY `ACT_FK_TIMER_JOB_PROCESS_INSTANCE` (`PROCESS_INSTANCE_ID_`),
    KEY `ACT_FK_TIMER_JOB_PROC_DEF` (`PROC_DEF_ID_`),
    KEY `ACT_FK_TIMER_JOB_EXCEPTION` (`EXCEPTION_STACK_ID_`),
    CONSTRAINT `ACT_FK_TIMER_JOB_EXCEPTION` FOREIGN KEY (`EXCEPTION_STACK_ID_`) REFERENCES `act_ge_bytearray` (`ID_`),
    CONSTRAINT `ACT_FK_TIMER_JOB_EXECUTION` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `act_ru_execution` (`ID_`),
    CONSTRAINT `ACT_FK_TIMER_JOB_PROCESS_INSTANCE` FOREIGN KEY (`PROCESS_INSTANCE_ID_`) REFERENCES `act_ru_execution` (`ID_`),
    CONSTRAINT `ACT_FK_TIMER_JOB_PROC_DEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `act_re_procdef` (`ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3
  COLLATE = utf8mb3_bin COMMENT ='运行时定时器作业表';
-- ================================================================================================================================
-- 运行时流程变量数据表
drop table if exists `act_ru_variable`;
CREATE TABLE `act_ru_variable`
(
    `ID_`           varchar(64)  NOT NULL,
    `REV_`          int(11)       DEFAULT NULL COMMENT '乐观锁版本号',
    `TYPE_`         varchar(255) NOT NULL COMMENT '编码类型',
    `NAME_`         varchar(255) NOT NULL COMMENT '变量名称',
    `EXECUTION_ID_` varchar(64)   DEFAULT NULL COMMENT '执行实例ID',
    `PROC_INST_ID_` varchar(64)   DEFAULT NULL COMMENT '流程实例Id',
    `TASK_ID_`      varchar(64)   DEFAULT NULL COMMENT '任务id',
    `BYTEARRAY_ID_` varchar(64)   DEFAULT NULL COMMENT '字节组ID,字节表的ID（ACT_GE_BYTEARRAY）',
    `DOUBLE_`       double        DEFAULT NULL COMMENT '',
    `LONG_`         bigint(20)    DEFAULT NULL COMMENT '存储变量类型为long',
    `TEXT_`         varchar(4000) DEFAULT NULL COMMENT '存储变量值类型为String,存储变量值类型为String',
    `TEXT2_`        varchar(4000) DEFAULT NULL COMMENT '此处存储的是JPA持久化对象时，才会有值。此值为对象ID',
    PRIMARY KEY (`ID_`),
    KEY `ACT_IDX_VARIABLE_TASK_ID` (`TASK_ID_`),
    KEY `ACT_FK_VAR_EXE` (`EXECUTION_ID_`),
    KEY `ACT_FK_VAR_PROCINST` (`PROC_INST_ID_`),
    KEY `ACT_FK_VAR_BYTEARRAY` (`BYTEARRAY_ID_`),
    CONSTRAINT `ACT_FK_VAR_BYTEARRAY` FOREIGN KEY (`BYTEARRAY_ID_`) REFERENCES `act_ge_bytearray` (`ID_`),
    CONSTRAINT `ACT_FK_VAR_EXE` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `act_ru_execution` (`ID_`),
    CONSTRAINT `ACT_FK_VAR_PROCINST` FOREIGN KEY (`PROC_INST_ID_`) REFERENCES `act_ru_execution` (`ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3
  COLLATE = utf8mb3_bin COMMENT ='运行时流程变量数据表';
-- ================================================================================================================================
INSERT INTO `act_ge_property` (`NAME_`, `VALUE_`, `REV_`) VALUES ('cfg.execution-related-entities-count', 'false', 1);
INSERT INTO `act_ge_property` (`NAME_`, `VALUE_`, `REV_`) VALUES ('next.dbid', '1', 1);
INSERT INTO `act_ge_property` (`NAME_`, `VALUE_`, `REV_`) VALUES ('schema.history', 'create(7.1.0-M6)', 1);
INSERT INTO `act_ge_property` (`NAME_`, `VALUE_`, `REV_`) VALUES ('schema.version', '7.1.0-M6', 1);
-- 开启外键约束检查   最后再开启外键约束
SET FOREIGN_KEY_CHECKS = 1;