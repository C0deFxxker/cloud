package com.lyl.study.cloud.member.api.dto.response;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@ToString
@Accessors(chain = true)
public class MemberDTO implements Serializable {
    private Long id;
    private String uid;
    private String username;
    private String mobile;
    private String email;
    private Long levelId;
    private Boolean enable;
    private Date createTime;
}