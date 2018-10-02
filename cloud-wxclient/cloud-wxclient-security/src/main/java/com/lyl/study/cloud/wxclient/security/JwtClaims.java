package com.lyl.study.cloud.wxclient.security;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@ToString
@Accessors(chain = true)
public class JwtClaims implements Serializable {
    // Current session ID
    private String sessionId;
    // Current member ID
    private Long memberId;
    // Current member OpenID
    private String openId;
    // Last login time (timestamp)
    private Long loginTime;
}
