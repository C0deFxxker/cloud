package com.lyl.study.cloud.vip.api.dto.request;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;

@Data
@ToString
@Accessors(chain = true)
public class MemberUpdateForm implements Serializable {
    @NotNull
    private Long id;
    @Pattern(regexp = "\\d{11}")
    private String mobile;
    @Length(max = 255)
    private String email;
    @NotNull
    private Date birthday;
    @NotNull
    private String name;
    @Length(max = 255)
    private String address;
}
