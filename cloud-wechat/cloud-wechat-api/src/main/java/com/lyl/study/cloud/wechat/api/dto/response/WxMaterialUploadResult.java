package com.lyl.study.cloud.wechat.api.dto.response;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@ToString
@Accessors(chain = true)
public class WxMaterialUploadResult implements Serializable {
    private String mediaId;
    private String url;
}
