package com.lyl.study.cloud.security.api.dto.request;

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
public class UserUpdateForm implements Serializable {
    private Long id;
    private String nickname;
    private String mobile;
    private String email;
    private Date birthday;
    private Boolean sex;
    private String address;
    private Boolean enable;
    private List<Long> roles = new ArrayList<>();
    private Long ownerId;
    private Long ownerRoleId;
}
