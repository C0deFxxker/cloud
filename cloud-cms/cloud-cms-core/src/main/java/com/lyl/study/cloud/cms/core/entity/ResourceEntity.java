package com.lyl.study.cloud.cms.core.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.Version;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author liyilin
 * @since 2018-09-13
 */
@Data
@Accessors(chain = true)
@TableName("cms_resource_entity")
public class ResourceEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.INPUT)
    private Long id;
    @TableField("mediaType")
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


    public static final String ID = "id";

    public static final String MEDIATYPE = "mediaType";

    public static final String URL = "url";

    public static final String FILEPATH = "filepath";

    public static final String ORIGINAL_FILENAME = "original_filename";

    public static final String SIZE = "size";

    public static final String CREATOR_ID = "creator_id";

    public static final String OWNER_ID = "owner_id";

    public static final String OWNER_ROLE_ID = "owner_role_id";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_TIME = "update_time";

}
