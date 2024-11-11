-- 事项申请相关表结构
-- ================================================================================================================================
-- 事项申请表
DROP TABLE IF EXISTS `biz_apply`;
CREATE TABLE `biz_apply`
(
    `id`          bigint unsigned                         NOT NULL AUTO_INCREMENT COMMENT '主键',
    `title`       varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '标题',
    `user_id`     bigint unsigned                         NOT NULL COMMENT '申请人 sys_user 用户表 id',
    `type`        tinyint(1) unsigned                     NOT NULL DEFAULT '0' COMMENT '类型: 1-考勤管理; 2-行政管理; 3-财务管理; 4-人事管理; 5-...',
    `subtype`     tinyint(1) unsigned                     NOT NULL DEFAULT '0' COMMENT '对应不同业务的子类型',
    `business_id` bigint                                  NOT NULL COMMENT '类型对应的业务申请表 id',
    `status`      tinyint(1) unsigned                     NOT NULL DEFAULT '0' COMMENT '流程状态: 0-进行中; 1-已完成; 2-已驳回; 3-已撤销;',
    `def_key`     varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'act_re_procdef 流程定义表 key',
    `create_time` datetime                                NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime                                NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='事项申请表';

-- ================================================================================================================================
-- 考勤管理申请表
DROP TABLE IF EXISTS `biz_attendance_apply`;
CREATE TABLE `biz_attendance_apply`
(
    `id`          bigint unsigned     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`     bigint unsigned     NOT NULL COMMENT '申请人id',
    `type`        tinyint(1) unsigned NOT NULL COMMENT '类型:1-请假; 2-出差;',
    `status`      tinyint(1) unsigned NOT NULL            DEFAULT '0' COMMENT '流程状态: 0-进行中; 1-已完成; 2-已驳回; 3-已撤销;',
    `remark`      varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
    `create_time` datetime            NOT NULL            DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime            NOT NULL            DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='考勤申请表';

-- ================================================================================================================================
-- 考勤申请请假表
DROP TABLE IF EXISTS `biz_attendance_apply_leave`;
CREATE TABLE `biz_attendance_apply_leave`
(
    `id`                  bigint              NOT NULL AUTO_INCREMENT COMMENT '主键',
    `attendance_apply_id` bigint              NOT NULL COMMENT '考勤申请表 id',
    `type`                tinyint(1) unsigned NOT NULL COMMENT '类型:1-事假; 2-病假; 3-年假; 4-丧假; 5-产假;',
    `start_time`          datetime            NOT NULL COMMENT '开始时间',
    `end_time`            datetime            NOT NULL COMMENT '结束时间',
    `create_time`         datetime            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`         datetime            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='考勤申请请假表';

-- ================================================================================================================================
-- 行政管理申请表
DROP TABLE IF EXISTS `biz_administration_apply`;
CREATE TABLE `biz_administration_apply`
(
    `id`          bigint unsigned     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`     bigint unsigned     NOT NULL COMMENT '申请人 sys_user 用户表 id',
    `type`        tinyint(1) unsigned NOT NULL COMMENT '类型:1-入库; 2-出库',
    `status`      tinyint(1) unsigned NOT NULL            DEFAULT '0' COMMENT '流程状态: 0-进行中; 1-已完成; 2-已驳回; 3-已撤销;',
    `remark`      varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
    `create_time` datetime            NOT NULL            DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime            NOT NULL            DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='行政管理申请表';

-- ================================================================================================================================
-- 行政管理申请表细表
DROP TABLE IF EXISTS `biz_administration_apply_detail`;
CREATE TABLE `biz_administration_apply_detail`
(
    `id`                      bigint              NOT NULL AUTO_INCREMENT COMMENT '主键',
    `administration_apply_id` bigint              NOT NULL COMMENT '行政管理申请表 id',
    `type`                    tinyint(1) unsigned NOT NULL COMMENT '类型:入库类型:1-正常入库; 2-退货入库; 3-调拨入库; 出库类型:1-物品领用; 2-报损; 3-退库;',
    `quantity`                int unsigned        NOT NULL DEFAULT 0 NULL COMMENT '数量',
    `material_name`           varchar(64)         NOT NULL NULL COMMENT '领用物料名称, 这里演示就固定几个物料',
    `create_time`             datetime            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`             datetime            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='行政管理申请表细表';

-- ================================================================================================================================
-- 财务管理申请表
DROP TABLE IF EXISTS `biz_finance_apply`;
CREATE TABLE `biz_finance_apply`
(
    `id`          bigint unsigned     NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`     bigint unsigned     NOT NULL COMMENT '申请人 sys_user 用户表 id',
    `type`        tinyint(1) unsigned NOT NULL COMMENT '类型:1-费用报销; 2-预算调整',
    `amount`      decimal(10, 2)      NOT NULL            DEFAULT 0.00 COMMENT '金额',
    `status`      tinyint(1) unsigned NOT NULL            DEFAULT '0' COMMENT '流程状态: 0-进行中; 1-已完成; 2-已驳回; 3-已撤销;',
    `remark`      varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
    `create_time` datetime            NOT NULL            DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime            NOT NULL            DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='财务管理申请表';