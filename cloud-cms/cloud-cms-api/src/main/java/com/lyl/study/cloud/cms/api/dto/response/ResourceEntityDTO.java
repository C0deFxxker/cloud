package com.lyl.study.cloud.cms.api.dto.response;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@ToString
@Accessors(chain = true)
public class ResourceEntityDTO implements Serializable {
    private Long id;
    private String contentType;
    private String url;
    private String filepath;
    private String originalFilename;
    private String properties;
    private Long creatorId;
    private Long ownerId;
    private Long ownerRoleId;
    private Date createTime;
    private Date updateTime;
}
