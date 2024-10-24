# 1 Activiti数据库表结构

## 1.1 数据库表名说明

Activiti 7 工作流总共包含 25 张数据表，所有的表名默认以 'ACT_' 开头。

| 表名                    | 注释        |
|-----------------------|-----------|
| ACT_EVT_LOG           | 流程事件日志记录表 |
| ACT_GE_BYTEARRAY      | 二进制数据表    |
| ACT_GE_PROPERTY       | 系统相关属性表   |
| ACT_HI_ACTINST        | 历史节点表     |
| ACT_HI_ATTACHMENT     | 历史附件表     |
| ACT_HI_COMMENT        | 历史审批意见表   |
| ACT_HI_DETAIL         | 历史详情表     |
| ACT_HI_IDENTITYLINK   | 历史身份链接表   |
| ACT_HI_PROCINST       | 历史流程实例表   |
| ACT_HI_TASKINST       | 历史任务实例表   |
| ACT_HI_VARINST        | 历史变量表     |
| ACT_PROCDEF_INFO      | 流程定义信息表   |
| ACT_RE_DEPLOYMENT     | 流程部署表     |
| ACT_RE_MODEL          | 流程模型表     |
| ACT_RE_PROCDEF        | 流程定义表     |
| ACT_RU_DEADLETTER_JOB | 作业死亡信息表   |
| ACT_RU_EVENT_SUBSCR   | 事件订阅表       |
| ACT_RU_EXECUTION      | 流程执行实例表   |
| ACT_RU_IDENTITYLINK   | 身份链接表     |
| ACT_RU_INTEGRATION    | 集成综合表    |
| ACT_RU_JOB            | 定时任务表     |
| ACT_RU_SUSPENDED_JOB  | 作业暂停表     |
| ACT_RU_TASK           | 任务数据表     |
| ACT_RU_TIMER_JOB      | 定时器作业表    |
| ACT_RU_VARIABLE       | 流程变量数据表   |

并且表名的第二部分用两个字母表明表的用例，而这个用例也基本上跟Service API匹配。

- ACT_GE_*：“GE”代表“General”（通用），用在各种情况下；

- ACT_HI_*：“HI”代表“History”（历史），这些表中保存的都是历史数据，比如执行过的流程实例、变量、任务，等等。

  **Activit 默认提供了4种历史级别,对于几种级别根据对功能的要求选择，如果需要日后跟踪详细可以开启 full：**

    1. none：不保存任何历史记录，可以提高系统性能；

    2. activity：保存所有的流程实例、任务、活动信息；

    3. audit：也是Activiti的默认级别，保存所有的流程实例、任务、活动、表单属性；

    4. full：最完整的历史记录，除了包含audit级别的信息之外还能保存详细，例如：流程变量。

- ACT_ID_*：“ID”代表“Identity”（身份），这些表中保存的都是身份信息，如用户和组以及两者之间的关系。如果Activiti被集成在某一系统当中的话，这些表可以不用，可以直接使用现有系统中的用户或组信息；

- ACT_RE_*：“RE”代表“Repository”（仓库），这些表中保存一些‘静态’信息，如流程定义和流程资源（如图片、规则等）；

- ACT_RU_*：“RU”代表“Runtime”（运行时），这些表中保存一些流程实例、用户任务、变量等的运行时数据。
  Activiti只保存流程实例在执行过程中的运行时数据，并且当流程结束后会立即移除这些数据，这是为了保证运行时表尽量的小并运行的足够快；

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

### ACT_EVT_LOG(act_evt_log) （流程事件日志记录表）

````sql
drop table if exists `act_evt_log`;
CREATE TABLE `act_evt_log`
(
  `LOG_NR_`       bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `TYPE_`         varchar(64)         DEFAULT NULL COMMENT '事件类型',
  `PROC_DEF_ID_`  varchar(64)         DEFAULT NULL COMMENT '流程定义 ID（ACT_RE_PROCDEF 表 ID_）',
  `PROC_INST_ID_` varchar(64)         DEFAULT NULL COMMENT '流程实例 ID（ACT_RU_EXECUTION 表 PROC_INST_ID_，PS：如果没有子节点，则此字段值为 ID_，否则值为父节点 ID_）',
  `EXECUTION_ID_` varchar(64)         DEFAULT NULL COMMENT '执行实例 ID（ACT_RU_EXECUTION 表 ID_）',
  `TASK_ID_`      varchar(64)         DEFAULT NULL COMMENT '任务 ID（ACT_RU_TASK 表 ID_，如果流程结束，该表数据会清空，可以在 ACT_HI_TASKINST 表中找到历史数据）',
  `TIME_STAMP_`   timestamp  NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '事件发生的时间戳',
  `USER_ID_`      varchar(255)        DEFAULT NULL COMMENT '执行事件的用户 ID',
  `DATA_`         longblob            DEFAULT NULL COMMENT '事件数据',
  `LOCK_OWNER_`   varchar(255)        DEFAULT NULL COMMENT '当前锁定该记录的用户或进程',
  `LOCK_TIME_`    timestamp  NULL     DEFAULT NULL COMMENT '锁定时间',
  `IS_PROCESSED_` tinyint(4)          DEFAULT 0 COMMENT '是否处理：1-已处理；0-未处理；',
  PRIMARY KEY (`LOG_NR_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='流程事件日志记录表';
````

### ACT_GE_BYTEARRAY(act_ge_bytearray)（二进制数据表，存放资源文件（ 图片, xml ））

用来保存部署文件的大文本数据。

保存流程定义图片和xml、Serializable(序列化)
的变量,即保存所有二进制数据，特别注意类路径部署时候，不要把svn等隐藏文件或者其他与流程无关的文件也一起部署到该表中，会造成一些错误（可能导致流程定义无法删除）。

````sql
drop table if exists `act_ge_bytearray`;
CREATE TABLE `act_ge_bytearray`
(
  `ID_`            varchar(64) NOT NULL COMMENT '主键',
  `REV_`           int(11)      DEFAULT NULL COMMENT '乐观锁版本号',
  `NAME_`          varchar(255) DEFAULT NULL COMMENT '部署的文件名称。mail.bpmn、mail.png、mail.bpmn20.xml',
  `DEPLOYMENT_ID_` varchar(64)  DEFAULT NULL COMMENT '部署 ID（ACT_RE_DEPLOYMENT 表 ID_）',
  `BYTES_`         longblob     DEFAULT NULL COMMENT '文本字节流',
  `GENERATED_`     tinyint(4)   DEFAULT NULL COMMENT '是否是引擎生成：0-用户生成；1-Activiti生成；',
  PRIMARY KEY (`ID_`),
  KEY `ACT_FK_BYTEARR_DEPL` (`DEPLOYMENT_ID_`),
  CONSTRAINT `ACT_FK_BYTEARR_DEPL` FOREIGN KEY (`DEPLOYMENT_ID_`) REFERENCES `act_re_deployment` (`ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='二进制数据表，存放资源文件（ 图片, xml ）';
````

### ACT_GE_PROPERTY(act_ge_property)（系统相关属性表）

属性数据表。存储这个流程引擎级别的数据。

````sql
drop table if exists `act_ge_property`;
CREATE TABLE `act_ge_property`
(
  `NAME_`  varchar(64) NOT NULL COMMENT '主键，属性名称。schema.version、schema.history、next.dbid',
  `VALUE_` varchar(300) DEFAULT NULL COMMENT '属性值。5.*、create ( 5.* )',
  `REV_`   int(11)      DEFAULT NULL COMMENT '乐观锁版本号',
  PRIMARY KEY (`NAME_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='系统相关属性表';
````

#### ACT_HI_ACTINST(act_hi_actinst)（历史节点表）

历史活动信息。这里记录流程流转过的所有节点，与HI_TASKINST不同的是，taskinst只记录usertask内容。

````sql
drop table if exists `act_hi_actinst`;
CREATE TABLE `act_hi_actinst`
(
  `ID_`                varchar(64)  NOT NULL COMMENT '主键',
  `PROC_DEF_ID_`       varchar(64)  NOT NULL COMMENT '流程定义 ID（ACT_RE_PROCDEF 表 ID_）',
  `PROC_INST_ID_`      varchar(64)  NOT NULL COMMENT '流程实例 ID（ACT_RU_EXECUTION 表 PROC_INST_ID_，PS：如果没有子节点，则此字段值为 ID_，否则值为父节点 ID_）',
  `EXECUTION_ID_`      varchar(64)  NOT NULL COMMENT '执行实例 ID（ACT_RU_EXECUTION 表 ID_）',
  `ACT_ID_`            varchar(255) NOT NULL COMMENT '活动节点 ID（对应流程文件 process 中的节点 id）',
  `TASK_ID_`           varchar(64)   DEFAULT NULL COMMENT '任务 ID，其他自动完成的节点类型，该值为空（ACT_RU_TASK 表 ID_，如果流程结束，该表数据会清空，可以在 ACT_HI_TASKINST 表中找到历史数据）',
  `CALL_PROC_INST_ID_` varchar(64)   DEFAULT NULL COMMENT '调用流程实例 ID',
  `ACT_NAME_`          varchar(255)  DEFAULT NULL COMMENT '活动名称，流程文件节点定义的名称',
  `ACT_TYPE_`          varchar(255) NOT NULL COMMENT '活动类型，如startEvent、userTask',
  `ASSIGNEE_`          varchar(255)  DEFAULT NULL COMMENT '任务办理人',
  `START_TIME_`        datetime     NOT NULL COMMENT '开始时间',
  `END_TIME_`          datetime      DEFAULT NULL COMMENT '结束时间',
  `DURATION_`          bigint(20)    DEFAULT NULL COMMENT '总耗时（毫秒）',
  `DELETE_REASON_`     varchar(4000) DEFAULT NULL COMMENT '删除原因',
  `TENANT_ID_`         varchar(255)  DEFAULT '' COMMENT '租户 ID',
  PRIMARY KEY (`ID_`),
  KEY `ACT_IDX_HI_ACT_INST_START` (`START_TIME_`),
  KEY `ACT_IDX_HI_ACT_INST_END` (`END_TIME_`),
  KEY `ACT_IDX_HI_ACT_INST_PROCINST` (`PROC_INST_ID_`, `ACT_ID_`),
  KEY `ACT_IDX_HI_ACT_INST_EXEC` (`EXECUTION_ID_`, `ACT_ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='历史节点表';
````

### ACT_HI_ATTACHMENT(act_hi_attachment)（历史附件表）

````sql
drop table if exists `act_hi_attachment`;
CREATE TABLE `act_hi_attachment`
(
  `ID_`           varchar(64) NOT NULL COMMENT '主键',
  `REV_`          int(11)       DEFAULT NULL COMMENT '乐观锁版本号',
  `USER_ID_`      varchar(255)  DEFAULT NULL COMMENT '用户 ID',
  `NAME_`         varchar(255)  DEFAULT NULL COMMENT '名称',
  `DESCRIPTION_`  varchar(4000) DEFAULT NULL COMMENT '描述',
  `TYPE_`         varchar(255)  DEFAULT NULL COMMENT '类型',
  `TASK_ID_`      varchar(64)   DEFAULT NULL COMMENT '任务 ID（ACT_RU_TASK 表 ID_，如果流程结束，该表数据会清空，可以在 ACT_HI_TASKINST 表中找到历史数据）',
  `PROC_INST_ID_` varchar(64)   DEFAULT NULL COMMENT '流程实例 ID（ACT_RU_EXECUTION 表 PROC_INST_ID_，PS：如果没有子节点，则此字段值为 ID_，否则值为父节点 ID_）',
  `URL_`          varchar(4000) DEFAULT NULL COMMENT '附件地址',
  `CONTENT_ID_`   varchar(64)   DEFAULT NULL COMMENT '内容 ID（ACT_GE_BYTEARRAY 表 ID_）',
  `TIME_`         datetime      DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='历史附件表';
````

### 表名:ACT_HI_COMMENT(act_hi_comment)（历史审批意见表）

````sql
drop table if exists `act_hi_comment`;
CREATE TABLE `act_hi_comment`
(
  `ID_`           varchar(64) NOT NULL COMMENT '主键',
  `TYPE_`         varchar(255)  DEFAULT NULL COMMENT '类型：event-事件；comment-意见；',
  `TIME_`         datetime    NOT NULL COMMENT '创建时间',
  `USER_ID_`      varchar(255)  DEFAULT NULL COMMENT '用户 ID',
  `TASK_ID_`      varchar(64)   DEFAULT NULL COMMENT '任务 ID（ACT_RU_TASK 表 ID_，如果流程结束，该表数据会清空，可以在 ACT_HI_TASKINST 表中找到历史数据）',
  `PROC_INST_ID_` varchar(64)   DEFAULT NULL COMMENT '流程实例 ID（ACT_RU_EXECUTION 表 PROC_INST_ID_，PS：如果没有子节点，则此字段值为 ID_，否则值为父节点 ID_）',
  `ACTION_`       varchar(255)  DEFAULT NULL COMMENT '行为类型：AddUserLink、DeleteUserLink、AddGroupLink、DeleteGroupLink、AddComment、AddAttachment、DeleteAttachment',
  `MESSAGE_`      varchar(4000) DEFAULT NULL COMMENT '审批意见',
  `FULL_MSG_`     longblob      DEFAULT NULL COMMENT '全部消息',
  PRIMARY KEY (`ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='历史审批意见表';
````

### ACT_HI_DETAIL(act_hi_detail)（历史详情表）

历史详情表：流程中产生的变量详细，包括控制流程流转的变量，业务表单中填写的流程需要用到的变量等。

````sql
drop table if exists `act_hi_detail`;
CREATE TABLE `act_hi_detail`
(
  `ID_`           varchar(64)  NOT NULL COMMENT '主键',
  `TYPE_`         varchar(255) NOT NULL COMMENT '类型：FormProperty-表单；VariableUpdate-参数；',
  `PROC_INST_ID_` varchar(64)   DEFAULT NULL COMMENT '流程实例 ID（ACT_RU_EXECUTION 表 PROC_INST_ID_，PS：如果没有子节点，则此字段值为 ID_，否则值为父节点 ID_）',
  `EXECUTION_ID_` varchar(64)   DEFAULT NULL COMMENT '执行实例 ID（ACT_RU_EXECUTION 表 ID_）',
  `TASK_ID_`      varchar(64)   DEFAULT NULL COMMENT '任务 ID（ACT_RU_TASK 表 ID_，如果流程结束，该表数据会清空，可以在 ACT_HI_TASKINST 表中找到历史数据）',
  `ACT_INST_ID_`  varchar(64)   DEFAULT NULL COMMENT '活动实例 ID',
  `NAME_`         varchar(255) NOT NULL COMMENT '变量名称',
  `VAR_TYPE_`     varchar(255)  DEFAULT NULL COMMENT '变量类型：jpa-entity、boolean、bytes、serializable（可序列化）、自定义type（根据你自身配置）、 CustomVariableType、date、double、integer、long、null、short、string',
  `REV_`          int(11)       DEFAULT NULL COMMENT '乐观锁版本号',
  `TIME_`         datetime     NOT NULL COMMENT '创建时间',
  `BYTEARRAY_ID_` varchar(64)   DEFAULT NULL COMMENT 'ACT_GE_BYTEARRAY 表 ID_',
  `DOUBLE_`       double        DEFAULT NULL COMMENT '变量的 double 类型值',
  `LONG_`         bigint(20)    DEFAULT NULL COMMENT '变量的 long 类型值',
  `TEXT_`         varchar(4000) DEFAULT NULL COMMENT '变量的文本值，如此处存储 JPA 持久化对象时，值存 ClassName',
  `TEXT2_`        varchar(4000) DEFAULT NULL COMMENT '变量的附加文本值，此处存储的是 JPA 持久化对象时，此值存对象 ID',
  PRIMARY KEY (`ID_`),
  KEY `ACT_IDX_HI_DETAIL_PROC_INST` (`PROC_INST_ID_`),
  KEY `ACT_IDX_HI_DETAIL_ACT_INST` (`ACT_INST_ID_`),
  KEY `ACT_IDX_HI_DETAIL_TIME` (`TIME_`),
  KEY `ACT_IDX_HI_DETAIL_NAME` (`NAME_`),
  KEY `ACT_IDX_HI_DETAIL_TASK_ID` (`TASK_ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='历史详情表';
````

### ACT_HI_IDENTITYLINK(act_hi_identitylink) （历史身份链接表）

任务参与者数据表。主要存储历史节点参与者的信息。

````sql
drop table if exists `act_hi_identitylink`;
CREATE TABLE `act_hi_identitylink`
(
  `ID_`           varchar(64) NOT NULL COMMENT '主键',
  `GROUP_ID_`     varchar(255) DEFAULT NULL COMMENT '组 ID',
  `TYPE_`         varchar(255) DEFAULT NULL COMMENT '组类型：assignee-受让人；candidate-候选人；owner-所有者；starter-起动器；participant-参与者；',
  `USER_ID_`      varchar(255) DEFAULT NULL COMMENT '用户 ID',
  `TASK_ID_`      varchar(64)  DEFAULT NULL COMMENT '任务 ID（ACT_RU_TASK 表 ID_，如果流程结束，该表数据会清空，可以在 ACT_HI_TASKINST 表中找到历史数据）',
  `PROC_INST_ID_` varchar(64)  DEFAULT NULL COMMENT '流程实例 ID（ACT_RU_EXECUTION 表 PROC_INST_ID_，PS：如果没有子节点，则此字段值为 ID_，否则值为父节点 ID_）',
  PRIMARY KEY (`ID_`),
  KEY `ACT_IDX_HI_IDENT_LNK_USER` (`USER_ID_`),
  KEY `ACT_IDX_HI_IDENT_LNK_TASK` (`TASK_ID_`),
  KEY `ACT_IDX_HI_IDENT_LNK_PROCINST` (`PROC_INST_ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='历史身份链接表';
````

### ACT_HI_PROCINST(act_hi_procinst)（历史流程实例表）核心表

````sql
drop table if exists `act_hi_procinst`;
CREATE TABLE `act_hi_procinst`
(
  `ID_`                        varchar(64) NOT NULL COMMENT '主键',
  `PROC_INST_ID_`              varchar(64) NOT NULL COMMENT '流程实例 ID（ACT_RU_EXECUTION 表 PROC_INST_ID_，PS：如果没有子节点，则此字段值为 ID_，否则值为父节点 ID_）',
  `BUSINESS_KEY_`              varchar(255)  DEFAULT NULL COMMENT '业务系统 Key（流程定义 key:表单 ID）',
  `PROC_DEF_ID_`               varchar(64) NOT NULL COMMENT '流程定义 ID（ACT_RE_PROCDEF 表 ID_）',
  `START_TIME_`                datetime    NOT NULL COMMENT '开始时间',
  `END_TIME_`                  datetime      DEFAULT NULL COMMENT '结束时间',
  `DURATION_`                  bigint(20)    DEFAULT NULL COMMENT '总耗时（毫秒）',
  `START_USER_ID_`             varchar(255)  DEFAULT NULL COMMENT '开始用户 ID',
  `START_ACT_ID_`              varchar(255)  DEFAULT NULL COMMENT '开始节点 ID（对应流程文件 process 中的节点 id）',
  `END_ACT_ID_`                varchar(255)  DEFAULT NULL COMMENT '结束节点 ID（对应流程文件 process 中的节点 id）',
  `SUPER_PROCESS_INSTANCE_ID_` varchar(64)   DEFAULT NULL COMMENT '上级流程实例 ID',
  `DELETE_REASON_`             varchar(4000) DEFAULT NULL COMMENT '删除原因',
  `TENANT_ID_`                 varchar(255)  DEFAULT '' COMMENT '租户 ID',
  `NAME_`                      varchar(255)  DEFAULT NULL COMMENT '流程实例名称',
  PRIMARY KEY (`ID_`),
  UNIQUE KEY `PROC_INST_ID_` (`PROC_INST_ID_`),
  KEY `ACT_IDX_HI_PRO_INST_END` (`END_TIME_`),
  KEY `ACT_IDX_HI_PRO_I_BUSKEY` (`BUSINESS_KEY_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='历史流程实例表';
````

### ACT_HI_TASKINST(act_hi_taskinst)（历史任务实例表）核心表

````sql
drop table if exists `act_hi_taskinst`;
CREATE TABLE `act_hi_taskinst`
(
  `ID_`             varchar(64) NOT NULL COMMENT '主键',
  `PROC_DEF_ID_`    varchar(64)   DEFAULT NULL COMMENT '流程定义 ID（ACT_RE_PROCDEF 表 ID_）',
  `TASK_DEF_KEY_`   varchar(255)  DEFAULT NULL COMMENT '任务定义 Key（对应流程文件 process 中的节点 id）',
  `PROC_INST_ID_`   varchar(64)   DEFAULT NULL COMMENT '流程实例 ID（ACT_RU_EXECUTION 表 PROC_INST_ID_，PS：如果没有子节点，则此字段值为 ID_，否则值为父节点 ID_）',
  `EXECUTION_ID_`   varchar(64)   DEFAULT NULL COMMENT '执行实例 ID（ACT_RU_EXECUTION 表 ID_）',
  `NAME_`           varchar(255)  DEFAULT NULL COMMENT '任务名称',
  `PARENT_TASK_ID_` varchar(64)   DEFAULT NULL COMMENT '父任务 ID',
  `DESCRIPTION_`    varchar(4000) DEFAULT NULL COMMENT '任务描述',
  `OWNER_`          varchar(255)  DEFAULT NULL COMMENT '任务的拥有者（默认为空，只有在委托时才有值）',
  `ASSIGNEE_`       varchar(255)  DEFAULT NULL COMMENT '任务办理人',
  `START_TIME_`     datetime    NOT NULL COMMENT '开始时间',
  `CLAIM_TIME_`     datetime      DEFAULT NULL COMMENT '任务认领时间',
  `END_TIME_`       datetime      DEFAULT NULL COMMENT '结束时间',
  `DURATION_`       bigint(20)    DEFAULT NULL COMMENT '总耗时（毫秒）',
  `DELETE_REASON_`  varchar(4000) DEFAULT NULL COMMENT '删除原因',
  `PRIORITY_`       int(11)       DEFAULT NULL COMMENT '优先级（默认 50）',
  `DUE_DATE_`       datetime      DEFAULT NULL COMMENT '应完成时间，表明任务应在多长时间内完成',
  `FORM_KEY_`       varchar(255)  DEFAULT NULL COMMENT '任务的表单键，节点定义的 form_key 属性',
  `CATEGORY_`       varchar(255)  DEFAULT NULL COMMENT '任务类型',
  `TENANT_ID_`      varchar(255)  DEFAULT '' COMMENT '租户 ID',
  PRIMARY KEY (`ID_`),
  KEY `ACT_IDX_HI_TASK_INST_PROCINST` (`PROC_INST_ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='历史任务实例表';
````

### ACT_HI_VARINST(act_hi_varinst)（历史变量表）

````sql
drop table if exists `act_hi_varinst`;
CREATE TABLE `act_hi_varinst`
(
  `ID_`                varchar(64)  NOT NULL COMMENT '主键',
  `PROC_INST_ID_`      varchar(64)   DEFAULT NULL COMMENT '流程实例 ID（ACT_RU_EXECUTION 表 PROC_INST_ID_，PS：如果没有子节点，则此字段值为 ID_，否则值为父节点 ID_）',
  `EXECUTION_ID_`      varchar(64)   DEFAULT NULL COMMENT '执行实例 ID（ACT_RU_EXECUTION 表 ID_）',
  `TASK_ID_`           varchar(64)   DEFAULT NULL COMMENT '任务 ID（ACT_RU_TASK 表 ID_，如果流程结束，该表数据会清空，可以在 ACT_HI_TASKINST 表中找到历史数据）',
  `NAME_`              varchar(255) NOT NULL COMMENT '变量名称',
  `VAR_TYPE_`          varchar(100)  DEFAULT NULL COMMENT '变量类型：jpa-entity、boolean、bytes、serializable（可序列化）、自定义type（根据你自身配置）、 CustomVariableType、date、double、integer、long、null、short、string',
  `REV_`               int(11)       DEFAULT NULL COMMENT '乐观锁版本号',
  `BYTEARRAY_ID_`      varchar(64)   DEFAULT NULL COMMENT 'ACT_GE_BYTEARRAY 表 ID_',
  `DOUBLE_`            double        DEFAULT NULL COMMENT '变量的 double 类型值',
  `LONG_`              bigint(20)    DEFAULT NULL COMMENT '变量的 long 类型值',
  `TEXT_`              varchar(4000) DEFAULT NULL COMMENT '变量的文本值，如此处存储 JPA 持久化对象时，值存 ClassName',
  `TEXT2_`             varchar(4000) DEFAULT NULL COMMENT '变量的附加文本值，此处存储的是 JPA 持久化对象时，此值存对象 ID',
  `CREATE_TIME_`       datetime      DEFAULT NULL COMMENT '创建时间',
  `LAST_UPDATED_TIME_` datetime      DEFAULT NULL COMMENT '最后更新时间',
  PRIMARY KEY (`ID_`),
  KEY `ACT_IDX_HI_PROCVAR_PROC_INST` (`PROC_INST_ID_`),
  KEY `ACT_IDX_HI_PROCVAR_NAME_TYPE` (`NAME_`, `VAR_TYPE_`),
  KEY `ACT_IDX_HI_PROCVAR_TASK_ID` (`TASK_ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='历史变量表';
````

### ACT_PROCDEF_INFO(act_procdef_info)  (流程定义信息表)

````sql
drop table if exists `act_procdef_info`;
CREATE TABLE `act_procdef_info`
(
  `ID_`           varchar(64) NOT NULL COMMENT '主键',
  `PROC_DEF_ID_`  varchar(64) NOT NULL COMMENT 'ACT_RE_PROCDEF 表 ID',
  `REV_`          int(11)     DEFAULT NULL COMMENT '乐观锁版本号',
  `INFO_JSON_ID_` varchar(64) DEFAULT NULL COMMENT 'ACT_GE_BYTEARRAY 表 ID',
  PRIMARY KEY (`ID_`),
  UNIQUE KEY `ACT_UNIQ_INFO_PROCDEF` (`PROC_DEF_ID_`),
  KEY `ACT_IDX_INFO_PROCDEF` (`PROC_DEF_ID_`),
  KEY `ACT_FK_INFO_JSON_BA` (`INFO_JSON_ID_`),
  CONSTRAINT `ACT_FK_INFO_JSON_BA` FOREIGN KEY (`INFO_JSON_ID_`) REFERENCES `act_ge_bytearray` (`ID_`),
  CONSTRAINT `ACT_FK_INFO_PROCDEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `act_re_procdef` (`ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='流程定义信息表';
````

### ACT_RE_DEPLOYMENT(act_re_deployment)（流程部署表）

用来存储部署时需要持久化保存下来的信息

````sql
drop table if exists `act_re_deployment`;
CREATE TABLE `act_re_deployment`
(
  `ID_`                      varchar(64) NOT NULL COMMENT '主键',
  `NAME_`                    varchar(255)     DEFAULT NULL COMMENT '部署名称',
  `CATEGORY_`                varchar(255)     DEFAULT NULL COMMENT '类型',
  `KEY_`                     varchar(255)     DEFAULT NULL COMMENT '关键字',
  `TENANT_ID_`               varchar(255)     DEFAULT '' COMMENT '租户 ID',
  `DEPLOY_TIME_`             timestamp   NULL DEFAULT NULL COMMENT '部署时间',
  `ENGINE_VERSION_`          varchar(255)     DEFAULT NULL COMMENT '流程引擎版本号',
  `VERSION_`                 int(11)          DEFAULT 1 COMMENT '版本号，从 1 开始',
  `PROJECT_RELEASE_VERSION_` varchar(255)     DEFAULT NULL COMMENT '存储项目发布的版本信息',
  PRIMARY KEY (`ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='流程部署表';
````

### ACT_RE_MODEL(act_re_model)(流程模型表)

创建流程的设计模型时，保存在该数据表中。

````sql
drop table if exists `act_re_model`;
CREATE TABLE `act_re_model`
(
  `ID_`                           varchar(64) NOT NULL COMMENT '主键',
  `REV_`                          int(11)          DEFAULT NULL COMMENT '乐观锁版本号',
  `NAME_`                         varchar(255)     DEFAULT NULL COMMENT '模型名称',
  `KEY_`                          varchar(255)     DEFAULT NULL COMMENT '模型关键字',
  `CATEGORY_`                     varchar(255)     DEFAULT NULL COMMENT '类型',
  `CREATE_TIME_`                  timestamp   NULL DEFAULT NULL COMMENT '创建时间',
  `LAST_UPDATE_TIME_`             timestamp   NULL DEFAULT NULL COMMENT '最后修改时间',
  `VERSION_`                      int(11)          DEFAULT NULL COMMENT '版本号，从 1 开始',
  `META_INFO_`                    varchar(4000)    DEFAULT NULL COMMENT '以 json 格式保存流程定义的信息',
  `DEPLOYMENT_ID_`                varchar(64)      DEFAULT NULL COMMENT 'ACT_RE_DEPLOYMENT 表 ID_',
  `EDITOR_SOURCE_VALUE_ID_`       varchar(64)      DEFAULT NULL COMMENT '流程文件放在 ACT_GE_BYTEARRAY 表中的 ID_',
  `EDITOR_SOURCE_EXTRA_VALUE_ID_` varchar(64)      DEFAULT NULL COMMENT '流程文件图像放在 ACT_GE_BYTEARRAY 表中的 ID_',
  `TENANT_ID_`                    varchar(255)     DEFAULT '' COMMENT '租户 ID',
  PRIMARY KEY (`ID_`),
  KEY `ACT_FK_MODEL_SOURCE` (`EDITOR_SOURCE_VALUE_ID_`),
  KEY `ACT_FK_MODEL_SOURCE_EXTRA` (`EDITOR_SOURCE_EXTRA_VALUE_ID_`),
  KEY `ACT_FK_MODEL_DEPLOYMENT` (`DEPLOYMENT_ID_`),
  CONSTRAINT `ACT_FK_MODEL_DEPLOYMENT` FOREIGN KEY (`DEPLOYMENT_ID_`) REFERENCES `act_re_deployment` (`ID_`),
  CONSTRAINT `ACT_FK_MODEL_SOURCE` FOREIGN KEY (`EDITOR_SOURCE_VALUE_ID_`) REFERENCES `act_ge_bytearray` (`ID_`),
  CONSTRAINT `ACT_FK_MODEL_SOURCE_EXTRA` FOREIGN KEY (`EDITOR_SOURCE_EXTRA_VALUE_ID_`) REFERENCES `act_ge_bytearray` (`ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='流程模型表';
````

### ACT_RE_PROCDEF(act_re_procdef)（流程定义表）

流程解析表，解析成功了，在该表保存一条记录。业务流程定义数据表

此表和ACT_RE_DEPLOYMENT是多对一的关系，即，一个部署的bar包里可能包含多个流程定义文件，每个流程定义文件都会有一条记录在ACT_RE_PROCDEF表内，每个流程定义的数据，都会对于ACT_GE_BYTEARRAY表内的一个资源文件和PNG图片文件。和ACT_GE_BYTEARRAY的关联是通过程序用ACT_GE_BYTEARRAY.NAME与ACT_RE_PROCDEF.NAME_完成的，在数据库表结构中没有体现。

````sql
drop table if exists `act_re_procdef`;
CREATE TABLE `act_re_procdef`
(
  `ID_`                     varchar(64)  NOT NULL COMMENT '主键（规则：流程定义 key:流程定义版本:生成的 ID）',
  `REV_`                    int(11)       DEFAULT NULL COMMENT '乐观锁版本号',
  `CATEGORY_`               varchar(255)  DEFAULT NULL COMMENT '类型（流程文件定义 Namespace / 流程文件 targetNamespace 的属性值）',
  `NAME_`                   varchar(255)  DEFAULT NULL COMMENT '流程名称（流程文件 process 元素的 name 属性值）',
  `KEY_`                    varchar(255) NOT NULL COMMENT '流程编号（流程文件 process 元素的id属性值）',
  `VERSION_`                int(11)      NOT NULL COMMENT '版本号，从 1 开始',
  `DEPLOYMENT_ID_`          varchar(64)   DEFAULT NULL COMMENT 'ACT_RE_DEPLOYMENT 表 ID_',
  `RESOURCE_NAME_`          varchar(4000) DEFAULT NULL COMMENT '资源文件名称，流程 bpmn 文件名称',
  `DGRM_RESOURCE_NAME_`     varchar(4000) DEFAULT NULL COMMENT '图片资源文件名称，png 流程图片名称',
  `DESCRIPTION_`            varchar(4000) DEFAULT NULL COMMENT '描述信息',
  `HAS_START_FORM_KEY_`     tinyint(4)    DEFAULT NULL COMMENT 'start 节点是否存在 formKey：0-否；1-是；',
  `HAS_GRAPHICAL_NOTATION_` tinyint(4)    DEFAULT NULL COMMENT '是否有图形符号的标志',
  `SUSPENSION_STATE_`       int(11)       DEFAULT NULL COMMENT '是否挂起：1-激活；2-挂起；',
  `TENANT_ID_`              varchar(255)  DEFAULT '' COMMENT '租户 ID',
  `ENGINE_VERSION_`         varchar(255)  DEFAULT NULL COMMENT '流程引擎版本号',
  `APP_VERSION_`            int(11)       DEFAULT NULL COMMENT '自定义应用版本号',
  PRIMARY KEY (`ID_`),
  UNIQUE KEY `ACT_UNIQ_PROCDEF` (`KEY_`, `VERSION_`, `TENANT_ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='流程定义表';
````

### ACT_RU_DEADLETTER_JOB(act_ru_deadletter_job)(作业死亡信息表)

````sql
drop table if exists `act_ru_deadletter_job`;
CREATE TABLE `act_ru_deadletter_job`
(
  `ID_`                  varchar(64)  NOT NULL COMMENT '主键',
  `REV_`                 int(11)           DEFAULT NULL COMMENT '乐观锁版本号',
  `TYPE_`                varchar(255) NOT NULL COMMENT '类型',
  `EXCLUSIVE_`           tinyint(1)        DEFAULT NULL COMMENT '是否为排他任务',
  `EXECUTION_ID_`        varchar(64)       DEFAULT NULL COMMENT '执行实例 ID（ACT_RU_EXECUTION 表 ID_）',
  `PROCESS_INSTANCE_ID_` varchar(64)       DEFAULT NULL COMMENT '流程实例 ID（ACT_RU_EXECUTION 表 PROC_INST_ID_，PS：如果没有子节点，则此字段值为 ID_，否则值为父节点 ID_）',
  `PROC_DEF_ID_`         varchar(64)       DEFAULT NULL COMMENT '流程定义 ID（ACT_RE_PROCDEF 表 ID_）',
  `EXCEPTION_STACK_ID_`  varchar(64)       DEFAULT NULL COMMENT '异常信息 ID（ACT_GE_BYTEARRAY 表 ID）',
  `EXCEPTION_MSG_`       varchar(4000)     DEFAULT NULL COMMENT '异常信息',
  `DUEDATE_`             timestamp    NULL DEFAULT NULL COMMENT '到期时间',
  `REPEAT_`              varchar(255)      DEFAULT NULL COMMENT '重复配置',
  `HANDLER_TYPE_`        varchar(255)      DEFAULT NULL COMMENT '处理类型',
  `HANDLER_CFG_`         varchar(4000)     DEFAULT NULL COMMENT '处理器配置',
  `TENANT_ID_`           varchar(255)      DEFAULT '' COMMENT '租户 ID',
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
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='作业死亡信息表';
````

### ACT_RU_EVENT_SUBSCR(act_ru_event_subscr)(事件订阅表)

````sql
drop table if exists `act_ru_event_subscr`;
CREATE TABLE `act_ru_event_subscr`
(
  `ID_`            varchar(64)  NOT NULL COMMENT '主键',
  `REV_`           int(11)               DEFAULT NULL COMMENT '乐观锁版本号',
  `EVENT_TYPE_`    varchar(255) NOT NULL COMMENT '事件类型',
  `EVENT_NAME_`    varchar(255)          DEFAULT NULL COMMENT '事件名称',
  `EXECUTION_ID_`  varchar(64)           DEFAULT NULL COMMENT '执行实例 ID（ACT_RU_EXECUTION 表 ID_）',
  `PROC_INST_ID_`  varchar(64)           DEFAULT NULL COMMENT '流程实例 ID（ACT_RU_EXECUTION 表 PROC_INST_ID_，PS：如果没有子节点，则此字段值为 ID_，否则值为父节点 ID_）',
  `ACTIVITY_ID_`   varchar(64)           DEFAULT NULL COMMENT '活动节点 ID（对应流程文件 process 中的节点 id）',
  `CONFIGURATION_` varchar(255)          DEFAULT NULL COMMENT '配置信息',
  `CREATED_`       timestamp    NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
  `PROC_DEF_ID_`   varchar(64)           DEFAULT NULL COMMENT '流程定义 ID（ACT_RE_PROCDEF 表 ID_）',
  `TENANT_ID_`     varchar(255)          DEFAULT '' COMMENT '租户 ID',
  PRIMARY KEY (`ID_`),
  KEY `ACT_IDX_EVENT_SUBSCR_CONFIG_` (`CONFIGURATION_`),
  KEY `ACT_FK_EVENT_EXEC` (`EXECUTION_ID_`),
  CONSTRAINT `ACT_FK_EVENT_EXEC` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `act_ru_execution` (`ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='事件订阅表';
````

### ACT_RU_EXECUTION(act_ru_execution)（流程执行实例表）

核心，我的代办任务查询表

````sql
drop table if exists `act_ru_execution`;
CREATE TABLE `act_ru_execution`
(
  `ID_`                   varchar(64) NOT NULL COMMENT '主键',
  `REV_`                  int(11)          DEFAULT NULL COMMENT '乐观锁版本号',
  `PROC_INST_ID_`         varchar(64)      DEFAULT NULL COMMENT '流程实例ID，如果没有子节点，则此字段值为 ID_，否则值为父节点 ID_',
  `BUSINESS_KEY_`         varchar(255)     DEFAULT NULL COMMENT '业务 Key，用于将业务数据与流程实例关联',
  `PARENT_ID_`            varchar(64)      DEFAULT NULL COMMENT '父流程实例 ID（ACT_RU_EXECUTION 表 ID_）',
  `PROC_DEF_ID_`          varchar(64)      DEFAULT NULL COMMENT '流程定义 ID（ACT_RE_PROCDEF 表 ID_）',
  `SUPER_EXEC_`           varchar(64)      DEFAULT NULL COMMENT '上级执行实例 ID（ACT_RU_EXECUTION 表 ID_）',
  `ROOT_PROC_INST_ID_`    varchar(64)      DEFAULT NULL COMMENT '根流程实例ID（ACT_RU_EXECUTION 表 ID_）',
  `ACT_ID_`               varchar(255)     DEFAULT NULL COMMENT '表示流程运行到的节点',
  `IS_ACTIVE_`            tinyint(4)       DEFAULT NULL COMMENT '是否激活：1-激活；2挂起；',
  `IS_CONCURRENT_`        tinyint(4)       DEFAULT NULL COMMENT '是否并行：1-是；0-否；',
  `IS_SCOPE_`             tinyint(4)       DEFAULT NULL COMMENT '主实例字段为 1，子实例字段为 0',
  `IS_EVENT_SCOPE_`       tinyint(4)       DEFAULT NULL COMMENT '是否为事件作用域：1-是；0-否；',
  `IS_MI_ROOT_`           tinyint(4)       DEFAULT NULL COMMENT '是否是多实例的根流程：1-是；0-否；',
  `SUSPENSION_STATE_`     int(11)          DEFAULT NULL COMMENT '挂起状态：1-激活；2-挂起；',
  `CACHED_ENT_STATE_`     int(11)          DEFAULT NULL COMMENT '缓存结束状态：1-事件监听；2-人工任务；3-异步作业；',
  `TENANT_ID_`            varchar(255)     DEFAULT '' COMMENT '租户 ID',
  `NAME_`                 varchar(255)     DEFAULT NULL COMMENT '名称',
  `START_TIME_`           datetime         DEFAULT NULL COMMENT '开始时间',
  `START_USER_ID_`        varchar(255)     DEFAULT NULL COMMENT '启动实例用户 ID',
  `LOCK_TIME_`            timestamp   NULL DEFAULT NULL COMMENT '锁定时间',
  `IS_COUNT_ENABLED_`     tinyint(4)       DEFAULT NULL COMMENT '是否能够计数：1-是；0-否；',
  `EVT_SUBSCR_COUNT_`     int(11)          DEFAULT NULL COMMENT '事件订阅数量',
  `TASK_COUNT_`           int(11)          DEFAULT NULL COMMENT '任务数量',
  `JOB_COUNT_`            int(11)          DEFAULT NULL COMMENT '定时任务数量',
  `TIMER_JOB_COUNT_`      int(11)          DEFAULT NULL COMMENT '定时器工作数量',
  `SUSP_JOB_COUNT_`       int(11)          DEFAULT NULL COMMENT '挂起工作数量',
  `DEADLETTER_JOB_COUNT_` int(11)          DEFAULT NULL COMMENT '死信工作数量',
  `VAR_COUNT_`            int(11)          DEFAULT NULL COMMENT '变量数量',
  `ID_LINK_COUNT_`        int(11)          DEFAULT NULL COMMENT '标识链接数量',
  `APP_VERSION_`          int(11)          DEFAULT NULL COMMENT '应用版本',
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
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='流程执行实例表';
````

### ACT_RU_IDENTITYLINK(act_ru_identitylink)（身份链接表）

主要存储当前节点参与者的信息,任务参与者数据表。

````sql
drop table if exists `act_ru_identitylink`;
CREATE TABLE `act_ru_identitylink`
(
  `ID_`           varchar(64) NOT NULL COMMENT '主键',
  `REV_`          int(11)      DEFAULT NULL COMMENT '乐观锁版本号',
  `GROUP_ID_`     varchar(255) DEFAULT NULL COMMENT '组 ID',
  `TYPE_`         varchar(255) DEFAULT NULL COMMENT '类型：assignee-受让人；candidate-候选人；owner-所有者；starter-起动器；participant-参与者；',
  `USER_ID_`      varchar(255) DEFAULT NULL COMMENT '用户 ID',
  `TASK_ID_`      varchar(64)  DEFAULT NULL COMMENT '任务 ID',
  `PROC_INST_ID_` varchar(64)  DEFAULT NULL COMMENT '流程实例 ID',
  `PROC_DEF_ID_`  varchar(64)  DEFAULT NULL COMMENT '流程定义 ID',
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
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='身份链接表';
````

#### ACT_RU_INTEGRATION(act_ru_integration)(集成综合表)

````sql
drop table if exists `act_ru_integration`;
CREATE TABLE `act_ru_integration`
(
  `ID_`                  varchar(64) NOT NULL COMMENT '主键',
  `EXECUTION_ID_`        varchar(64)          DEFAULT NULL COMMENT '执行实例 ID（ACT_RU_EXECUTION 表 ID_）',
  `PROCESS_INSTANCE_ID_` varchar(64)          DEFAULT NULL COMMENT '流程实例 ID（ACT_RU_EXECUTION 表 PROC_INST_ID_，PS：如果没有子节点，则此字段值为 ID_，否则值为父节点 ID_）',
  `PROC_DEF_ID_`         varchar(64)          DEFAULT NULL COMMENT '流程定义 ID（ACT_RE_PROCDEF 表 ID_）',
  `FLOW_NODE_ID_`        varchar(64)          DEFAULT NULL COMMENT '流程节点 ID',
  `CREATED_DATE_`        timestamp   NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '创建时间，并在更新时自动更新为当前时间',
  PRIMARY KEY (`ID_`),
  KEY `ACT_FK_INT_EXECUTION` (`EXECUTION_ID_`),
  KEY `ACT_FK_INT_PROC_INST` (`PROCESS_INSTANCE_ID_`),
  KEY `ACT_FK_INT_PROC_DEF` (`PROC_DEF_ID_`),
  CONSTRAINT `ACT_FK_INT_EXECUTION` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE CASCADE,
  CONSTRAINT `ACT_FK_INT_PROC_DEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `act_re_procdef` (`ID_`),
  CONSTRAINT `ACT_FK_INT_PROC_INST` FOREIGN KEY (`PROCESS_INSTANCE_ID_`) REFERENCES `act_ru_execution` (`ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='集成综合表';
````

### ACT_RU_JOB(act_ru_job)（定时任务表）

运行时定时任务数据表

````sql
drop table if exists `act_ru_job`;
CREATE TABLE `act_ru_job`
(
  `ID_`                  varchar(64)  NOT NULL COMMENT '主键',
  `REV_`                 int(11)           DEFAULT NULL COMMENT '乐观锁版本号',
  `TYPE_`                varchar(255) NOT NULL COMMENT '类型',
  `LOCK_EXP_TIME_`       timestamp    NULL DEFAULT NULL COMMENT '任务锁定过期时间',
  `LOCK_OWNER_`          varchar(255)      DEFAULT NULL COMMENT '任务锁定人',
  `EXCLUSIVE_`           tinyint(1)        DEFAULT NULL COMMENT '是否为排他任务',
  `EXECUTION_ID_`        varchar(64)       DEFAULT NULL COMMENT '执行实例 ID（ACT_RU_EXECUTION 表 ID_）',
  `PROCESS_INSTANCE_ID_` varchar(64)       DEFAULT NULL COMMENT '流程实例 ID（ACT_RU_EXECUTION 表 PROC_INST_ID_，PS：如果没有子节点，则此字段值为 ID_，否则值为父节点 ID_）',
  `PROC_DEF_ID_`         varchar(64)       DEFAULT NULL COMMENT '流程定义 ID（ACT_RE_PROCDEF 表 ID_）',
  `RETRIES_`             int(11)           DEFAULT NULL COMMENT '重试次数',
  `EXCEPTION_STACK_ID_`  varchar(64)       DEFAULT NULL COMMENT '异常信息 ID（ACT_GE_BYTEARRAY 表的ID_）',
  `EXCEPTION_MSG_`       varchar(4000)     DEFAULT NULL COMMENT '异常信息',
  `DUEDATE_`             timestamp    NULL DEFAULT NULL COMMENT '任务的到期时间',
  `REPEAT_`              varchar(255)      DEFAULT NULL COMMENT '重复配置',
  `HANDLER_TYPE_`        varchar(255)      DEFAULT NULL COMMENT '处理类型',
  `HANDLER_CFG_`         varchar(4000)     DEFAULT NULL COMMENT '处理配置',
  `TENANT_ID_`           varchar(255)      DEFAULT '' COMMENT '租户 ID',
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
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='定时任务表';
````

### ACT_RU_SUSPENDED_JOB(act_ru_suspended_job)(作业暂停表)

````sql
drop table if exists `act_ru_suspended_job`;
CREATE TABLE `act_ru_suspended_job`
(
  `ID_`                  varchar(64)  NOT NULL COMMENT '主键',
  `REV_`                 int(11)           DEFAULT NULL COMMENT '乐观锁版本号',
  `TYPE_`                varchar(255) NOT NULL COMMENT '类型',
  `EXCLUSIVE_`           tinyint(1)        DEFAULT NULL COMMENT '是否为排他任务',
  `EXECUTION_ID_`        varchar(64)       DEFAULT NULL COMMENT '执行实例 ID（ACT_RU_EXECUTION 表 ID_）',
  `PROCESS_INSTANCE_ID_` varchar(64)       DEFAULT NULL COMMENT '流程实例 ID（ACT_RU_EXECUTION 表 PROC_INST_ID_，PS：如果没有子节点，则此字段值为 ID_，否则值为父节点 ID_）',
  `PROC_DEF_ID_`         varchar(64)       DEFAULT NULL COMMENT '流程定义 ID（ACT_RE_PROCDEF 表 ID_）',
  `RETRIES_`             int(11)           DEFAULT NULL COMMENT '重试次数',
  `EXCEPTION_STACK_ID_`  varchar(64)       DEFAULT NULL COMMENT '异常信息 ID，ACT_GE_BYTEARRAY 表的ID_',
  `EXCEPTION_MSG_`       varchar(4000)     DEFAULT NULL COMMENT '异常消息',
  `DUEDATE_`             timestamp    NULL DEFAULT NULL COMMENT '任务的到期时间',
  `REPEAT_`              varchar(255)      DEFAULT NULL COMMENT '重复配置',
  `HANDLER_TYPE_`        varchar(255)      DEFAULT NULL COMMENT '处理类型',
  `HANDLER_CFG_`         varchar(4000)     DEFAULT NULL COMMENT '处理配置',
  `TENANT_ID_`           varchar(255)      DEFAULT '' COMMENT '租户 ID',
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
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='作业暂停表';
````

### ACT_RU_TASK(act_ru_task)(任务数据表)

（执行中实时任务）代办任务查询表

````sql
drop table if exists `act_ru_task`;
CREATE TABLE `act_ru_task`
(
  `ID_`               varchar(64) NOT NULL COMMENT '主键',
  `REV_`              int(11)          DEFAULT NULL COMMENT '乐观锁版本号',
  `EXECUTION_ID_`     varchar(64)      DEFAULT NULL COMMENT '执行实例 ID（ACT_RU_EXECUTION 表 ID_）',
  `PROC_INST_ID_`     varchar(64)      DEFAULT NULL COMMENT '流程实例 ID（ACT_RU_EXECUTION 表 PROC_INST_ID_，PS：如果没有子节点，则此字段值为 ID_，否则值为父节点 ID_）',
  `PROC_DEF_ID_`      varchar(64)      DEFAULT NULL COMMENT '流程定义 ID（ACT_RE_PROCDEF 表 ID_）',
  `NAME_`             varchar(255)     DEFAULT NULL COMMENT '任务名称',
  `BUSINESS_KEY_`     varchar(255)     DEFAULT NULL COMMENT '业务 key',
  `PARENT_TASK_ID_`   varchar(64)      DEFAULT NULL COMMENT '父任务 ID',
  `DESCRIPTION_`      varchar(4000)    DEFAULT NULL COMMENT '任务描述',
  `TASK_DEF_KEY_`     varchar(255)     DEFAULT NULL COMMENT '任务定义 Key（对应流程文件 process 中的节点 id）',
  `OWNER_`            varchar(255)     DEFAULT NULL COMMENT '任务所属人（存在委托行为才会有值）',
  `ASSIGNEE_`         varchar(255)     DEFAULT NULL COMMENT '任务的受理人',
  `DELEGATION_`       varchar(64)      DEFAULT NULL COMMENT '委托状态：PENDING，RESOLVED，如无委托则为空',
  `PRIORITY_`         int(11)          DEFAULT NULL COMMENT '优先级（默认 50）',
  `CREATE_TIME_`      timestamp   NULL DEFAULT NULL COMMENT '创建时间',
  `DUE_DATE_`         datetime         DEFAULT NULL COMMENT '截止日期',
  `CATEGORY_`         varchar(255)     DEFAULT NULL COMMENT '任务的类别',
  `SUSPENSION_STATE_` int(11)          DEFAULT NULL COMMENT '挂起状态：1-激活；2-挂起；',
  `TENANT_ID_`        varchar(255)     DEFAULT '' COMMENT '租户 ID',
  `FORM_KEY_`         varchar(255)     DEFAULT NULL COMMENT '关联的表单键',
  `CLAIM_TIME_`       datetime         DEFAULT NULL COMMENT '任务的认领时间',
  `APP_VERSION_`      int(11)          DEFAULT NULL COMMENT '应用版本',
  PRIMARY KEY (`ID_`),
  KEY `ACT_IDX_TASK_CREATE` (`CREATE_TIME_`),
  KEY `ACT_FK_TASK_EXE` (`EXECUTION_ID_`),
  KEY `ACT_FK_TASK_PROCINST` (`PROC_INST_ID_`),
  KEY `ACT_FK_TASK_PROCDEF` (`PROC_DEF_ID_`),
  CONSTRAINT `ACT_FK_TASK_EXE` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `act_ru_execution` (`ID_`),
  CONSTRAINT `ACT_FK_TASK_PROCDEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `act_re_procdef` (`ID_`),
  CONSTRAINT `ACT_FK_TASK_PROCINST` FOREIGN KEY (`PROC_INST_ID_`) REFERENCES `act_ru_execution` (`ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='任务数据表';
````

### ACT_RU_TIMER_JOB(act_ru_timer_job)(定时器作业表)

````sql
drop table if exists `act_ru_timer_job`;
CREATE TABLE `act_ru_timer_job`
(
  `ID_`                  varchar(64)  NOT NULL COMMENT '主键',
  `REV_`                 int(11)           DEFAULT NULL COMMENT '乐观锁版本号',
  `TYPE_`                varchar(255) NOT NULL COMMENT '类型',
  `LOCK_EXP_TIME_`       timestamp    NULL DEFAULT NULL COMMENT '任务锁定过期时间',
  `LOCK_OWNER_`          varchar(255)      DEFAULT NULL COMMENT '任务锁定人',
  `EXCLUSIVE_`           tinyint(1)        DEFAULT NULL COMMENT '是否为排他任务',
  `EXECUTION_ID_`        varchar(64)       DEFAULT NULL COMMENT '执行实例 ID（ACT_RU_EXECUTION 表 ID_）',
  `PROCESS_INSTANCE_ID_` varchar(64)       DEFAULT NULL COMMENT '流程实例 ID（ACT_RU_EXECUTION 表 PROC_INST_ID_，PS：如果没有子节点，则此字段值为 ID_，否则值为父节点 ID_）',
  `PROC_DEF_ID_`         varchar(64)       DEFAULT NULL COMMENT '流程定义 ID（ACT_RE_PROCDEF 表 ID_）',
  `RETRIES_`             int(11)           DEFAULT NULL COMMENT '重试次数',
  `EXCEPTION_STACK_ID_`  varchar(64)       DEFAULT NULL COMMENT '异常 ID（ACT_GE_BYTEARRAY 表 ID_）',
  `EXCEPTION_MSG_`       varchar(4000)     DEFAULT NULL COMMENT '异常消息',
  `DUEDATE_`             timestamp    NULL DEFAULT NULL COMMENT '任务的到期时间',
  `REPEAT_`              varchar(255)      DEFAULT NULL COMMENT '重复配置',
  `HANDLER_TYPE_`        varchar(255)      DEFAULT NULL COMMENT '处理类型',
  `HANDLER_CFG_`         varchar(4000)     DEFAULT NULL COMMENT '处理配置',
  `TENANT_ID_`           varchar(255)      DEFAULT '' COMMENT '租户 ID',
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
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='定时器作业表';
````

#### 1.2.24 表名:ACT_RU_VARIABLE(act_ru_variable)(运行时流程变量数据表)

````sql
drop table if exists `act_ru_variable`;
CREATE TABLE `act_ru_variable`
(
  `ID_`           varchar(64)  NOT NULL COMMENT '主键',
  `REV_`          int(11)       DEFAULT NULL COMMENT '乐观锁版本号',
  `TYPE_`         varchar(255) NOT NULL COMMENT '类型',
  `NAME_`         varchar(255) NOT NULL COMMENT '变量名称',
  `EXECUTION_ID_` varchar(64)   DEFAULT NULL COMMENT '执行实例 ID（ACT_RU_EXECUTION 表 ID_）',
  `PROC_INST_ID_` varchar(64)   DEFAULT NULL COMMENT '流程实例 ID（ACT_RU_EXECUTION 表 PROC_INST_ID_，PS：如果没有子节点，则此字段值为 ID_，否则值为父节点 ID_）',
  `TASK_ID_`      varchar(64)   DEFAULT NULL COMMENT '任务 ID（ACT_RU_TASK 表 ID_，如果流程结束，该表数据会清空，可以在 ACT_HI_TASKINST 表中找到历史数据）',
  `BYTEARRAY_ID_` varchar(64)   DEFAULT NULL COMMENT 'ACT_GE_BYTEARRAY 表 ID',
  `DOUBLE_`       double        DEFAULT NULL COMMENT '变量的 double 类型值',
  `LONG_`         bigint(20)    DEFAULT NULL COMMENT '变量的 long 类型值',
  `TEXT_`         varchar(4000) DEFAULT NULL COMMENT '变量的文本值，如此处存储 JPA 持久化对象时，值存 ClassName',
  `TEXT2_`        varchar(4000) DEFAULT NULL COMMENT '变量的附加文本值，此处存储的是 JPA 持久化对象时，此值存对象 ID',
  PRIMARY KEY (`ID_`),
  KEY `ACT_IDX_VARIABLE_TASK_ID` (`TASK_ID_`),
  KEY `ACT_FK_VAR_EXE` (`EXECUTION_ID_`),
  KEY `ACT_FK_VAR_PROCINST` (`PROC_INST_ID_`),
  KEY `ACT_FK_VAR_BYTEARRAY` (`BYTEARRAY_ID_`),
  CONSTRAINT `ACT_FK_VAR_BYTEARRAY` FOREIGN KEY (`BYTEARRAY_ID_`) REFERENCES `act_ge_bytearray` (`ID_`),
  CONSTRAINT `ACT_FK_VAR_EXE` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `act_ru_execution` (`ID_`),
  CONSTRAINT `ACT_FK_VAR_PROCINST` FOREIGN KEY (`PROC_INST_ID_`) REFERENCES `act_ru_execution` (`ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='运行时流程变量数据表';
````
