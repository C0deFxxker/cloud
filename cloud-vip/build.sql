DROP TABLE IF EXISTS vip_level;
CREATE TABLE vip_level (
  id BIGINT PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  label VARCHAR(50) NOT NULL,
  sort INT NOT NULL,
  require_value DECIMAL(20, 2) NOT NULL,
  deleted BIT(1) NOT NULL DEFAULT 1 COMMENT '逻辑删除，会员等级流水表需要依赖',
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO vip_level(id, name, label, sort, require_value) VALUES
(1, '青铜会员', 'V1', 1, 0),
(2, '白银会员', 'V2', 2, 0),
(3, '黄金会员', 'V3', 3, 0),
(4, '铂金会员', 'V4', 4, 0),
(5, '钻石会员', 'V5', 5, 0);

DROP TABLE IF EXISTS vip_member;
CREATE TABLE vip_member (
  id BIGINT PRIMARY KEY,
  uid VARCHAR(50) UNIQUE NOT NULL,
  username VARCHAR(20) UNIQUE NOT NULL,
  mobile CHAR(11),
  email VARCHAR(255),
  level_id BIGINT NOT NULL,
  enable BIT(1) NOT NULL DEFAULT 1,
  deleted BIT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS vip_member_level_record;
CREATE TABLE vip_member_level_record (
  id BIGINT PRIMARY KEY,
  member_id BIGINT NOT NULL,
  old_level_id BIGINT NOT NULL,
  new_level_id BIGINT NOT NULL,
  sort INT NOT NULL,
  require_value DECIMAL(20, 2) NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS vip_member_grow;
CREATE TABLE vip_member_grow (
  id BIGINT PRIMARY KEY,
  member_id BIGINT NOT NULL,
  `value` DECIMAL(20, 2) NOT NULL,
  expire_time TIMESTAMP,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS vip_member_grow_record;
CREATE TABLE vip_member_grow_record (
  id BIGINT PRIMARY KEY,
  member_id BIGINT NOT NULL,
  `value` DECIMAL(20, 2) NOT NULL,
  description VARCHAR(255),
  expire_time TIMESTAMP,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS vip_member_point;
CREATE TABLE vip_member_point (
  id BIGINT PRIMARY KEY,
  member_id BIGINT NOT NULL,
  `value` DECIMAL(20, 2) NOT NULL,
  expire_time TIMESTAMP,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS vip_member_point_record;
CREATE TABLE vip_member_point_record (
  id BIGINT PRIMARY KEY,
  member_id BIGINT NOT NULL,
  `value` DECIMAL(20, 2) NOT NULL,
  description VARCHAR(255),
  expire_time TIMESTAMP,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS vip_member_point_consume_record_item;
CREATE TABLE vip_member_point_record_consume_item (
  id BIGINT PRIMARY KEY,
  member_point_record_id BIGINT NOT NULL,
  `value` DECIMAL(20, 2) NOT NULL,
  expire_time TIMESTAMP,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) COMMENT='考虑消耗积分操作的撤回功能，需要按照同样过期时间返还';