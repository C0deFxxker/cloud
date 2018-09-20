package com.lyl.study.cloud.wechat.api.dto;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@ToString
@Accessors(chain = true)
public class WxJsapiSign {
    private String appId;
    private String nonceStr;
    private long timestamp;
    private String url;
    private String signature;
}
