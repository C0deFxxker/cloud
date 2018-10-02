package com.lyl.study.cloud.vip.api.dto.response;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@ToString
@Accessors(chain = true)
public class MemberDTO implements Serializable {
    private Long id;
    private String uid;
    private String mobile;
    private String email;
    private String openId;
    private Long levelId;
    private BigDecimal remainPoint;
    private BigDecimal remainGrow;
    private Boolean enable;
    private Date createTime;
}