package com.lyl.study.cloud.gateway.core.entity;

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
@TableName("sys_role_department")
public class RoleDepartment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "user_id", type = IdType.INPUT)
    private Long userId;
    private Long departmentId;
    private Date createTime;


    public static final String USER_ID = "user_id";

    public static final String DEPARTMENT_ID = "department_id";

    public static final String CREATE_TIME = "create_time";

}
