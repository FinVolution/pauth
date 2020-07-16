CREATE DATABASE IF NOT EXISTS `pauth` DEFAULT CHARSET utf8 COLLATE utf8_general_ci;

CREATE TABLE IF NOT EXISTS `pauth`.`user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `email` varchar(128) NOT NULL COMMENT '用户邮箱地址',
  `name` varchar(64) NULL COMMENT '用户名',
  `checkcode` varchar(32) NULL COMMENT '用户密码校验码',
  `password` varchar(32) NULL COMMENT '用户密码',
  `roles` varchar(64) NULL COMMENT '用户角色',
  `last_visit_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上一次访问时间',
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `insert_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) NULL COMMENT '最近修改者',
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name_UNIQUE` (`name`),
  UNIQUE INDEX `email_UNIQUE` (`email`),
  KEY `idx_inserttime` (`insert_time`),
  KEY `idx_updatetime` (`update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户表';

CREATE TABLE IF NOT EXISTS `pauth`.`user_security_action`(
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '安全动作ID',
  `type` varchar(64) NOT NULL COMMENT '安全动作类型',
  `once_flag` varchar(64) NOT NULL COMMENT '一次性安全标记',
  `user_id` bigint(20) NULL COMMENT '用户ID',
  `user_name` varchar(64) NULL COMMENT '用户名',
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `insert_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) NULL COMMENT '最近修改者',
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `FLAG_UNIQUE` (`once_flag`),
  KEY `idx_inserttime` (`insert_time`),
  KEY `idx_updatetime` (`update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='数据库表主键ID序列';

CREATE TABLE IF NOT EXISTS `pauth`.`audit_log`(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(32) DEFAULT NULL COMMENT '发送请求者',
  `client_ip` varchar(32) DEFAULT NULL,
  `http_method` varchar(64) DEFAULT NULL COMMENT '请求方法：GET/POST/PUT/DELETE',
  `http_uri` varchar(256) DEFAULT NULL COMMENT '请求URI',
  `class_method` varchar(128) DEFAULT NULL COMMENT '调用方法',
  `class_method_args` varchar(1024) DEFAULT NULL COMMENT '调用方法参数',
  `class_method_return` varchar(1024) DEFAULT NULL COMMENT '调用方法返回值',
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '插入时间',
  `insert_by` varchar(64) DEFAULT NULL,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  INDEX `idx_inserttime` (`insert_time`),
  INDEX `idx_updatetime` (`update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='操作日志表';

CREATE TABLE IF NOT EXISTS `pauth`.`authorization_code` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '授权码主键ID',
  `code` varchar(256) COMMENT '授权码',
  `auth_holder_id` BIGINT COMMENT '授权持有者ID',
  `expiration` TIMESTAMP NULL COMMENT '失效时间',
  `user_id` BIGINT COMMENT '用户ID',
  `client_id` varchar(128) NOT NULL COMMENT '颁发给应用开发的Client ID',
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `insert_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) NULL COMMENT '最近修改者',
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='';

CREATE TABLE IF NOT EXISTS `pauth`.`authentication_holder` (
  `id` bigint(20) AUTO_INCREMENT PRIMARY KEY COMMENT '授权持有者主键ID',
  `user_id` BIGINT COMMENT '用户ID',
  `approved` BOOLEAN COMMENT '是否同意',
  `redirect_uri` varchar(1024) COMMENT '重定向URL',
  `client_id` varchar(128) NOT NULL COMMENT '颁发给应用开发的Client ID',
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `insert_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) NULL COMMENT '最近修改者',
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='授权表';

CREATE TABLE IF NOT EXISTS `pauth`.`access_token` (
  `id` bigint(20) AUTO_INCREMENT PRIMARY KEY COMMENT '访问令牌ID',
  `token_value` varchar(1024) COMMENT '令牌',
  `expiration` TIMESTAMP NULL COMMENT '失效时间',
  `token_type` varchar(64) COMMENT '令牌类型',
  `refresh_token_id` BIGINT COMMENT '刷新令牌ID',
  `auth_holder_id` BIGINT COMMENT '授权持有者ID',
  `user_id` BIGINT COMMENT '用户ID',
  `client_id` varchar(128) NOT NULL COMMENT '颁发给应用开发的Client ID',
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `insert_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) NULL COMMENT '最近修改者',
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='访问令牌表';

CREATE TABLE IF NOT EXISTS `pauth`.`refresh_token` (
  `id` bigint(20) AUTO_INCREMENT PRIMARY KEY COMMENT '刷新令牌主键',
  `token_value` varchar(1024) COMMENT '令牌',
  `expiration` timestamp NULL COMMENT '失效时间',
  `auth_holder_id` BIGINT COMMENT '授权持有者ID',
  `user_id` BIGINT COMMENT '用户ID',
  `client_id` varchar(128) NOT NULL COMMENT '颁发给应用开发的Client ID',
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `insert_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) NULL COMMENT '最近修改者',
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='刷新令牌表';

CREATE TABLE IF NOT EXISTS `pauth`.`client` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Client ID',
  `description` varchar(1024) COMMENT '描述',
  `reuse_refresh_tokens` BOOLEAN DEFAULT FALSE NOT NULL COMMENT '是否可以重复使用刷新令牌',
  `client_id` varchar(128) NOT NULL COMMENT '颁发给应用开发的Client ID',
  `client_secret` varchar(128) NOT NULL COMMENT '颁发给应用开发的Client Secret',
  `basic_auth` varchar(256) NOT NULL COMMENT '颁发给应用开发的Basic Auth',
  `redirect_url` varchar(1024) NOT NULL COMMENT '应用Client的重定向Url',
  `owner_id` BIGINT NOT NULL COMMENT '所有者ID（用户ID）',
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `insert_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) NULL COMMENT '最近修改者',
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '逻辑删除',
  UNIQUE INDEX `client_UNIQUE` (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='应用Client表';

CREATE TABLE IF NOT EXISTS `pauth`.`approved_site` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  `user_name` varchar(128) NOT NULL COMMENT '登录通过的用户名或者用户邮箱',
  `client_id` varchar(128) NOT NULL COMMENT '申请访问的应用Client ID',
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `insert_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) NULL COMMENT '最近修改者',
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户登录通过的站点表';

CREATE TABLE IF NOT EXISTS `pauth`.`scope` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  `name` varchar(128) NOT NULL  COMMENT '访问域Scope名字',
  `description` varchar(1024) NULL COMMENT '描述',
  `is_default` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否为默认Scope',
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `insert_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) NULL COMMENT '最近修改者',
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='访问域Scope表';

CREATE TABLE IF NOT EXISTS `pauth`.`approved_site_scope` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  `approved_site_id` BIGINT NOT NULL COMMENT '用户登录通过站点ID',
  `scope_name` varchar(128) COMMENT '域Scope名字',
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `insert_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) NULL COMMENT '最近修改者',
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='申请的访问域表';

CREATE TABLE IF NOT EXISTS `pauth`.`client_scope` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
  `client_id` varchar(128) NOT NULL COMMENT '颁发给应用开发的Client ID',
  `scope_name` varchar(128) NOT NULL COMMENT '域Scope名字',
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `insert_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) NULL COMMENT '最近修改者',
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='应用Client拥有的访问域表';