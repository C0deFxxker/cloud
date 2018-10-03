package com.lyl.study.cloud.vip.core.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.annotations.TableLogic;
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
 * @since 2018-09-30
 */
@Data
@Accessors(chain = true)
@TableName("vip_member")
public class Member implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.INPUT)
    private Long id;
    private String uid;
    private String username;
    private String mobile;
    private String email;
    private Long levelId;
    private Boolean enable;
    /**
     * 逻辑删除
     */
    @TableLogic
    private Boolean deleted;
    private Date createTime;
    private Date updateTime;


    public static final String ID = "id";

    public static final String UID = "uid";

    public static final String USERNAME = "username";

    public static final String MOBILE = "mobile";

    public static final String EMAIL = "email";

    public static final String LEVEL_ID = "level_id";

    public static final String ENABLE = "enable";

    public static final String DELETED = "deleted";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_TIME = "update_time";

}
