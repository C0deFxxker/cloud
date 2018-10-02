package com.lyl.study.cloud.member.core.entity;

import java.math.BigDecimal;
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
 * @since 2018-10-02
 */
@Data
@Accessors(chain = true)
@TableName("vip_level")
public class Level implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.INPUT)
    private Long id;
    private String name;
    private String label;
    private Integer sort;
    private BigDecimal requireValue;
    /**
     * 逻辑删除，会员等级流水表需要依赖
     */
    @TableLogic
    private Boolean deleted;
    private Date createTime;
    private Date updateTime;


    public static final String ID = "id";

    public static final String NAME = "name";

    public static final String LABEL = "label";

    public static final String SORT = "sort";

    public static final String REQUIRE_VALUE = "require_value";

    public static final String DELETED = "deleted";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_TIME = "update_time";

}
