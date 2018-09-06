package com.lyl.study.cloud.security.api.dto.response;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@ToString
@Accessors(chain = true)
public class UserDTO implements Serializable {
    private long id;
    private String username;
    private String password;
    private String nickname;
    private boolean enable;
    private Date createTime;
    private Date updateTime;
}