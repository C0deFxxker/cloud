DROP TABLE IF EXISTS cms_resource_entity;
CREATE TABLE cms_resource_entity (
  id                BIGINT PRIMARY KEY,
  mediaType         VARCHAR(50),
  url               VARCHAR(255) NOT NULL,
  filepath          VARCHAR(255) NOT NULL,
  original_filename VARCHAR(255),
  size              BIGINT       NOT NULL,
  creator_id        BIGINT,
  owner_id          BIGINT,
  owner_role_id     BIGINT,
  create_time       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
  ON UPDATE CURRENT_TIMESTAMP,
  KEY cms_resource_file_index_media_type (mediaType(6))
)
  CHARSET = utf8mb4;

DROP TABLE IF EXISTS cms_resource_article;
CREATE TABLE cms_resource_article (
  id            bigint(20)   NOT NULL
  COMMENT '主键id',
  title         varchar(100) NOT NULL
  COMMENT '主题',
  author        varchar(32)  NOT NULL
  COMMENT '作者名称',
  abstract_text varchar(255) NOT NULL
  COMMENT '文本摘要',
  content       MEDIUMTEXT   NOT NULL
  COMMENT '文章内容',
  surface_url   varchar(255) NOT NULL
  COMMENT '封面图链接url',
  deleted       bit(1)       NOT NULL DEFAULT 0
  COMMENT '逻辑删除字段',
  creator_id    bigint(20)            DEFAULT NULL
  COMMENT '创建者id',
  owner_id      bigint(20)            DEFAULT NULL
  COMMENT '拥有者id，默认为创建者id',
  owner_role_id bigint(20)            DEFAULT NULL
  COMMENT '拥有者角色id',
  create_time   datetime              DEFAULT CURRENT_TIMESTAMP
  COMMENT '创建时间',
  update_time   datetime              DEFAULT CURRENT_TIMESTAMP
  ON UPDATE CURRENT_TIMESTAMP
  COMMENT '修改时间',
  PRIMARY KEY (id)
)
  CHARSET = utf8mb4
  COMMENT '图文资源';

DROP TABLE IF EXISTS cms_article_message;
CREATE TABLE cms_article_message (
  id            bigint(20)   NOT NULL
  COMMENT '主键id',
  article_id BITINT NOT NULL,
  COMMENT '图文ID',
  title         varchar(100) NOT NULL
  COMMENT '主题',
  author        varchar(32)  NOT NULL
  COMMENT '作者名称',
  abstract_text varchar(255) NOT NULL
  COMMENT '文本摘要',
  content       MEDIUMTEXT   NOT NULL
  COMMENT '文章内容',
  surface_url   varchar(255) NOT NULL
  COMMENT '封面图链接url',
  enable        BIT(1)       NOT NULL
  COMMENT '是否启用（用于消息撤回）',
  enable_time   TIMESTAMP    NOT NULL
  COMMENT '启用时间（用于定时发布）',
  creator_id    bigint(20) DEFAULT NULL
  COMMENT '创建者id',
  owner_id      bigint(20) DEFAULT NULL
  COMMENT '拥有者id，默认为创建者id',
  owner_role_id bigint(20) DEFAULT NULL
  COMMENT '拥有者角色id',
  create_time   datetime   DEFAULT CURRENT_TIMESTAMP
  COMMENT '创建时间',
  update_time   datetime   DEFAULT CURRENT_TIMESTAMP
  ON UPDATE CURRENT_TIMESTAMP
  COMMENT '修改时间',
  PRIMARY KEY (id)
)
  CHARSET = utf8mb4
  COMMENT '图文消息（已经发送出去的）';

DROP TABLE IF EXISTS cms_message_user;
CREATE TABLE cms_message_user (
  message_id  BIGINT    NOT NULL,
  user_id     BIGINT    NOT NULL,
  state       BIT(1)    NOT NULL
  COMMENT '是否已读',
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (message_id, user_id),
  KEY select_unread_user_messages (user_id, state)
)
  COMMENT '消息用户关联表';

# DROP TABLE IF EXISTS cms_message_pvuv;
# CREATE TABLE cms_message_pvuv (
#   id BIGINT PRIMARY KEY,
#   message_id BIGINT NOT NULL,
#
# );
