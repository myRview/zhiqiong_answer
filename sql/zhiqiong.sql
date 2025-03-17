create database zhiqiong;
use zhiqiong;


SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for app
-- ----------------------------
DROP TABLE IF EXISTS `app`;
CREATE TABLE `app`
(
    `id`               bigint(20)                                                     NOT NULL AUTO_INCREMENT COMMENT 'id',
    `app_name`         varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '应用名',
    `app_desc`         varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL     DEFAULT NULL COMMENT '应用描述',
    `app_icon`         varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL     DEFAULT NULL COMMENT '应用图标',
    `app_type`         tinyint(4)                                                     NOT NULL DEFAULT 0 COMMENT '应用类型（0-得分类，1-测评类）',
    `scoring_strategy` tinyint(4)                                                     NOT NULL DEFAULT 0 COMMENT '评分策略（0-自定义，1-AI）',
    `review_status`    int(11)                                                        NOT NULL DEFAULT 0 COMMENT '审核状态：0-待审核, 1-通过, 2-拒绝',
    `review_message`   varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NULL     DEFAULT NULL COMMENT '审核信息',
    `reviewer_id`      bigint(20)                                                     NULL     DEFAULT NULL COMMENT '审核人 id',
    `review_time`      datetime                                                       NULL     DEFAULT NULL COMMENT '审核时间',
    `user_id`          bigint(20)                                                     NOT NULL COMMENT '创建用户 id',
    `create_time`      datetime                                                       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`      datetime                                                       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `status`           tinyint(4)                                                     NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_app_name` (`app_name`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '应用表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for question
-- ----------------------------
DROP TABLE IF EXISTS `question`;
CREATE TABLE `question`
(
    `id`               bigint(20)                                            NOT NULL AUTO_INCREMENT COMMENT 'id',
    `question_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '题目内容（json格式）',
    `app_id`           bigint(20)                                            NOT NULL COMMENT '应用 id',
    `user_id`          bigint(20)                                            NOT NULL COMMENT '创建用户 id',
    `create_time`      datetime                                              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`      datetime                                              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `status`           tinyint(4)                                            NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_app_id` (`app_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '题目表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for scoring_result
-- ----------------------------
DROP TABLE IF EXISTS `scoring_result`;
CREATE TABLE `scoring_result`
(
    `id`                 bigint(20)                                                     NOT NULL AUTO_INCREMENT COMMENT 'id',
    `result_name`        varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '结果名称，如物流师',
    `result_desc`        text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci          NULL COMMENT '结果描述',
    `result_picture`     varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL     DEFAULT NULL COMMENT '结果图片',
    `result_prop`        varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NULL     DEFAULT NULL COMMENT '结果属性集合 JSON，如 [I,S,T,J]',
    `result_score_range` int(11)                                                        NULL     DEFAULT NULL COMMENT '结果得分范围，如 80，表示 80及以上的分数命中此结果',
    `app_id`             bigint(20)                                                     NOT NULL COMMENT '应用 id',
    `user_id`            bigint(20)                                                     NOT NULL COMMENT '创建用户 id',
    `create_time`        datetime                                                       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`        datetime                                                       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `status`             tinyint(4)                                                     NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_app_id` (`app_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '评分结果表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`
(
    `id`            bigint(20)                                                     NOT NULL AUTO_INCREMENT COMMENT 'id',
    `user_account`  varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '账号',
    `user_password` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '密码',
    `union_id`      varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NULL     DEFAULT NULL COMMENT '微信开放平台id',
    `mp_open_id`    varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NULL     DEFAULT NULL COMMENT '公众号openId',
    `user_name`     varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NULL     DEFAULT NULL COMMENT '用户昵称',
    `user_avatar`   varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL     DEFAULT NULL COMMENT '用户头像',
    `user_profile`  varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NULL     DEFAULT NULL COMMENT '用户简介',
    `user_role`     varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL DEFAULT 'user' COMMENT '用户角色：user/admin/ban',
    `create_time`   datetime                                                       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   datetime                                                       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `status`        tinyint(4)                                                     NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_unionId` (`union_id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '用户表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_answer
-- ----------------------------
DROP TABLE IF EXISTS `user_answer`;
CREATE TABLE `user_answer`
(
    `id`               bigint(20)                                                     NOT NULL AUTO_INCREMENT COMMENT 'id',
    `app_id`           bigint(20)                                                     NOT NULL COMMENT '应用 id',
    `app_type`         tinyint(4)                                                     NOT NULL DEFAULT 0 COMMENT '应用类型（0-得分类，1-角色测评类）',
    `scoring_strategy` tinyint(4)                                                     NOT NULL DEFAULT 0 COMMENT '评分策略（0-自定义，1-AI）',
    `choices`          text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci          NULL COMMENT '用户答案（JSON 数组）',
    `result_id`        bigint(20)                                                     NULL     DEFAULT NULL COMMENT '评分结果 id',
    `result_name`      varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NULL     DEFAULT NULL COMMENT '结果名称，如物流师',
    `result_desc`      text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci          NULL COMMENT '结果描述',
    `result_picture`   varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL     DEFAULT NULL COMMENT '结果图标',
    `result_score`     int(11)                                                        NULL     DEFAULT NULL COMMENT '得分',
    `user_id`          bigint(20)                                                     NOT NULL COMMENT '用户 id',
    `create_time`      datetime                                                       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`      datetime                                                       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `status`           tinyint(4)                                                     NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_app_id` (`app_id`) USING BTREE,
    INDEX `idx_user_id` (`user_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '用户答题记录'
  ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

#管理员密码是123456
INSERT INTO `zhiqiong`.`user` (`id`, `user_account`, `user_password`, `union_id`, `mp_open_id`, `user_name`,
                               `user_avatar`, `user_profile`, `user_role`, `create_time`, `update_time`, `status`)
VALUES (1900160367981613058, 'admin', 'e10adc3949ba59abbe56e057f20f883e', NULL, NULL, '管理员', NULL, NULL, 'admin',
        '2025-03-13 12:21:56', '2025-03-13 12:23:26', 0);
alter table  `app` add index  idx_review_status(review_status);