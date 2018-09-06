package com.lyl.study.cloud.security.api.dto.response;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@Accessors(chain = true)
public class PermissionDTO implements Serializable {
    private long id;
    private String appKey;
    private Long parentId;
    private Integer type;
    private String label;
    private String sign;
    private String icon;
    private Integer sort;
    private boolean visible;
    private Boolean enable;
    private boolean deleted;
    private Long creatorId;
    private Long ownerId;
    private Long ownerRoleId;
    private Date createTime;
    private Date updateTime;
}
