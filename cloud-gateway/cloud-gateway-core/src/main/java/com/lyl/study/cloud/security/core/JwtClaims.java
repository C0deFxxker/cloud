package com.lyl.study.cloud.security.core;

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
    private long userId;
    // Current role ID
    private long currentRoleId;
    // Last login time
    private Date loginTime;
}
