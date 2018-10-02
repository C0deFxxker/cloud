package com.lyl.study.cloud.system.api.dto.response;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@ToString
@Accessors(chain = true)
public class OrganizationDTO implements Serializable {
    /**
     * 主键
     */
    private Long id;
    /**
     * 上级组织ID，一级组织为空
     */
    private Long parentId;
    /**
     * 父组织名称
     */
    private String parentName;
    /**
     * 组织名称
     */
    private String name;
    /**
     * 启/停用
     */
    private Boolean enable;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改时间
     */
    private Date updateTime;
}
