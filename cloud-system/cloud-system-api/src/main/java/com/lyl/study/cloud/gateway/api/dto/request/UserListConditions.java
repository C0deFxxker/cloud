package com.lyl.study.cloud.gateway.api.dto.request;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@ToString
@Accessors(chain = true)
public class UserListConditions implements Serializable {
    private Long organizationId;
    private Long roleId;
    private String username;
    private String nickname;
    private String mobile;
    private String email;
    private Date birthdayStart;
    private Date birthdayEnd;
    private Boolean sex;
    private String address;
    private Boolean enable;
    private Integer pageIndex;
    private Integer pageSize;
    private Integer offset;
}
