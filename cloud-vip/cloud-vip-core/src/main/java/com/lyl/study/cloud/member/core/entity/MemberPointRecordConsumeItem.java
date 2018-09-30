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
 * 考虑消耗积分操作的撤回功能，需要按照同样过期时间返还
 * </p>
 *
 * @author liyilin
 * @since 2018-09-30
 */
@Data
@Accessors(chain = true)
@TableName("vip_member_point_record_consume_item")
public class MemberPointRecordConsumeItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.INPUT)
    private Long id;
    private Long memberPointRecordId;
    private BigDecimal value;
    private Date expireTime;
    private Date createTime;


    public static final String ID = "id";

    public static final String MEMBER_POINT_RECORD_ID = "member_point_record_id";

    public static final String VALUE = "value";

    public static final String EXPIRE_TIME = "expire_time";

    public static final String CREATE_TIME = "create_time";

}
