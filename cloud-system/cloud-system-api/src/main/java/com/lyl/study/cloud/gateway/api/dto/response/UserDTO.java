package com.lyl.study.cloud.gateway.api.dto.response;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@ToString
@Accessors(chain = true)
public class UserDTO implements Serializable {
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String mobile;
    private String email;
    private Date birthday;
    private Boolean sex;
    private String address;
    private Boolean enable;
    private Long creatorId;
    private Long ownerId;
    private Long ownerRoleId;
    private Date createTime;
    private Date updateTime;
}