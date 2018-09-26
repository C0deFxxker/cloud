package com.lyl.study.cloud.wechat.api.dto.response;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@ToString
@Accessors(chain = true)
public class WxMaterialFileUploadResult {
    private String mediaId;
    private String url;
}
