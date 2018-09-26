package com.lyl.study.cloud.wechat.api.dto.response;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@ToString
@Accessors(chain = true)
public class WxUserDTO {
    private Boolean subscribe;
    private String openId;
    private String nickname;
    private String sexDesc;
    private Integer sex;
    private String language;
    private String city;
    private String province;
    private String country;
    private String headImgUrl;
    private Long subscribeTime;
    private String unionId;
    private String remark;
    private Integer groupId;
    private Long[] tagIds;
    private String[] privileges;
    private String subscribeScene;
    private String qrScene;
    private String qrSceneStr;
}
