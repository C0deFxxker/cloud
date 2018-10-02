package com.lyl.study.cloud.vip.api.dto.request;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@ToString
@Accessors(chain = true)
public class MemberSaveForm implements Serializable {
    private String username;
    private String mobile;
    private String email;
}
