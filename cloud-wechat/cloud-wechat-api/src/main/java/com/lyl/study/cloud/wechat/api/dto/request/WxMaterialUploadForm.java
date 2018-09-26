package com.lyl.study.cloud.wechat.api.dto.request;

import lombok.Data;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class WxMaterialUploadForm implements Serializable {
  private static final long serialVersionUID = -1651816949780969485L;

  private String mediaType;
  private String name;
  private String filepath;
  private String videoTitle;
  private String videoIntroduction;
}
