package com.lyl.study.cloud.cms.api.dto.request;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

@Data
@ToString
@Accessors(chain = true)
public class ResourceEntitySaveForm implements Serializable {
    @NotNull
    private String mediaType;
    @NotNull
    private String url;
    @NotNull
    private String filepath;
    @NotNull
    private String originalFilename;
    @NotNull
    private Long size;
    private Map<String, Object> properties = Collections.emptyMap();
    private Long creatorId;
    private Long ownerId;
    private Long ownerRoleId;
}
