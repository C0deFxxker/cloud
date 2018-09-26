package com.lyl.study.cloud.wechat.api.dto.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class WxMaterialCountResult implements Serializable {
  private static final long serialVersionUID = -5568772662085874138L;

  private int voiceCount;
  private int videoCount;
  private int imageCount;
  private int newsCount;
}

