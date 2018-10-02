package com.lyl.study.cloud.cms.api.dto.request;

import com.lyl.study.cloud.system.api.dto.request.UserListConditions;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@ToString
@Accessors(chain = true)
public class ArticleMessageUpdateForm {
    private Long messageId;
    private UserListConditions conditions;
    private Date sendTime;
}
