package com.lyl.study.cloud.security.api.dto.response;

import lombok.AllArgsConstructor;
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
    private Long departmentId;
    private String departmentName;
    private Boolean enable;
    private List<PermissionItem> permissions = new ArrayList<>();
    private Long creatorId;
    private Long ownRoleId;
    private Long ownerId;
    private Date createTime;
    private Date updateTime;

    @Data
    @ToString
    @AllArgsConstructor
    public static class PermissionItem implements Serializable {
        private Long id;
        private String sign;
    }
}
