package com.lyl.study.cloud.security.api.dto.resp;

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
    private List<PermissionItem> permissions = new ArrayList<>();
    private Long creatorId;
    private Long roleId;
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
