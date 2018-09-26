package com.lyl.study.cloud.wechat.api.dto.request;

import com.lyl.study.cloud.wechat.api.dto.response.WxMaterialNews;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@ToString
@Accessors(chain = true)
public class WxMaterialArticleUpdateForm implements Serializable {
    private String mediaId;
    private int index;
    private WxMaterialNews.WxMpMaterialNewsArticle articles;
}
