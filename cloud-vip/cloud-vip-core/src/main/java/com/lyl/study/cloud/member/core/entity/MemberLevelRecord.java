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
@TableName("vip_member_level_record")
public class MemberLevelRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.INPUT)
    private Long id;
    private Long memberId;
    private Long oldLevelId;
    private Long newLevelId;
    private Integer sort;
    private BigDecimal requireValue;
    private Date createTime;
    private Date updateTime;


    public static final String ID = "id";

    public static final String MEMBER_ID = "member_id";

    public static final String OLD_LEVEL_ID = "old_level_id";

    public static final String NEW_LEVEL_ID = "new_level_id";

    public static final String SORT = "sort";

    public static final String REQUIRE_VALUE = "require_value";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_TIME = "update_time";

}
