package com.lyl.study.cloud.system.api.dto.response;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@ToString
@Accessors(chain = true)
public class RoleDTO implements Serializable {
    private Long id;
    private String name;
    private String sign;
    private Long organizationId;
    private String organizationName;
    private Boolean enable;
    private List<PermissionItem> permissions = new ArrayList<>();
    private Long creatorId;
    private Long ownerId;
    private Long ownerRoleId;
    private Date createTime;
    private Date updateTime;
}
