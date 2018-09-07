package com.lyl.study.cloud.gateway.api.dto.response;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@Accessors(chain = true)
public class PermissionDTO implements Serializable {
    private long id;
    private Long parentId;
    private Short type;
    private String label;
    private String sign;
    private String icon;
    private Integer sort;
    private Boolean enable;
    private Long creatorId;
    private Long ownerId;
    private Long ownerRoleId;
    private Date createTime;
    private Date updateTime;
}
