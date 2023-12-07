# 1 Activiti数据库表结构

## 1.1 数据库表名说明

Activiti工作流总共包含23张数据表，所有的表名默认以“ACT_”开头。

并且表名的第二部分用两个字母表明表的用例，而这个用例也基本上跟Service API匹配。

- ACT_GE_* : “GE”代表“General”（通用），用在各种情况下；

- ACT_HI_* : “HI”代表“History”（历史），这些表中保存的都是历史数据，比如执行过的流程实例、变量、任务，等等。

  __Activit默认提供了4种历史级别,对于几种级别根据对功能的要求选择，如果需要日后跟踪详细可以开启full：__
    1. none: 不保存任何历史记录，可以提高系统性能；
    2. activity：保存所有的流程实例、任务、活动信息；
    3. audit：也是Activiti的默认级别，保存所有的流程实例、任务、活动、表单属性；
    4. full：最完整的历史记录，除了包含audit级别的信息之外还能保存详细，例如：流程变量。

- ACT_ID_* : “ID”代表“Identity”（身份），这些表中保存的都是身份信息，如用户和组以及两者之间的关系。如果Activiti被集成在某一系统当中的话，这些表可以不用，可以直接使用现有系统中的用户或组信息；

- ACT_RE_* : “RE”代表“Repository”（仓库），这些表中保存一些‘静态’信息，如流程定义和流程资源（如图片、规则等）；

- ACT_RU_* :
  “RU”代表“Runtime”（运行时），这些表中保存一些流程实例、用户任务、变量等的运行时数据。Activiti只保存流程实例在执行过程中的运行时数据，并且当流程结束后会立即移除这些数据，这是为了保证运行时表尽量的小并运行的足够快；

## 1.2 数据库表结构

### 1.2.1 Activiti数据表清单:

| 表分类                       | 表名                                                                                                                                                                                        | 解释                                                                                                              | 
|:--------------------------|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------|
| 一般数据                      | ACT_GE_BYTEARRAY<br/> ACT_GE_PROPERTY                                                                                                                                                     | 通用的流程定义和流程资源<br/> 系统相关属性,会默认插入三条记录                                                                              |
| 流程历史记录                    | ACT_HI_ACTINST<br/>ACT_HI_ATTACHMENT<br/>ACT_HI_COMMENT<br/>ACT_HI_DETAIL<br/>ACT_HI_IDENTITYLINK<br/>ACT_HI_PROCINST<br/>ACT_HI_TASKINST<br/>ACT_HI_VARINST                              | 历史的流程实例<br/>历史的流程附件<br/>历史的说明性信息<br/>历史的流程运行中的细节信息<br/>历史的流程运行过程中用户关系<br/>历史的流程实例<br/>历史的任务实例<br/>历史的流程运行中的变量信息 |
| 事件日志                      | ACT_EVT_LOG                                                                                                                                                                               | 事件日志                                                                                                            |
| 流程定义的动态变更信息               | ACT_PROCDEF_INFO                                                                                                                                                                          | 流程定义的动态变更信息                                                                                                     |
| 用户用户组表， v7.1.0.M6好像没找到这些表 | ACT_ID_GROUP<br/>ACT_ID_INFO<br/>ACT_ID_MEMBERSHIP <br/>ACT_ID_USER                                                                                                                       | 身份信息-组信息<br/>身份信息-组信息<br/>身份信息-用户和组关系的中间表<br/>身份信息-用户信息                                                         |
| 流程定义表                     | ACT_RE_DEPLOYMENT<br/>ACT_RE_MODEL  <br/>ACT_RE_PROCDEF                                                                                                                                   | 部署单元信息<br/>模型信息 <br/>已部署的流程定义                                                                                   |
| 运行实例表                     | ACT_RU_DEADLETTER_JOB<br/>ACT_RU_EVENT_SUBSCR<br/>ACT_RU_EXECUTION<br/>ACT_RU_IDENTITYLINK<br/>ACT_RU_INTEGRATION<br/>ACT_RU_JOB<br/>ACT_RU_TASK<br/>ACT_RU_TIMER_JOB<br/>ACT_RU_VARIABLE | 作业失败<br/>运行时事件<br/>运行时流程执行实例<br/>运行时用户关系信息<br/>运行时综合<br/>运行时作业<br/>运行时任务<br/>运行时定时器<br/>运行时变量表                  |

### 对象和表的对应关系

| 映射文件名称                       | 含义             | 实体类名                           | 数据库表名                 |
|:-----------------------------|:---------------|--------------------------------|-----------------------|
| Attachment.xml               | 历史的流程附件        | AttachmentEntity               | ACT_HI_ATTACHMENT     |
| ByteArray.xml                | 二进制数据表         | ByteArrayEntity                | ACT_GE_BYTEARRAY      |
| Comment.xml                  | 历史的说明性信息       | CommentEntity                  | ACT_HI_COMMENT        |
| DeadLetterJob.xml            | 作业死亡信息表        | DeadLetterJobEntity            | ACT_RU_DEADLETTER_JOB |
| Deployment.xml               | 部署单元信息         | DeploymentEntity               | ACT_RE_DEPLOYMENT     |
| EventLogEntry.xml            | 流程引擎通用事件日志记录表  | EventLogEntryEntity            | ACT_EVT_LOG           |
| EventSubscription.xml        | 运行时事件          | EventSubscriptionEntity        | ACT_RU_EVENT_SUBSCR   |
| Execution.xml                | 运行时流程执行实例      | ExecutionEntity                | ACT_RU_EXECUTION      |
| HistoricActivityInstance.xml | 历史流程实例         | HistoricActivityInstanceEntity | ACT_HI_ACTINST        |
| HistoricDetail.xml           | 历史的流程运行中的细节信息  | HistoricDetailEntity           | ACT_HI_DETAIL         |
| HistoricIdentityLink.xml     | 历史的流程运行过程中用户关系 | HistoricIdentityLinkEntity     | ACT_HI_IDENTITYLINK   |
| HistoricProcessInstance.xml  | 历史的流程实例        | HistoricProcessInstanceEntity  | ACT_HI_PROCINST       |
| HistoricTaskInstance.xml     | 历史的任务实例        | HistoricTaskInstanceEntity     | ACT_HI_TASKINST       |
| HistoricVariableInstance.xml | 历史的流程运行中的变量信息  | HistoricVariableInstanceEntity | ACT_HI_VARINST        |
| IdentityLink.xml             | 运行时用户关系信息      | IdentityLinkEntity             | ACT_RU_IDENTITYLINK   |
| IntegrationContext.xml       | 运行时积分表         | IntegrationContextEntity       | ACT_RU_INTEGRATION    |
| Job.xml                      | 运行时作业          | JobEntity                      | ACT_RU_JOB            |
| Model.xml                    | 模型信息           | ModelEntity                    | ACT_RE_MODEL          |
| ProcessDefinition.xml        | 已部署的流程定义       | ProcessDefinitionEntity        | ACT_RE_PROCDEF        |
| ProcessDefinitionInfo.xml    | 流程定义的动态变更信息    | ProcessDefinitionInfoEntity    | ACT_PROCDEF_INFO      |
| Property.xml                 | 系统相关属性         | PropertyEntity                 | ACT_GE_PROPERTY       |
| Resource.xml                 |                | ResourceEntity                 | ACT_GE_BYTEARRAY      |
| SuspendedJob.xml             | 运行时作业暂停表       | SuspendedJobEntity             | ACT_RU_SUSPENDED_JOB  |
| TableData.xml                | 运行时作业暂停表       | 无                              | 任意表                   |
| Task.xml                     | 运行时任务          | TaskEntity                     | ACT_RU_TASK           |
| Timerjob.xml                 | 运行时定时器作业表      | TimerJobEntity                 | ACT_RU_TIMER_JOB      |
| VariableInstance.xml         | 运行时变量表         | VariableInstanceEntity         | ACT_RU_VARIABLE       |

### ACT_EVT_LOG(act_evt_log) （流程引擎通用事件日志记录表）

````sql
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
  COLLATE = utf8mb3_bin;
````

### ACT_GE_BYTEARRAY(act_ge_bytearray)（通用的流程定义和流程资源）

用来保存部署文件的大文本数据。

保存流程定义图片和xml、Serializable(序列化)
的变量,即保存所有二进制数据，特别注意类路径部署时候，不要把svn等隐藏文件或者其他与流程无关的文件也一起部署到该表中，会造成一些错误（可能导致流程定义无法删除）。

````sql
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
  COLLATE = utf8mb3_bin;
````

### ACT_GE_PROPERTY(act_ge_property)（系统相关属性）

属性数据表。存储这个流程引擎级别的数据。

````sql
CREATE TABLE `act_ge_property`
(
    `NAME_`  varchar(64) NOT NULL COMMENT '属性名称,schema.version,schema.history,next.dbid',
    `VALUE_` varchar(300) DEFAULT NULL COMMENT '属性值,5.*,create(5.*)',
    `REV_`   int(11)      DEFAULT NULL COMMENT '乐观锁版本号',
    PRIMARY KEY (`NAME_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3
  COLLATE = utf8mb3_bin;
````

#### ACT_HI_ACTINST(act_hi_actinst)（历史节点表）

历史活动信息。这里记录流程流转过的所有节点，与HI_TASKINST不同的是，taskinst只记录usertask内容。

````sql
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
  COLLATE = utf8mb3_bin;
````

### ACT_HI_ATTACHMENT(act_hi_attachment)（附件信息）

````sql
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
  COLLATE = utf8mb3_bin;
````

### 表名:ACT_HI_COMMENT(act_hi_comment)（历史审批意见表）

````sql
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
  COLLATE = utf8mb3_bin;
````

### ACT_HI_DETAIL(act_hi_detail)（历史详细信息）

历史详情表：流程中产生的变量详细，包括控制流程流转的变量，业务表单中填写的流程需要用到的变量等。

````sql
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
  COLLATE = utf8mb3_bin;
````

### ACT_HI_IDENTITYLINK(act_hi_identitylink) （历史流程人员表）

任务参与者数据表。主要存储历史节点参与者的信息。

````sql
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
  COLLATE = utf8mb3_bin;
````

### ACT_HI_PROCINST(act_hi_procinst)（历史流程实例信息）核心表

````sql
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
  COLLATE = utf8mb3_bin;
````

### ACT_HI_TASKINST(act_hi_taskinst)（历史任务流程实例信息）核心表

````sql
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
  COLLATE = utf8mb3_bin;
````

### ACT_HI_VARINST(act_hi_varinst)（历史变量信息）

````sql
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
  COLLATE = utf8mb3_bin;
````

### ACT_PROCDEF_INFO(act_procdef_info)  (已部署的流程定义)

````sql
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
  COLLATE = utf8mb3_bin;
````

### ACT_RE_DEPLOYMENT(act_re_deployment)（部署信息表）

用来存储部署时需要持久化保存下来的信息

````sql
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
  COLLATE = utf8mb3_bin;
````

### ACT_RE_MODEL(act_re_model)(流程设计模型表)

创建流程的设计模型时，保存在该数据表中。

````sql
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
  COLLATE = utf8mb3_bin;
````

### ACT_RE_PROCDEF(act_re_procdef)（流程定义：解析表）

流程解析表，解析成功了，在该表保存一条记录。业务流程定义数据表

此表和ACT_RE_DEPLOYMENT是多对一的关系，即，一个部署的bar包里可能包含多个流程定义文件，每个流程定义文件都会有一条记录在ACT_RE_PROCDEF表内，每个流程定义的数据，都会对于ACT_GE_BYTEARRAY表内的一个资源文件和PNG图片文件。和ACT_GE_BYTEARRAY的关联是通过程序用ACT_GE_BYTEARRAY.NAME与ACT_RE_PROCDEF.NAME_完成的，在数据库表结构中没有体现。

````sql
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
  COLLATE = utf8mb3_bin;
````

### ACT_RU_DEADLETTER_JOB(act_ru_deadletter_job)(作业死亡信息表)

````sql
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
  COLLATE = utf8mb3_bin;
````

### ACT_RU_EVENT_SUBSCR(act_ru_event_subscr)(运行时事件)

````sql
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
  COLLATE = utf8mb3_bin;
````

### ACT_RU_EXECUTION(act_ru_execution)（运行时流程执行实例）

核心，我的代办任务查询表

````sql
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
  COLLATE = utf8mb3_bin;
````

### ACT_RU_IDENTITYLINK(act_ru_identitylink)（身份联系）

主要存储当前节点参与者的信息,任务参与者数据表。

````sql
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
  COLLATE = utf8mb3_bin;
````

#### ACT_RU_INTEGRATION(act_ru_integration)(运行时综合表)

````sql
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
  COLLATE = utf8mb3_bin;
````

### ACT_RU_JOB(act_ru_job)（运行中的任务）

运行时定时任务数据表

````sql
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
  COLLATE = utf8mb3_bin;
````

### ACT_RU_SUSPENDED_JOB(act_ru_suspended_job)( 运行时作业暂停表)

````sql
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
  COLLATE = utf8mb3_bin;
````

### ACT_RU_TASK(act_ru_task)(运行时任务数据表)

（执行中实时任务）代办任务查询表

````sql
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
  COLLATE = utf8mb3_bin;
````

### ACT_RU_TIMER_JOB(act_ru_timer_job)( 运行时定时器作业表)

````sql
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
  COLLATE = utf8mb3_bin;
````

#### 1.2.24 表名:ACT_RU_VARIABLE(act_ru_variable)(运行时流程变量数据表)

````sql
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
  COLLATE = utf8mb3_bin;
````
