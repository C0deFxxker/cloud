package com.lyl.study.cloud.gateway.api.dto.request;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ToString
@Accessors(chain = true)
public class OrganizationUpdateForm implements Serializable {
    /**
     * 主键，修改时才用到
     */
    @NotNull
    private Long id;
    /**
     * 组织名称
     */
    @NotBlank
    @Length(max = 50)
    private String name;
    /**
     * 描述
     */
    private String description;

}
