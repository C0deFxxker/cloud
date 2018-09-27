package com.lyl.study.cloud.admin.security;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@ToString
@Accessors(chain = true)
public class JwtClaims implements Serializable {
    // Current session ID
    private String sessionId;
    // Current user ID
    private Long userId;
    // Current role ID
    private Long currentRoleId;
    // Last login time (timestamp)
    private Long loginTime;
}
