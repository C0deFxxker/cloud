package com.lyl.study.cloud.security.api.dto.response;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@ToString
@Accessors(chain = true)
public class DepartmentDTO implements Serializable {
    /**
     * 主键
     */
    private Long id;
    /**
     * 上级部门ID，一级部门为空
     */
    private Long parentId;
    /**
     * 父部门名称
     */
    private String parentName;
    /**
     * 部门名称
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
