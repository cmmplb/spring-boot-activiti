-- ================================================================================================================================
-- 系统用户表
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`
(
    `id`          bigint unsigned                        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username`    varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
    `password`    varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码( MD5 加密)',
    `name`        varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '姓名',
    `avatar`      longtext COLLATE utf8mb4_general_ci COMMENT '头像 base64 存储',
    `enabled`     tinyint(1) unsigned                    NOT NULL DEFAULT '0' COMMENT '状态: 0-启用; 1-禁用;',
    `tenant_id`   bigint unsigned                        NOT NULL DEFAULT '0' COMMENT '租户 id',
    `create_time` datetime                               NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime                               NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uq_username` (`username`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='系统用户表';
-- ================================================================================================================================
-- 系统用户角色关联表
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`
(
    `id`          bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`     bigint unsigned NOT NULL COMMENT '用户 id',
    `role_id`     bigint unsigned NOT NULL COMMENT '角色 id',
    `create_time` datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uq_user_id_role_id` (`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='系统用户角色关联表';
-- ================================================================================================================================
-- 系统角色表
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`
(
    `id`          bigint unsigned                        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`        varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
    `code`        varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '编码',
    `description` varchar(256) COLLATE utf8mb4_general_ci         DEFAULT NULL COMMENT '描述',
    `enabled`     tinyint(1) unsigned                    NOT NULL DEFAULT '0' COMMENT '状态: 0-启用; 1-禁用;',
    `tenant_id`   bigint unsigned                        NOT NULL DEFAULT '0' COMMENT '租户 id',
    `create_time` datetime                               NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime                               NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='系统角色表';
-- ================================================================================================================================
-- 系统角色菜单关联表
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`
(
    `id`          bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `role_id`     bigint unsigned NOT NULL COMMENT '角色 id',
    `menu_id`     bigint unsigned NOT NULL COMMENT '菜单 id',
    `create_time` datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uq_menu_id_role_id` (`menu_id`, `role_id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='系统角色菜单关联表';
-- ================================================================================================================================
-- 系统菜单表
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`
(
    `id`          bigint unsigned                         NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`        varchar(64) COLLATE utf8mb4_general_ci  NOT NULL COMMENT '名称',
    `icon`        varchar(256) COLLATE utf8mb4_general_ci          DEFAULT NULL COMMENT '图标',
    `code`        varchar(128) COLLATE utf8mb4_general_ci NOT NULL COMMENT '编码',
    `path`        varchar(256) COLLATE utf8mb4_general_ci NOT NULL COMMENT '路由',
    `visible`     tinyint(1) unsigned                     NOT NULL DEFAULT '0' COMMENT '显示状态: 0-显示; 1-隐藏',
    `parent_id`   bigint unsigned                         NOT NULL DEFAULT '0' COMMENT '父菜单 id( 0 为一级菜单 )',
    `sort_num`    int unsigned                            NOT NULL DEFAULT '1' COMMENT '排序编号',
    `enabled`     tinyint(1) unsigned                     NOT NULL DEFAULT '0' COMMENT '状态: 0-启用; 1-禁用;',
    `type`        tinyint(1) unsigned                     NOT NULL DEFAULT '0' COMMENT '类别: 0-菜单; 1-按钮;',
    `tenant_id`   bigint unsigned                         NOT NULL DEFAULT '0' COMMENT '租户 id',
    `create_time` datetime                                NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime                                NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `UNIQUE_CODE` (`code`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='系统菜单表';