DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
  id BIGINT PRIMARY KEY,
  username VARCHAR(50) NOT NULL,
  password VARCHAR(32) NOT NULL,
  nickname VARCHAR(50),
  mobile VARCHAR(50),
  email VARCHAR(255),
  birthday DATE,
  sex bit(1),
  address VARCHAR(255),
  enable bit(1) NOT NULL,
  creator_id BIGINT,
  owner_id BIGINT,
  owner_role_id BIGINT,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP  ON UPDATE CURRENT_TIMESTAMP
)CHARSET=utf8mb4;

DROP TABLE IF EXISTS sys_organization;
CREATE TABLE sys_organization (
  id BIGINT PRIMARY KEY,
  parent_id BIGINT,
  name VARCHAR(50) NOT NULL,
  description VARCHAR(255),
  enable BIT(1) NOT NULL,
  sort TINYINT NOT NULL DEFAULT 0,
  creator_id BIGINT,
  owner_id BIGINT,
  owner_role_id BIGINT,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP  ON UPDATE CURRENT_TIMESTAMP
)CHARSET=utf8mb4;

DROP TABLE IF EXISTS sys_permission;
CREATE TABLE sys_permission (
  id BIGINT PRIMARY KEY,
  parent_id BIGINT,
  type TINYINT NOT NULL COMMENT '菜单类型：0-目录，1-页面，2-请求',
  label VARCHAR(50) NOT NULL,
  sign VARCHAR(50) COMMENT 'type为1时，此处为页面uri；type为2时，此处为后端接口权限标记',
  icon VARCHAR(255),
  sort TINYINT NOT NULL DEFAULT 0,
  enable BIT(1) NOT NULL,
  creator_id BIGINT,
  owner_id BIGINT,
  owner_role_id BIGINT,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP  ON UPDATE CURRENT_TIMESTAMP
)CHARSET=utf8mb4;

DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
  id BIGINT PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  sign VARCHAR(50) NOT NULL,
  organization_id BIGINT NOT NULL,
  enable BIT(1) NOT NULL,
  creator_id BIGINT,
  owner_id BIGINT,
  owner_role_id BIGINT,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP  ON UPDATE CURRENT_TIMESTAMP
)CHARSET=utf8mb4;

DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (user_id, role_id),
  KEY sys_user_role_key_role_id (role_id)
)CHARSET=utf8mb4;

DROP TABLE IF EXISTS sys_role_permission;
CREATE TABLE sys_role_permission (
  role_id BIGINT NOT NULL,
  permission_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (role_id, permission_id),
  KEY sys_role_permission_key_permission_id (permission_id)
)CHARSET=utf8mb4;