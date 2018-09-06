package com.lyl.study.cloud.security.api.dto.response;

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
public class UserDetailDTO implements Serializable {
    private long id;
    private String username;
    private String password;
    private String nickname;
    private boolean enable;
    private List<RoleDTO> roles = new ArrayList<>();
    private Date createTime;
    private Date updateTime;
}