package com.lyl.study.cloud.system.api.dto.request;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
@ToString
public class UserChangeMyPasswordForm {
    @NotNull
    @Length(min = 6, max = 20)
    private String password;
}
