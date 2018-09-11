package com.lyl.study.cloud.gateway.api.dto.request;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Data
@ToString
public class UserChangePasswordForm {
    @NotBlank
    @Length(min = 6, max = 20)
    private String username;
    @NotNull
    @Length(min = 6, max = 20)
    private String password;
}
