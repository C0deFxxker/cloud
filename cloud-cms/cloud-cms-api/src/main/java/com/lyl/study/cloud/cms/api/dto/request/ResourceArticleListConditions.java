package com.lyl.study.cloud.cms.api.dto.request;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author liyilin
 */
@Data
@ToString
@Accessors(chain = true)
public class ResourceArticleListConditions implements Serializable {
    /**
     * 主题
     */
    @Length(max = 100)
    private String title;
    /**
     * 作者名称
     */
    @Length(max = 32)
    private String author;
    /**
     * 筛选创建时间段起始值
     */
    private Date createTimeStart;
    /**
     * 筛选创建时间段结束值
     */
    private Date createTimeEnd;
    /**
     * 页码
     */
    private Integer pageIndex = 1;
    /**
     * 页面大小
     */
    private Integer pageSize = 10;
}
