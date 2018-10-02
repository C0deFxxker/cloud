package com.lyl.study.cloud.system.api.dto.request;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@ToString
@Accessors(chain = true)
public class UserSaveForm implements Serializable {
    @NotBlank
    @Length(min = 6, max = 20)
    private String username;
    @NotBlank
    @Length(min = 6, max = 20)
    private String password;
    @NotBlank
    @Length(max = 20)
    private String nickname;
    @Pattern(regexp = "\\d{11}", message = "手机号格式不正确")
    private String mobile;
    @Length(max = 255)
    private String email;
    private Date birthday;
    private Boolean sex;
    @Length(max = 255)
    private String address;
    private Boolean enable;
    @NotNull
    private List<Long> roles = new ArrayList<>();
    private Long creatorId;
    private Long ownerId;
    private Long ownerRoleId;
}