-- 设置外键约束检查关闭   首先执行
SET FOREIGN_KEY_CHECKS = 0;
-- ================================================================================================================================
-- 流程事件日志记录表
DROP TABLE IF EXISTS `act_evt_log`;
CREATE TABLE `act_evt_log`
(
    `log_nr_`       bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `type_`         varchar(64)         DEFAULT NULL COMMENT '事件类型',
    `proc_def_id_`  varchar(64)         DEFAULT NULL COMMENT '流程定义 id ( act_re_procdef 表 id_ ) ',
    `proc_inst_id_` varchar(64)         DEFAULT NULL COMMENT '流程实例 id ( act_ru_execution 表 proc_inst_id_, ps: 如果没有子节点, 则此字段值为 id_, 否则值为父节点 id_ ) ',
    `execution_id_` varchar(64)         DEFAULT NULL COMMENT '执行实例 id ( act_ru_execution 表 id_ ) ',
    `task_id_`      varchar(64)         DEFAULT NULL COMMENT '任务 id ( act_ru_task 表 id_, 如果流程结束, 该表数据会清空, 可以在 act_hi_taskinst 表中找到历史数据 ) ',
    `time_stamp_`   timestamp  NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '事件发生的时间戳',
    `user_id_`      varchar(255)        DEFAULT NULL COMMENT '执行事件的用户 id',
    `data_`         longblob            DEFAULT NULL COMMENT '事件数据',
    `lock_owner_`   varchar(255)        DEFAULT NULL COMMENT '当前锁定该记录的用户或进程',
    `lock_time_`    timestamp  NULL     DEFAULT NULL COMMENT '锁定时间',
    `is_processed_` tinyint(4)          DEFAULT 0 COMMENT '是否处理: 1-已处理; 0-未处理;',
    PRIMARY KEY (`log_nr_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='流程事件日志记录表';
-- ================================================================================================================================
-- 二进制数据表, 存放资源文件 (  图片, xml  ) 
DROP TABLE IF EXISTS `act_ge_bytearray`;
CREATE TABLE `act_ge_bytearray`
(
    `id_`            varchar(64) NOT NULL COMMENT '主键',
    `rev_`           int(11)      DEFAULT NULL COMMENT '乐观锁版本号',
    `name_`          varchar(255) DEFAULT NULL COMMENT '部署的文件名称. mail.bpmn, mail.png, mail.bpmn20.xml',
    `deployment_id_` varchar(64)  DEFAULT NULL COMMENT '部署 id ( act_re_deployment 表 id_ ) ',
    `bytes_`         longblob     DEFAULT NULL COMMENT '文本字节流',
    `generated_`     tinyint(4)   DEFAULT NULL COMMENT '是否是引擎生成: 0-用户生成; 1-activiti 生成;',
    PRIMARY KEY (`id_`),
    KEY `act_fk_bytearr_depl` (`deployment_id_`),
    CONSTRAINT `act_fk_bytearr_depl` FOREIGN KEY (`deployment_id_`) REFERENCES `act_re_deployment` (`id_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='二进制数据表, 存放资源文件 (  图片, xml  ) ';
-- ================================================================================================================================
-- 系统相关属性表
DROP TABLE IF EXISTS `act_ge_property`;
CREATE TABLE `act_ge_property`
(
    `name_`  varchar(64) NOT NULL COMMENT '主键, 属性名称. schema.version, schema.history, next.dbid',
    `value_` varchar(300) DEFAULT NULL COMMENT '属性值. 5.*, create ( 5.* )',
    `rev_`   int(11)      DEFAULT NULL COMMENT '乐观锁版本号',
    PRIMARY KEY (`name_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='系统相关属性表';
-- ================================================================================================================================
-- 历史节点表
DROP TABLE IF EXISTS `act_hi_actinst`;
CREATE TABLE `act_hi_actinst`
(
    `id_`                varchar(64)  NOT NULL COMMENT '主键',
    `proc_def_id_`       varchar(64)  NOT NULL COMMENT '流程定义 id ( act_re_procdef 表 id_ ) ',
    `proc_inst_id_`      varchar(64)  NOT NULL COMMENT '流程实例 id ( act_ru_execution 表 proc_inst_id_, ps: 如果没有子节点, 则此字段值为 id_, 否则值为父节点 id_ ) ',
    `execution_id_`      varchar(64)  NOT NULL COMMENT '执行实例 id ( act_ru_execution 表 id_ ) ',
    `act_id_`            varchar(255) NOT NULL COMMENT '活动节点 id ( 对应流程文件 process 中的节点 id ) ',
    `task_id_`           varchar(64)   DEFAULT NULL COMMENT '任务 id, 其他自动完成的节点类型, 该值为空 ( act_ru_task 表 id_, 如果流程结束, 该表数据会清空, 可以在 act_hi_taskinst 表中找到历史数据 ) ',
    `call_proc_inst_id_` varchar(64)   DEFAULT NULL COMMENT '调用流程实例 id',
    `act_name_`          varchar(255)  DEFAULT NULL COMMENT '活动名称, 流程文件节点定义的名称',
    `act_type_`          varchar(255) NOT NULL COMMENT '活动类型, 如startEvent, userTask',
    `assignee_`          varchar(255)  DEFAULT NULL COMMENT '任务办理人',
    `start_time_`        datetime     NOT NULL COMMENT '开始时间',
    `end_time_`          datetime      DEFAULT NULL COMMENT '结束时间',
    `duration_`          bigint(20)    DEFAULT NULL COMMENT '总耗时 ( 毫秒 ) ',
    `delete_reason_`     varchar(4000) DEFAULT NULL COMMENT '删除原因',
    `tenant_id_`         varchar(255)  DEFAULT '' COMMENT '租户 id',
    PRIMARY KEY (`id_`),
    KEY `act_idx_hi_act_inst_start` (`start_time_`),
    KEY `act_idx_hi_act_inst_end` (`end_time_`),
    KEY `act_idx_hi_act_inst_procinst` (`proc_inst_id_`, `act_id_`),
    KEY `act_idx_hi_act_inst_exec` (`execution_id_`, `act_id_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='历史节点表';
-- ================================================================================================================================
-- 历史附件表
DROP TABLE IF EXISTS `act_hi_attachment`;
CREATE TABLE `act_hi_attachment`
(
    `id_`           varchar(64) NOT NULL COMMENT '主键',
    `rev_`          int(11)       DEFAULT NULL COMMENT '乐观锁版本号',
    `user_id_`      varchar(255)  DEFAULT NULL COMMENT '用户 id',
    `name_`         varchar(255)  DEFAULT NULL COMMENT '名称',
    `description_`  varchar(4000) DEFAULT NULL COMMENT '描述',
    `type_`         varchar(255)  DEFAULT NULL COMMENT '类型',
    `task_id_`      varchar(64)   DEFAULT NULL COMMENT '任务 id ( act_ru_task 表 id_, 如果流程结束, 该表数据会清空, 可以在 act_hi_taskinst 表中找到历史数据 ) ',
    `proc_inst_id_` varchar(64)   DEFAULT NULL COMMENT '流程实例 id ( act_ru_execution 表 proc_inst_id_, ps: 如果没有子节点, 则此字段值为 id_, 否则值为父节点 id_ ) ',
    `url_`          varchar(4000) DEFAULT NULL COMMENT '附件地址',
    `content_id_`   varchar(64)   DEFAULT NULL COMMENT '内容 id ( act_ge_bytearray 表 id_ ) ',
    `time_`         datetime      DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='历史附件表';
-- ================================================================================================================================
-- 历史审批意见表
DROP TABLE IF EXISTS `act_hi_comment`;
CREATE TABLE `act_hi_comment`
(
    `id_`           varchar(64) NOT NULL COMMENT '主键',
    `type_`         varchar(255)  DEFAULT NULL COMMENT '类型: event-事件; comment-意见;',
    `time_`         datetime    NOT NULL COMMENT '创建时间',
    `user_id_`      varchar(255)  DEFAULT NULL COMMENT '用户 id',
    `task_id_`      varchar(64)   DEFAULT NULL COMMENT '任务 id ( act_ru_task 表 id_, 如果流程结束, 该表数据会清空, 可以在 act_hi_taskinst 表中找到历史数据 ) ',
    `proc_inst_id_` varchar(64)   DEFAULT NULL COMMENT '流程实例 id ( act_ru_execution 表 proc_inst_id_, ps: 如果没有子节点, 则此字段值为 id_, 否则值为父节点 id_ ) ',
    `action_`       varchar(255)  DEFAULT NULL COMMENT '行为类型: AddUserLink, DeleteUserLink, AddGroupLink, DeleteGroupLink, AddComment, AddAttachment, DeleteAttachment',
    `message_`      varchar(4000) DEFAULT NULL COMMENT '审批意见',
    `full_msg_`     longblob      DEFAULT NULL COMMENT '全部消息',
    PRIMARY KEY (`id_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='历史审批意见表';
-- ================================================================================================================================
-- 历史详情表
DROP TABLE IF EXISTS `act_hi_detail`;
CREATE TABLE `act_hi_detail`
(
    `id_`           varchar(64)  NOT NULL COMMENT '主键',
    `type_`         varchar(255) NOT NULL COMMENT '类型: FormProperty-表单; VariableUpdate-参数;',
    `proc_inst_id_` varchar(64)   DEFAULT NULL COMMENT '流程实例 id ( act_ru_execution 表 proc_inst_id_, ps: 如果没有子节点, 则此字段值为 id_, 否则值为父节点 id_ ) ',
    `execution_id_` varchar(64)   DEFAULT NULL COMMENT '执行实例 id ( act_ru_execution 表 id_ ) ',
    `task_id_`      varchar(64)   DEFAULT NULL COMMENT '任务 id ( act_ru_task 表 id_, 如果流程结束, 该表数据会清空, 可以在 act_hi_taskinst 表中找到历史数据 ) ',
    `act_inst_id_`  varchar(64)   DEFAULT NULL COMMENT '活动实例 id',
    `name_`         varchar(255) NOT NULL COMMENT '变量名称',
    `var_type_`     varchar(255)  DEFAULT NULL COMMENT '变量类型: jpa-entity, boolean, bytes, serializable ( 可序列化 ) , 自定义type ( 根据你自身配置 ) ,  CustomVariableType, date, double, integer, long, null, short, string',
    `rev_`          int(11)       DEFAULT NULL COMMENT '乐观锁版本号',
    `time_`         datetime     NOT NULL COMMENT '创建时间',
    `bytearray_id_` varchar(64)   DEFAULT NULL COMMENT 'act_ge_bytearray 表 id_',
    `double_`       double        DEFAULT NULL COMMENT '变量的 double 类型值',
    `long_`         bigint(20)    DEFAULT NULL COMMENT '变量的 long 类型值',
    `text_`         varchar(4000) DEFAULT NULL COMMENT '变量的文本值, 如此处存储 JPA 持久化对象时, 值存 ClassName',
    `text2_`        varchar(4000) DEFAULT NULL COMMENT '变量的附加文本值, 此处存储的是 JPA 持久化对象时, 此值存对象 ID',
    PRIMARY KEY (`id_`),
    KEY `act_idx_hi_detail_proc_inst` (`proc_inst_id_`),
    KEY `act_idx_hi_detail_act_inst` (`act_inst_id_`),
    KEY `act_idx_hi_detail_time` (`time_`),
    KEY `act_idx_hi_detail_name` (`name_`),
    KEY `act_idx_hi_detail_task_id` (`task_id_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='历史详情表';
-- ================================================================================================================================
-- 历史身份链接表
DROP TABLE IF EXISTS `act_hi_identitylink`;
CREATE TABLE `act_hi_identitylink`
(
    `id_`           varchar(64) NOT NULL COMMENT '主键',
    `group_id_`     varchar(255) DEFAULT NULL COMMENT '组 id',
    `type_`         varchar(255) DEFAULT NULL COMMENT '组类型: assignee-受让人; candidate-候选人; owner-所有者; starter-起动器; participant-参与者;',
    `user_id_`      varchar(255) DEFAULT NULL COMMENT '用户 id',
    `task_id_`      varchar(64)  DEFAULT NULL COMMENT '任务 id ( act_ru_task 表 id_, 如果流程结束, 该表数据会清空, 可以在 act_hi_taskinst 表中找到历史数据 ) ',
    `proc_inst_id_` varchar(64)  DEFAULT NULL COMMENT '流程实例 id ( act_ru_execution 表 proc_inst_id_, ps: 如果没有子节点, 则此字段值为 id_, 否则值为父节点 id_ ) ',
    PRIMARY KEY (`id_`),
    KEY `act_idx_hi_ident_lnk_user` (`user_id_`),
    KEY `act_idx_hi_ident_lnk_task` (`task_id_`),
    KEY `act_idx_hi_ident_lnk_procinst` (`proc_inst_id_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='历史身份链接表';
-- ================================================================================================================================
-- 历史流程实例表
DROP TABLE IF EXISTS `act_hi_procinst`;
CREATE TABLE `act_hi_procinst`
(
    `id_`                        varchar(64) NOT NULL COMMENT '主键',
    `proc_inst_id_`              varchar(64) NOT NULL COMMENT '流程实例 id ( act_ru_execution 表 proc_inst_id_, ps: 如果没有子节点, 则此字段值为 id_, 否则值为父节点 id_ ) ',
    `business_key_`              varchar(255)  DEFAULT NULL COMMENT '业务系统 Key ( 流程定义 key:表单 ID ) ',
    `proc_def_id_`               varchar(64) NOT NULL COMMENT '流程定义 id ( act_re_procdef 表 id_ ) ',
    `start_time_`                datetime    NOT NULL COMMENT '开始时间',
    `end_time_`                  datetime      DEFAULT NULL COMMENT '结束时间',
    `duration_`                  bigint(20)    DEFAULT NULL COMMENT '总耗时 ( 毫秒 ) ',
    `start_user_id_`             varchar(255)  DEFAULT NULL COMMENT '开始用户 id',
    `start_act_id_`              varchar(255)  DEFAULT NULL COMMENT '开始节点 id ( 对应流程文件 process 中的节点 id ) ',
    `end_act_id_`                varchar(255)  DEFAULT NULL COMMENT '结束节点 id ( 对应流程文件 process 中的节点 id ) ',
    `super_process_instance_id_` varchar(64)   DEFAULT NULL COMMENT '上级流程实例 id',
    `delete_reason_`             varchar(4000) DEFAULT NULL COMMENT '删除原因',
    `tenant_id_`                 varchar(255)  DEFAULT '' COMMENT '租户 id',
    `name_`                      varchar(255)  DEFAULT NULL COMMENT '流程实例名称',
    PRIMARY KEY (`id_`),
    UNIQUE KEY `proc_inst_id_` (`proc_inst_id_`),
    KEY `act_idx_hi_pro_inst_end` (`end_time_`),
    KEY `act_idx_hi_pro_i_buskey` (`business_key_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='历史流程实例表';
-- ================================================================================================================================
-- 历史任务实例表
DROP TABLE IF EXISTS `act_hi_taskinst`;
CREATE TABLE `act_hi_taskinst`
(
    `id_`             varchar(64) NOT NULL COMMENT '主键',
    `proc_def_id_`    varchar(64)   DEFAULT NULL COMMENT '流程定义 id ( act_re_procdef 表 id_ ) ',
    `task_def_key_`   varchar(255)  DEFAULT NULL COMMENT '任务定义 key ( 对应流程文件 process 中的节点 id ) ',
    `proc_inst_id_`   varchar(64)   DEFAULT NULL COMMENT '流程实例 id ( act_ru_execution 表 proc_inst_id_, ps: 如果没有子节点, 则此字段值为 id_, 否则值为父节点 id_ ) ',
    `execution_id_`   varchar(64)   DEFAULT NULL COMMENT '执行实例 id ( act_ru_execution 表 id_ ) ',
    `name_`           varchar(255)  DEFAULT NULL COMMENT '任务名称',
    `parent_task_id_` varchar(64)   DEFAULT NULL COMMENT '父任务 id',
    `description_`    varchar(4000) DEFAULT NULL COMMENT '任务描述',
    `owner_`          varchar(255)  DEFAULT NULL COMMENT '任务的拥有者 ( 默认为空, 只有在委托时才有值 ) ',
    `assignee_`       varchar(255)  DEFAULT NULL COMMENT '任务办理人',
    `start_time_`     datetime    NOT NULL COMMENT '开始时间',
    `claim_time_`     datetime      DEFAULT NULL COMMENT '任务认领时间',
    `end_time_`       datetime      DEFAULT NULL COMMENT '结束时间',
    `duration_`       bigint(20)    DEFAULT NULL COMMENT '总耗时 ( 毫秒 ) ',
    `delete_reason_`  varchar(4000) DEFAULT NULL COMMENT '删除原因',
    `priority_`       int(11)       DEFAULT NULL COMMENT '优先级 ( 默认 50 ) ',
    `due_date_`       datetime      DEFAULT NULL COMMENT '应完成时间, 表明任务应在多长时间内完成',
    `form_key_`       varchar(255)  DEFAULT NULL COMMENT '任务的表单键, 节点定义的 form_key 属性',
    `category_`       varchar(255)  DEFAULT NULL COMMENT '任务类型',
    `tenant_id_`      varchar(255)  DEFAULT '' COMMENT '租户 id',
    PRIMARY KEY (`id_`),
    KEY `act_idx_hi_task_inst_procinst` (`proc_inst_id_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='历史任务实例表';
-- ================================================================================================================================
-- 历史变量表
DROP TABLE IF EXISTS `act_hi_varinst`;
CREATE TABLE `act_hi_varinst`
(
    `id_`                varchar(64)  NOT NULL COMMENT '主键',
    `proc_inst_id_`      varchar(64)   DEFAULT NULL COMMENT '流程实例 id ( act_ru_execution 表 proc_inst_id_, ps: 如果没有子节点, 则此字段值为 id_, 否则值为父节点 id_ ) ',
    `execution_id_`      varchar(64)   DEFAULT NULL COMMENT '执行实例 id ( act_ru_execution 表 id_ ) ',
    `task_id_`           varchar(64)   DEFAULT NULL COMMENT '任务 id ( act_ru_task 表 id_, 如果流程结束, 该表数据会清空, 可以在 act_hi_taskinst 表中找到历史数据 ) ',
    `name_`              varchar(255) NOT NULL COMMENT '变量名称',
    `var_type_`          varchar(100)  DEFAULT NULL COMMENT '变量类型: jpa-entity, boolean, bytes, serializable ( 可序列化 ) , 自定义type ( 根据你自身配置 ) ,  CustomVariableType, date, double, integer, long, null, short, string',
    `rev_`               int(11)       DEFAULT NULL COMMENT '乐观锁版本号',
    `bytearray_id_`      varchar(64)   DEFAULT NULL COMMENT 'act_ge_bytearray 表 id_',
    `double_`            double        DEFAULT NULL COMMENT '变量的 double 类型值',
    `long_`              bigint(20)    DEFAULT NULL COMMENT '变量的 long 类型值',
    `text_`              varchar(4000) DEFAULT NULL COMMENT '变量的文本值, 如此处存储 JPA 持久化对象时, 值存 ClassName',
    `text2_`             varchar(4000) DEFAULT NULL COMMENT '变量的附加文本值, 此处存储的是 JPA 持久化对象时, 此值存对象 ID',
    `create_time_`       datetime      DEFAULT NULL COMMENT '创建时间',
    `last_updated_time_` datetime      DEFAULT NULL COMMENT '最后更新时间',
    PRIMARY KEY (`id_`),
    KEY `act_idx_hi_procvar_proc_inst` (`proc_inst_id_`),
    KEY `act_idx_hi_procvar_name_type` (`name_`, `var_type_`),
    KEY `act_idx_hi_procvar_task_id` (`task_id_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='历史变量表';
-- ================================================================================================================================
-- 流程定义信息表
DROP TABLE IF EXISTS `act_procdef_info`;
CREATE TABLE `act_procdef_info`
(
    `id_`           varchar(64) NOT NULL COMMENT '主键',
    `proc_def_id_`  varchar(64) NOT NULL COMMENT 'act_re_procdef 表 id',
    `rev_`          int(11)     DEFAULT NULL COMMENT '乐观锁版本号',
    `info_json_id_` varchar(64) DEFAULT NULL COMMENT 'act_ge_bytearray 表 id',
    PRIMARY KEY (`id_`),
    UNIQUE KEY `act_uniq_info_procdef` (`proc_def_id_`),
    KEY `act_idx_info_procdef` (`proc_def_id_`),
    KEY `act_fk_info_json_ba` (`info_json_id_`),
    CONSTRAINT `act_fk_info_json_ba` FOREIGN KEY (`info_json_id_`) REFERENCES `act_ge_bytearray` (`id_`),
    CONSTRAINT `act_fk_info_procdef` FOREIGN KEY (`proc_def_id_`) REFERENCES `act_re_procdef` (`id_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='流程定义信息表';
-- ================================================================================================================================
-- 流程部署表
DROP TABLE IF EXISTS `act_re_deployment`;
CREATE TABLE `act_re_deployment`
(
    `id_`                      varchar(64) NOT NULL COMMENT '主键',
    `name_`                    varchar(255)     DEFAULT NULL COMMENT '部署名称',
    `category_`                varchar(255)     DEFAULT NULL COMMENT '类型',
    `key_`                     varchar(255)     DEFAULT NULL COMMENT '关键字',
    `tenant_id_`               varchar(255)     DEFAULT '' COMMENT '租户 id',
    `deploy_time_`             timestamp   NULL DEFAULT NULL COMMENT '部署时间',
    `engine_version_`          varchar(255)     DEFAULT NULL COMMENT '流程引擎版本号',
    `version_`                 int(11)          DEFAULT 1 COMMENT '版本号, 从 1 开始',
    `project_release_version_` varchar(255)     DEFAULT NULL COMMENT '存储项目发布的版本信息',
    PRIMARY KEY (`id_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='流程部署表';
-- ================================================================================================================================
-- 流程模型表
DROP TABLE IF EXISTS `act_re_model`;
CREATE TABLE `act_re_model`
(
    `id_`                           varchar(64) NOT NULL COMMENT '主键',
    `rev_`                          int(11)          DEFAULT NULL COMMENT '乐观锁版本号',
    `name_`                         varchar(255)     DEFAULT NULL COMMENT '模型名称',
    `key_`                          varchar(255)     DEFAULT NULL COMMENT '模型关键字',
    `category_`                     varchar(255)     DEFAULT NULL COMMENT '类型',
    `create_time_`                  timestamp   NULL DEFAULT NULL COMMENT '创建时间',
    `last_update_time_`             timestamp   NULL DEFAULT NULL COMMENT '最后修改时间',
    `version_`                      int(11)          DEFAULT NULL COMMENT '版本号, 从 1 开始',
    `meta_info_`                    varchar(4000)    DEFAULT NULL COMMENT '以 json 格式保存流程定义的信息',
    `deployment_id_`                varchar(64)      DEFAULT NULL COMMENT 'act_re_deployment 表 id_',
    `editor_source_value_id_`       varchar(64)      DEFAULT NULL COMMENT '流程文件放在 act_ge_bytearray 表中的 id_',
    `editor_source_extra_value_id_` varchar(64)      DEFAULT NULL COMMENT '流程文件图像放在 act_ge_bytearray 表中的 id_',
    `tenant_id_`                    varchar(255)     DEFAULT '' COMMENT '租户 id',
    PRIMARY KEY (`id_`),
    KEY `act_fk_model_source` (`editor_source_value_id_`),
    KEY `act_fk_model_source_extra` (`editor_source_extra_value_id_`),
    KEY `act_fk_model_deployment` (`deployment_id_`),
    CONSTRAINT `act_fk_model_deployment` FOREIGN KEY (`deployment_id_`) REFERENCES `act_re_deployment` (`id_`),
    CONSTRAINT `act_fk_model_source` FOREIGN KEY (`editor_source_value_id_`) REFERENCES `act_ge_bytearray` (`id_`),
    CONSTRAINT `act_fk_model_source_extra` FOREIGN KEY (`editor_source_extra_value_id_`) REFERENCES `act_ge_bytearray` (`id_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='流程模型表';
-- ================================================================================================================================
-- 流程定义表
DROP TABLE IF EXISTS `act_re_procdef`;
CREATE TABLE `act_re_procdef`
(
    `id_`                     varchar(64)  NOT NULL COMMENT '主键 ( 规则: 流程定义 key:流程定义版本:生成的 id ) ',
    `rev_`                    int(11)       DEFAULT NULL COMMENT '乐观锁版本号',
    `category_`               varchar(255)  DEFAULT NULL COMMENT '类型 ( 流程文件定义 Namespace / 流程文件 targetNamespace 的属性值 ) ',
    `name_`                   varchar(255)  DEFAULT NULL COMMENT '流程名称 ( 流程文件 process 元素的 name 属性值 ) ',
    `key_`                    varchar(255) NOT NULL COMMENT '流程编号 ( 流程文件 process 元素的id属性值 ) ',
    `version_`                int(11)      NOT NULL COMMENT '版本号, 从 1 开始',
    `deployment_id_`          varchar(64)   DEFAULT NULL COMMENT 'act_re_deployment 表 id_',
    `resource_name_`          varchar(4000) DEFAULT NULL COMMENT '资源文件名称, 流程 bpmn 文件名称',
    `dgrm_resource_name_`     varchar(4000) DEFAULT NULL COMMENT '图片资源文件名称, png 流程图片名称, 同时部署流程和图片时生成',
    `description_`            varchar(4000) DEFAULT NULL COMMENT '描述信息',
    `has_start_form_key_`     tinyint(4)    DEFAULT NULL COMMENT 'start 节点是否存在 formKey: 0-否; 1-是;',
    `has_graphical_notation_` tinyint(4)    DEFAULT NULL COMMENT '是否有图形符号的标志',
    `suspension_state_`       int(11)       DEFAULT NULL COMMENT '是否挂起: 1-激活; 2-挂起;',
    `tenant_id_`              varchar(255)  DEFAULT '' COMMENT '租户 id',
    `engine_version_`         varchar(255)  DEFAULT NULL COMMENT '流程引擎版本号',
    `app_version_`            int(11)       DEFAULT NULL COMMENT '自定义应用版本号, 对应 act_re_deployment 表 project_release_version_ 字段',
    PRIMARY KEY (`id_`),
    UNIQUE KEY `act_uniq_procdef` (`key_`, `version_`, `tenant_id_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='流程定义表';
-- ================================================================================================================================
-- 作业死亡信息表
DROP TABLE IF EXISTS `act_ru_deadletter_job`;
CREATE TABLE `act_ru_deadletter_job`
(
    `id_`                  varchar(64)  NOT NULL COMMENT '主键',
    `rev_`                 int(11)           DEFAULT NULL COMMENT '乐观锁版本号',
    `type_`                varchar(255) NOT NULL COMMENT '类型',
    `exclusive_`           tinyint(1)        DEFAULT NULL COMMENT '是否为排他任务',
    `execution_id_`        varchar(64)       DEFAULT NULL COMMENT '执行实例 id ( act_ru_execution 表 id_ ) ',
    `process_instance_id_` varchar(64)       DEFAULT NULL COMMENT '流程实例 id ( act_ru_execution 表 proc_inst_id_, ps: 如果没有子节点, 则此字段值为 id_, 否则值为父节点 id_ ) ',
    `proc_def_id_`         varchar(64)       DEFAULT NULL COMMENT '流程定义 id ( act_re_procdef 表 id_ ) ',
    `exception_stack_id_`  varchar(64)       DEFAULT NULL COMMENT '异常信息 id ( act_ge_bytearray 表 id ) ',
    `exception_msg_`       varchar(4000)     DEFAULT NULL COMMENT '异常信息',
    `duedate_`             timestamp    NULL DEFAULT NULL COMMENT '到期时间',
    `repeat_`              varchar(255)      DEFAULT NULL COMMENT '重复配置',
    `handler_type_`        varchar(255)      DEFAULT NULL COMMENT '处理类型',
    `handler_cfg_`         varchar(4000)     DEFAULT NULL COMMENT '处理器配置',
    `tenant_id_`           varchar(255)      DEFAULT '' COMMENT '租户 id',
    PRIMARY KEY (`id_`),
    KEY `act_fk_deadletter_job_execution` (`execution_id_`),
    KEY `act_fk_deadletter_job_process_instance` (`process_instance_id_`),
    KEY `act_fk_deadletter_job_proc_def` (`proc_def_id_`),
    KEY `act_fk_deadletter_job_exception` (`exception_stack_id_`),
    CONSTRAINT `act_fk_deadletter_job_exception` FOREIGN KEY (`exception_stack_id_`) REFERENCES `act_ge_bytearray` (`id_`),
    CONSTRAINT `act_fk_deadletter_job_execution` FOREIGN KEY (`execution_id_`) REFERENCES `act_ru_execution` (`id_`),
    CONSTRAINT `act_fk_deadletter_job_process_instance` FOREIGN KEY (`process_instance_id_`) REFERENCES `act_ru_execution` (`id_`),
    CONSTRAINT `act_fk_deadletter_job_proc_def` FOREIGN KEY (`proc_def_id_`) REFERENCES `act_re_procdef` (`id_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='作业死亡信息表';
-- ================================================================================================================================
-- 事件订阅表
DROP TABLE IF EXISTS `act_ru_event_subscr`;
CREATE TABLE `act_ru_event_subscr`
(
    `id_`            varchar(64)  NOT NULL COMMENT '主键',
    `rev_`           int(11)               DEFAULT NULL COMMENT '乐观锁版本号',
    `event_type_`    varchar(255) NOT NULL COMMENT '事件类型',
    `event_name_`    varchar(255)          DEFAULT NULL COMMENT '事件名称',
    `execution_id_`  varchar(64)           DEFAULT NULL COMMENT '执行实例 id ( act_ru_execution 表 id_ ) ',
    `proc_inst_id_`  varchar(64)           DEFAULT NULL COMMENT '流程实例 id ( act_ru_execution 表 proc_inst_id_, ps: 如果没有子节点, 则此字段值为 id_, 否则值为父节点 id_ ) ',
    `activity_id_`   varchar(64)           DEFAULT NULL COMMENT '活动节点 id ( 对应流程文件 process 中的节点 id ) ',
    `configuration_` varchar(255)          DEFAULT NULL COMMENT '配置信息',
    `created_`       timestamp    NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
    `proc_def_id_`   varchar(64)           DEFAULT NULL COMMENT '流程定义 id ( act_re_procdef 表 id_ ) ',
    `tenant_id_`     varchar(255)          DEFAULT '' COMMENT '租户 id',
    PRIMARY KEY (`id_`),
    KEY `act_idx_event_subscr_config_` (`configuration_`),
    KEY `act_fk_event_exec` (`execution_id_`),
    CONSTRAINT `act_fk_event_exec` FOREIGN KEY (`execution_id_`) REFERENCES `act_ru_execution` (`id_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='事件订阅表';
-- ================================================================================================================================
-- 流程执行实例表
DROP TABLE IF EXISTS `act_ru_execution`;
CREATE TABLE `act_ru_execution`
(
    `id_`                   varchar(64) NOT NULL COMMENT '主键',
    `rev_`                  int(11)          DEFAULT NULL COMMENT '乐观锁版本号',
    `proc_inst_id_`         varchar(64)      DEFAULT NULL COMMENT '流程实例id, 如果没有子节点, 则此字段值为 id_, 否则值为父节点 id_',
    `business_key_`         varchar(255)     DEFAULT NULL COMMENT '业务 key, 用于将业务数据与流程实例关联',
    `parent_id_`            varchar(64)      DEFAULT NULL COMMENT '父流程实例 id ( act_ru_execution 表 id_ ) ',
    `proc_def_id_`          varchar(64)      DEFAULT NULL COMMENT '流程定义 id ( act_re_procdef 表 id_ ) ',
    `super_exec_`           varchar(64)      DEFAULT NULL COMMENT '上级执行实例 id ( act_ru_execution 表 id_ ) ',
    `root_proc_inst_id_`    varchar(64)      DEFAULT NULL COMMENT '根流程实例id ( act_ru_execution 表 id_ ) ',
    `act_id_`               varchar(255)     DEFAULT NULL COMMENT '表示流程运行到的节点',
    `is_active_`            tinyint(4)       DEFAULT NULL COMMENT '是否激活: 1-激活; 2-挂起;',
    `is_concurrent_`        tinyint(4)       DEFAULT NULL COMMENT '是否并行: 1-是; 0-否;',
    `is_scope_`             tinyint(4)       DEFAULT NULL COMMENT '主实例字段为 1, 子实例字段为 0',
    `is_event_scope_`       tinyint(4)       DEFAULT NULL COMMENT '是否为事件作用域: 1-是; 0-否;',
    `is_mi_root_`           tinyint(4)       DEFAULT NULL COMMENT '是否是多实例的根流程: 1-是; 0-否;',
    `suspension_state_`     int(11)          DEFAULT NULL COMMENT '挂起状态: 1-激活; 2-挂起;',
    `cached_ent_state_`     int(11)          DEFAULT NULL COMMENT '缓存结束状态: 1-事件监听; 2-人工任务; 3-异步作业;',
    `tenant_id_`            varchar(255)     DEFAULT '' COMMENT '租户 id',
    `name_`                 varchar(255)     DEFAULT NULL COMMENT '名称',
    `start_time_`           datetime         DEFAULT NULL COMMENT '开始时间',
    `start_user_id_`        varchar(255)     DEFAULT NULL COMMENT '启动实例用户 id',
    `lock_time_`            timestamp   NULL DEFAULT NULL COMMENT '锁定时间',
    `is_count_enabled_`     tinyint(4)       DEFAULT NULL COMMENT '是否能够计数: 1-是; 0-否;',
    `evt_subscr_count_`     int(11)          DEFAULT NULL COMMENT '事件订阅数量',
    `task_count_`           int(11)          DEFAULT NULL COMMENT '任务数量',
    `job_count_`            int(11)          DEFAULT NULL COMMENT '定时任务数量',
    `timer_job_count_`      int(11)          DEFAULT NULL COMMENT '定时器工作数量',
    `susp_job_count_`       int(11)          DEFAULT NULL COMMENT '挂起工作数量',
    `deadletter_job_count_` int(11)          DEFAULT NULL COMMENT '死信工作数量',
    `var_count_`            int(11)          DEFAULT NULL COMMENT '变量数量',
    `id_link_count_`        int(11)          DEFAULT NULL COMMENT '标识链接数量',
    `app_version_`          int(11)          DEFAULT NULL COMMENT '应用版本, 对应 act_re_deployment 表 project_release_version_ 字段',
    PRIMARY KEY (`id_`),
    KEY `act_idx_exec_buskey` (`business_key_`),
    KEY `act_idc_exec_root` (`root_proc_inst_id_`),
    KEY `act_fk_exe_procinst` (`proc_inst_id_`),
    KEY `act_fk_exe_parent` (`parent_id_`),
    KEY `act_fk_exe_super` (`super_exec_`),
    KEY `act_fk_exe_procdef` (`proc_def_id_`),
    CONSTRAINT `act_fk_exe_parent` FOREIGN KEY (`parent_id_`) REFERENCES `act_ru_execution` (`id_`) ON DELETE CASCADE,
    CONSTRAINT `act_fk_exe_procdef` FOREIGN KEY (`proc_def_id_`) REFERENCES `act_re_procdef` (`id_`),
    CONSTRAINT `act_fk_exe_procinst` FOREIGN KEY (`proc_inst_id_`) REFERENCES `act_ru_execution` (`id_`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `act_fk_exe_super` FOREIGN KEY (`super_exec_`) REFERENCES `act_ru_execution` (`id_`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='流程执行实例表';
-- ================================================================================================================================
-- 身份链接表
DROP TABLE IF EXISTS `act_ru_identitylink`;
CREATE TABLE `act_ru_identitylink`
(
    `id_`           varchar(64) NOT NULL COMMENT '主键',
    `rev_`          int(11)      DEFAULT NULL COMMENT '乐观锁版本号',
    `group_id_`     varchar(255) DEFAULT NULL COMMENT '组 id',
    `type_`         varchar(255) DEFAULT NULL COMMENT '类型: assignee-受让人; candidate-候选人; owner-所有者; starter-起动器; participant-参与者;',
    `user_id_`      varchar(255) DEFAULT NULL COMMENT '用户 id',
    `task_id_`      varchar(64)  DEFAULT NULL COMMENT '任务 id',
    `proc_inst_id_` varchar(64)  DEFAULT NULL COMMENT '流程实例 id',
    `proc_def_id_`  varchar(64)  DEFAULT NULL COMMENT '流程定义 id',
    PRIMARY KEY (`id_`),
    KEY `act_idx_ident_lnk_user` (`user_id_`),
    KEY `act_idx_ident_lnk_group` (`group_id_`),
    KEY `act_idx_athrz_procedef` (`proc_def_id_`),
    KEY `act_fk_tskass_task` (`task_id_`),
    KEY `act_fk_idl_procinst` (`proc_inst_id_`),
    CONSTRAINT `act_fk_athrz_procedef` FOREIGN KEY (`proc_def_id_`) REFERENCES `act_re_procdef` (`id_`),
    CONSTRAINT `act_fk_idl_procinst` FOREIGN KEY (`proc_inst_id_`) REFERENCES `act_ru_execution` (`id_`),
    CONSTRAINT `act_fk_tskass_task` FOREIGN KEY (`task_id_`) REFERENCES `act_ru_task` (`id_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='身份链接表';
-- ================================================================================================================================
-- 集成综合表
DROP TABLE IF EXISTS `act_ru_integration`;
CREATE TABLE `act_ru_integration`
(
    `id_`                  varchar(64) NOT NULL COMMENT '主键',
    `execution_id_`        varchar(64)          DEFAULT NULL COMMENT '执行实例 id ( act_ru_execution 表 id_ ) ',
    `process_instance_id_` varchar(64)          DEFAULT NULL COMMENT '流程实例 id ( act_ru_execution 表 proc_inst_id_, ps: 如果没有子节点, 则此字段值为 id_, 否则值为父节点 id_ ) ',
    `proc_def_id_`         varchar(64)          DEFAULT NULL COMMENT '流程定义 id ( act_re_procdef 表 id_ ) ',
    `flow_node_id_`        varchar(64)          DEFAULT NULL COMMENT '流程节点 id',
    `created_date_`        timestamp   NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '创建时间, 并在更新时自动更新为当前时间',
    PRIMARY KEY (`id_`),
    KEY `act_fk_int_execution` (`execution_id_`),
    KEY `act_fk_int_proc_inst` (`process_instance_id_`),
    KEY `act_fk_int_proc_def` (`proc_def_id_`),
    CONSTRAINT `act_fk_int_execution` FOREIGN KEY (`execution_id_`) REFERENCES `act_ru_execution` (`id_`) ON DELETE CASCADE,
    CONSTRAINT `act_fk_int_proc_def` FOREIGN KEY (`proc_def_id_`) REFERENCES `act_re_procdef` (`id_`),
    CONSTRAINT `act_fk_int_proc_inst` FOREIGN KEY (`process_instance_id_`) REFERENCES `act_ru_execution` (`id_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='集成综合表';
-- ================================================================================================================================
-- 定时任务表
DROP TABLE IF EXISTS `act_ru_job`;
CREATE TABLE `act_ru_job`
(
    `id_`                  varchar(64)  NOT NULL COMMENT '主键',
    `rev_`                 int(11)           DEFAULT NULL COMMENT '乐观锁版本号',
    `type_`                varchar(255) NOT NULL COMMENT '类型',
    `lock_exp_time_`       timestamp    NULL DEFAULT NULL COMMENT '任务锁定过期时间',
    `lock_owner_`          varchar(255)      DEFAULT NULL COMMENT '任务锁定人',
    `exclusive_`           tinyint(1)        DEFAULT NULL COMMENT '是否为排他任务',
    `execution_id_`        varchar(64)       DEFAULT NULL COMMENT '执行实例 id ( act_ru_execution 表 id_ ) ',
    `process_instance_id_` varchar(64)       DEFAULT NULL COMMENT '流程实例 id ( act_ru_execution 表 proc_inst_id_, ps: 如果没有子节点, 则此字段值为 id_, 否则值为父节点 id_ ) ',
    `proc_def_id_`         varchar(64)       DEFAULT NULL COMMENT '流程定义 id ( act_re_procdef 表 id_ ) ',
    `retries_`             int(11)           DEFAULT NULL COMMENT '重试次数',
    `exception_stack_id_`  varchar(64)       DEFAULT NULL COMMENT '异常信息 id ( act_ge_bytearray 表的id_ ) ',
    `exception_msg_`       varchar(4000)     DEFAULT NULL COMMENT '异常信息',
    `duedate_`             timestamp    NULL DEFAULT NULL COMMENT '任务的到期时间',
    `repeat_`              varchar(255)      DEFAULT NULL COMMENT '重复配置',
    `handler_type_`        varchar(255)      DEFAULT NULL COMMENT '处理类型',
    `handler_cfg_`         varchar(4000)     DEFAULT NULL COMMENT '处理配置',
    `tenant_id_`           varchar(255)      DEFAULT '' COMMENT '租户 id',
    PRIMARY KEY (`id_`),
    KEY `act_fk_job_execution` (`execution_id_`),
    KEY `act_fk_job_process_instance` (`process_instance_id_`),
    KEY `act_fk_job_proc_def` (`proc_def_id_`),
    KEY `act_fk_job_exception` (`exception_stack_id_`),
    CONSTRAINT `act_fk_job_exception` FOREIGN KEY (`exception_stack_id_`) REFERENCES `act_ge_bytearray` (`id_`),
    CONSTRAINT `act_fk_job_execution` FOREIGN KEY (`execution_id_`) REFERENCES `act_ru_execution` (`id_`),
    CONSTRAINT `act_fk_job_process_instance` FOREIGN KEY (`process_instance_id_`) REFERENCES `act_ru_execution` (`id_`),
    CONSTRAINT `act_fk_job_proc_def` FOREIGN KEY (`proc_def_id_`) REFERENCES `act_re_procdef` (`id_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='定时任务表';
-- ================================================================================================================================
-- 作业暂停表
DROP TABLE IF EXISTS `act_ru_suspended_job`;
CREATE TABLE `act_ru_suspended_job`
(
    `id_`                  varchar(64)  NOT NULL COMMENT '主键',
    `rev_`                 int(11)           DEFAULT NULL COMMENT '乐观锁版本号',
    `type_`                varchar(255) NOT NULL COMMENT '类型',
    `exclusive_`           tinyint(1)        DEFAULT NULL COMMENT '是否为排他任务',
    `execution_id_`        varchar(64)       DEFAULT NULL COMMENT '执行实例 id ( act_ru_execution 表 id_ ) ',
    `process_instance_id_` varchar(64)       DEFAULT NULL COMMENT '流程实例 id ( act_ru_execution 表 proc_inst_id_, ps: 如果没有子节点, 则此字段值为 id_, 否则值为父节点 id_ ) ',
    `proc_def_id_`         varchar(64)       DEFAULT NULL COMMENT '流程定义 id ( act_re_procdef 表 id_ ) ',
    `retries_`             int(11)           DEFAULT NULL COMMENT '重试次数',
    `exception_stack_id_`  varchar(64)       DEFAULT NULL COMMENT '异常信息 id, act_ge_bytearray 表的id_',
    `exception_msg_`       varchar(4000)     DEFAULT NULL COMMENT '异常消息',
    `duedate_`             timestamp    NULL DEFAULT NULL COMMENT '任务的到期时间',
    `repeat_`              varchar(255)      DEFAULT NULL COMMENT '重复配置',
    `handler_type_`        varchar(255)      DEFAULT NULL COMMENT '处理类型',
    `handler_cfg_`         varchar(4000)     DEFAULT NULL COMMENT '处理配置',
    `tenant_id_`           varchar(255)      DEFAULT '' COMMENT '租户 id',
    PRIMARY KEY (`id_`),
    KEY `act_fk_suspended_job_execution` (`execution_id_`),
    KEY `act_fk_suspended_job_process_instance` (`process_instance_id_`),
    KEY `act_fk_suspended_job_proc_def` (`proc_def_id_`),
    KEY `act_fk_suspended_job_exception` (`exception_stack_id_`),
    CONSTRAINT `act_fk_suspended_job_exception` FOREIGN KEY (`exception_stack_id_`) REFERENCES `act_ge_bytearray` (`id_`),
    CONSTRAINT `act_fk_suspended_job_execution` FOREIGN KEY (`execution_id_`) REFERENCES `act_ru_execution` (`id_`),
    CONSTRAINT `act_fk_suspended_job_process_instance` FOREIGN KEY (`process_instance_id_`) REFERENCES `act_ru_execution` (`id_`),
    CONSTRAINT `act_fk_suspended_job_proc_def` FOREIGN KEY (`proc_def_id_`) REFERENCES `act_re_procdef` (`id_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='作业暂停表';
-- ================================================================================================================================
-- 任务数据表
DROP TABLE IF EXISTS `act_ru_task`;
CREATE TABLE `act_ru_task`
(
    `id_`               varchar(64) NOT NULL COMMENT '主键',
    `rev_`              int(11)          DEFAULT NULL COMMENT '乐观锁版本号',
    `execution_id_`     varchar(64)      DEFAULT NULL COMMENT '执行实例 id ( act_ru_execution 表 id_ ) ',
    `proc_inst_id_`     varchar(64)      DEFAULT NULL COMMENT '流程实例 id ( act_ru_execution 表 proc_inst_id_, ps: 如果没有子节点, 则此字段值为 id_, 否则值为父节点 id_ ) ',
    `proc_def_id_`      varchar(64)      DEFAULT NULL COMMENT '流程定义 id ( act_re_procdef 表 id_ ) ',
    `name_`             varchar(255)     DEFAULT NULL COMMENT '任务名称',
    `business_key_`     varchar(255)     DEFAULT NULL COMMENT '业务 key',
    `parent_task_id_`   varchar(64)      DEFAULT NULL COMMENT '父任务 id',
    `description_`      varchar(4000)    DEFAULT NULL COMMENT '任务描述',
    `task_def_key_`     varchar(255)     DEFAULT NULL COMMENT '任务定义 key ( 对应流程文件 process 中的节点 id ) ',
    `owner_`            varchar(255)     DEFAULT NULL COMMENT '任务所属人 ( 存在委托行为才会有值 ) ',
    `assignee_`         varchar(255)     DEFAULT NULL COMMENT '任务的受理人',
    `delegation_`       varchar(64)      DEFAULT NULL COMMENT '委托状态: pending, resolved, 如无委托则为空',
    `priority_`         int(11)          DEFAULT NULL COMMENT '优先级 ( 默认 50 ) ',
    `create_time_`      timestamp   NULL DEFAULT NULL COMMENT '创建时间',
    `due_date_`         datetime         DEFAULT NULL COMMENT '截止日期',
    `category_`         varchar(255)     DEFAULT NULL COMMENT '任务的类别',
    `suspension_state_` int(11)          DEFAULT NULL COMMENT '挂起状态: 1-激活; 2-挂起;',
    `tenant_id_`        varchar(255)     DEFAULT '' COMMENT '租户 id',
    `form_key_`         varchar(255)     DEFAULT NULL COMMENT '关联的表单键',
    `claim_time_`       datetime         DEFAULT NULL COMMENT '任务的认领时间',
    `app_version_`      int(11)          DEFAULT NULL COMMENT '应用版本, 对应 act_re_deployment 表 project_release_version_ 字段',
    PRIMARY KEY (`id_`),
    KEY `act_idx_task_create` (`create_time_`),
    KEY `act_fk_task_exe` (`execution_id_`),
    KEY `act_fk_task_procinst` (`proc_inst_id_`),
    KEY `act_fk_task_procdef` (`proc_def_id_`),
    CONSTRAINT `act_fk_task_exe` FOREIGN KEY (`execution_id_`) REFERENCES `act_ru_execution` (`id_`),
    CONSTRAINT `act_fk_task_procdef` FOREIGN KEY (`proc_def_id_`) REFERENCES `act_re_procdef` (`id_`),
    CONSTRAINT `act_fk_task_procinst` FOREIGN KEY (`proc_inst_id_`) REFERENCES `act_ru_execution` (`id_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='任务数据表';
-- ================================================================================================================================
-- 定时器作业表
DROP TABLE IF EXISTS `act_ru_timer_job`;
CREATE TABLE `act_ru_timer_job`
(
    `id_`                  varchar(64)  NOT NULL COMMENT '主键',
    `rev_`                 int(11)           DEFAULT NULL COMMENT '乐观锁版本号',
    `type_`                varchar(255) NOT NULL COMMENT '类型',
    `lock_exp_time_`       timestamp    NULL DEFAULT NULL COMMENT '任务锁定过期时间',
    `lock_owner_`          varchar(255)      DEFAULT NULL COMMENT '任务锁定人',
    `exclusive_`           tinyint(1)        DEFAULT NULL COMMENT '是否为排他任务',
    `execution_id_`        varchar(64)       DEFAULT NULL COMMENT '执行实例 id ( act_ru_execution 表 id_ ) ',
    `process_instance_id_` varchar(64)       DEFAULT NULL COMMENT '流程实例 id ( act_ru_execution 表 proc_inst_id_, ps: 如果没有子节点, 则此字段值为 id_, 否则值为父节点 id_ ) ',
    `proc_def_id_`         varchar(64)       DEFAULT NULL COMMENT '流程定义 id ( act_re_procdef 表 id_ ) ',
    `retries_`             int(11)           DEFAULT NULL COMMENT '重试次数',
    `exception_stack_id_`  varchar(64)       DEFAULT NULL COMMENT '异常 id ( act_ge_bytearray 表 id_ ) ',
    `exception_msg_`       varchar(4000)     DEFAULT NULL COMMENT '异常消息',
    `duedate_`             timestamp    NULL DEFAULT NULL COMMENT '任务的到期时间',
    `repeat_`              varchar(255)      DEFAULT NULL COMMENT '重复配置',
    `handler_type_`        varchar(255)      DEFAULT NULL COMMENT '处理类型',
    `handler_cfg_`         varchar(4000)     DEFAULT NULL COMMENT '处理配置',
    `tenant_id_`           varchar(255)      DEFAULT '' COMMENT '租户 id',
    PRIMARY KEY (`id_`),
    KEY `act_fk_timer_job_execution` (`execution_id_`),
    KEY `act_fk_timer_job_process_instance` (`process_instance_id_`),
    KEY `act_fk_timer_job_proc_def` (`proc_def_id_`),
    KEY `act_fk_timer_job_exception` (`exception_stack_id_`),
    CONSTRAINT `act_fk_timer_job_exception` FOREIGN KEY (`exception_stack_id_`) REFERENCES `act_ge_bytearray` (`id_`),
    CONSTRAINT `act_fk_timer_job_execution` FOREIGN KEY (`execution_id_`) REFERENCES `act_ru_execution` (`id_`),
    CONSTRAINT `act_fk_timer_job_process_instance` FOREIGN KEY (`process_instance_id_`) REFERENCES `act_ru_execution` (`id_`),
    CONSTRAINT `act_fk_timer_job_proc_def` FOREIGN KEY (`proc_def_id_`) REFERENCES `act_re_procdef` (`id_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='定时器作业表';
-- ================================================================================================================================
-- 运行时流程变量数据表
DROP TABLE IF EXISTS `act_ru_variable`;
CREATE TABLE `act_ru_variable`
(
    `id_`           varchar(64)  NOT NULL COMMENT '主键',
    `rev_`          int(11)       DEFAULT NULL COMMENT '乐观锁版本号',
    `type_`         varchar(255) NOT NULL COMMENT '类型',
    `name_`         varchar(255) NOT NULL COMMENT '变量名称',
    `execution_id_` varchar(64)   DEFAULT NULL COMMENT '执行实例 id ( act_ru_execution 表 id_ ) ',
    `proc_inst_id_` varchar(64)   DEFAULT NULL COMMENT '流程实例 id ( act_ru_execution 表 proc_inst_id_, ps: 如果没有子节点, 则此字段值为 id_, 否则值为父节点 id_ ) ',
    `task_id_`      varchar(64)   DEFAULT NULL COMMENT '任务 id ( act_ru_task 表 id_, 如果流程结束, 该表数据会清空, 可以在 act_hi_taskinst 表中找到历史数据 ) ',
    `bytearray_id_` varchar(64)   DEFAULT NULL COMMENT 'act_ge_bytearray 表 id',
    `double_`       double        DEFAULT NULL COMMENT '变量的 double 类型值',
    `long_`         bigint(20)    DEFAULT NULL COMMENT '变量的 long 类型值',
    `text_`         varchar(4000) DEFAULT NULL COMMENT '变量的文本值, 如此处存储 JPA 持久化对象时, 值存 ClassName',
    `text2_`        varchar(4000) DEFAULT NULL COMMENT '变量的附加文本值, 此处存储的是 JPA 持久化对象时, 此值存对象 ID',
    PRIMARY KEY (`id_`),
    KEY `act_idx_variable_task_id` (`task_id_`),
    KEY `act_fk_var_exe` (`execution_id_`),
    KEY `act_fk_var_procinst` (`proc_inst_id_`),
    KEY `act_fk_var_bytearray` (`bytearray_id_`),
    CONSTRAINT `act_fk_var_bytearray` FOREIGN KEY (`bytearray_id_`) REFERENCES `act_ge_bytearray` (`id_`),
    CONSTRAINT `act_fk_var_exe` FOREIGN KEY (`execution_id_`) REFERENCES `act_ru_execution` (`id_`),
    CONSTRAINT `act_fk_var_procinst` FOREIGN KEY (`proc_inst_id_`) REFERENCES `act_ru_execution` (`id_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='运行时流程变量数据表';
-- ================================================================================================================================
INSERT INTO `act_ge_property` (`name_`, `value_`, `rev_`)
VALUES ('cfg.execution-related-entities-count', 'false', 1);
INSERT INTO `act_ge_property` (`name_`, `value_`, `rev_`)
VALUES ('next.dbid', '1', 1);
INSERT INTO `act_ge_property` (`name_`, `value_`, `rev_`)
VALUES ('schema.history', 'create(7.1.0-M6)', 1);
INSERT INTO `act_ge_property` (`name_`, `value_`, `rev_`)
VALUES ('schema.version', '7.1.0-M6', 1);
-- 开启外键约束检查   最后再开启外键约束
SET FOREIGN_KEY_CHECKS = 1;