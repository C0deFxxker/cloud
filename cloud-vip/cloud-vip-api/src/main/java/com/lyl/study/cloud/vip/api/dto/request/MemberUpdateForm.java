package com.lyl.study.cloud.vip.api.dto.request;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@ToString
@Accessors(chain = true)
public class MemberUpdateForm implements Serializable {
    private long id;
    private String mobile;
    private String email;
}
