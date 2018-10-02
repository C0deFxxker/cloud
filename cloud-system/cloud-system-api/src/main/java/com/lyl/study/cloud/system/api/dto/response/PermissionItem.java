package com.lyl.study.cloud.system.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PermissionItem implements Serializable {
    private Long id;
    private String sign;
    private Integer type;
}