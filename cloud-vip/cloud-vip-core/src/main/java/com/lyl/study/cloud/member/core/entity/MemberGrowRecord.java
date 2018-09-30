package com.lyl.study.cloud.member.core.entity;

import java.math.BigDecimal;
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
 * @since 2018-09-30
 */
@Data
@Accessors(chain = true)
@TableName("vip_member_grow_record")
public class MemberGrowRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.INPUT)
    private Long id;
    private Long memberId;
    private BigDecimal value;
    private String description;
    private Date expireTime;
    private Date createTime;


    public static final String ID = "id";

    public static final String MEMBER_ID = "member_id";

    public static final String VALUE = "value";

    public static final String DESCRIPTION = "description";

    public static final String EXPIRE_TIME = "expire_time";

    public static final String CREATE_TIME = "create_time";

}
