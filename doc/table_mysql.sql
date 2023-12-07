/*DROP DATABASE IF EXISTS `spring_boot_activiti`;

CREATE DATABASE `spring_boot_activiti` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

USE `spring_boot_activiti`;*/

-- 用户信息表
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`
(
    `id`          bigint   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username`    varchar(32)       DEFAULT NULL COMMENT '用户名',
    `name`        varchar(32)       DEFAULT NULL COMMENT '用户名',
    `status`      tinyint           DEFAULT '0' COMMENT '用户状态:0-正常;1-禁用',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    `update_time` datetime          DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新时间',
    `deleted`     tinyint  NOT NULL DEFAULT '0' COMMENT '逻辑删除:0-正常;1-删除',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户信息表';

-- 请假申请表
DROP TABLE IF EXISTS `biz_leave_apply`;
CREATE TABLE `biz_leave_apply`
(
    `id`          bigint   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `userId`      bigint   NOT NULL COMMENT '申请人id',
    `start_time`  datetime NOT NULL COMMENT '开始时间',
    `end_time`    datetime NOT NULL COMMENT '结束时间',
    `type`        tinyint  NOT NULL COMMENT '类型:1-事假;2-病假;3-年假;4-丧假;5-产假;',
    `reason`      varchar(1024) DEFAULT '0' COMMENT '原因',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    `update_time` datetime      DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='请假申请表';

INSERT INTO `sys_user` (`id`,
                        `username`,
                        `name`,
                        `status`,
                        `create_time`,
                        `update_time`)
VALUES (1,
        'admin',
        '管理员',
        0,
        NOW(),
        NOW()),
       (2,
        'user1',
        '小明'
           ,
        NOW(),
        NOW(),
        0),
       (3,
        'user2',
        '小芳',
        0,
        NOW(),
        NOW());
COMMIT;