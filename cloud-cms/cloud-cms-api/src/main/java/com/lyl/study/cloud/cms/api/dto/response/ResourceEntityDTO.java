package com.lyl.study.cloud.cms.api.dto.response;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author liyilin
 * @since 2018-09-13
 */
@Data
@ToString
@Accessors(chain = true)
public class ResourceEntityDTO implements Serializable {
    private Long id;
    private String mediaType;
    private String url;
    private String filepath;
    private String originalFilename;
    private Long size;
    private Long creatorId;
    private Long ownerId;
    private Long ownerRoleId;
    private Date createTime;
    private Date updateTime;
}
