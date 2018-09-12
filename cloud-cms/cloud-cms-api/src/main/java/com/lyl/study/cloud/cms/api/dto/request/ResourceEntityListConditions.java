package com.lyl.study.cloud.cms.api.dto.request;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@ToString
@Accessors(chain = true)
public class ResourceEntityListConditions implements Serializable {
    private String mediaType;
    private Integer pageIndex;
    private Integer pageSize;
}
