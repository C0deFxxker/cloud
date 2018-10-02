package com.lyl.study.cloud.system.core.entity;

import java.util.Date;
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
 * @since 2018-09-07
 */
@Data
@Accessors(chain = true)
@TableName("sys_permission")
public class Permission implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.INPUT)
    private Long id;
    private Long parentId;
    /**
     * 0-1-2-
     */
    private Integer type;
    private String label;
    private String sign;
    private String icon;
    private Integer sort;
    private Boolean enable;
    private Long creatorId;
    private Long ownerId;
    private Long ownerRoleId;
    private Date createTime;
    private Date updateTime;


    public static final String ID = "id";

    public static final String PARENT_ID = "parent_id";

    public static final String TYPE = "type";

    public static final String LABEL = "label";

    public static final String SIGN = "sign";

    public static final String ICON = "icon";

    public static final String SORT = "sort";

    public static final String ENABLE = "enable";

    public static final String CREATOR_ID = "creator_id";

    public static final String OWNER_ID = "owner_id";

    public static final String OWNER_ROLE_ID = "owner_role_id";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_TIME = "update_time";

}
