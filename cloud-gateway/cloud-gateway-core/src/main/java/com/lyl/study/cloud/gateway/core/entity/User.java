package com.lyl.study.cloud.gateway.core.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.annotations.TableName;

import java.io.Serializable;

import lombok.Data;
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
@TableName("sys_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.INPUT)
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String mobile;
    private String email;
    private Date birthday;
    private Boolean sex;
    private String address;
    private Boolean enable;
    private Long creatorId;
    private Long ownerId;
    private Long ownerRoleId;
    private Date createTime;
    private Date updateTime;


    public static final String ID = "id";

    public static final String USERNAME = "username";

    public static final String PASSWORD = "password";

    public static final String NICKNAME = "nickname";

    public static final String MOBILE = "mobile";

    public static final String EMAIL = "email";

    public static final String BIRTHDAY = "birthday";

    public static final String SEX = "sex";

    public static final String ADDRESS = "address";

    public static final String ENABLE = "enable";

    public static final String CREATOR_ID = "creator_id";

    public static final String OWNER_ID = "owner_id";

    public static final String OWNER_ROLE_ID = "owner_role_id";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_TIME = "update_time";

}
