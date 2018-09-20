package com.lyl.study.cloud.wechat.core.service;

import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.material.*;

import java.io.File;
import java.io.InputStream;

public interface WxMaterialService {
    WxMediaUploadResult mediaUpload(String var1, File var2) throws WxErrorException;

    WxMediaUploadResult mediaUpload(String var1, String var2, InputStream var3) throws WxErrorException;

    File mediaDownload(String var1) throws WxErrorException;

    WxMediaImgUploadResult mediaImgUpload(File var1) throws WxErrorException;

    WxMpMaterialUploadResult materialFileUpload(String var1, WxMpMaterial var2) throws WxErrorException;

    WxMpMaterialUploadResult materialNewsUpload(WxMpMaterialNews var1) throws WxErrorException;

    InputStream materialImageOrVoiceDownload(String var1) throws WxErrorException;

    WxMpMaterialVideoInfoResult materialVideoInfo(String var1) throws WxErrorException;

    WxMpMaterialNews materialNewsInfo(String var1) throws WxErrorException;

    boolean materialNewsUpdate(WxMpMaterialArticleUpdate var1) throws WxErrorException;

    boolean materialDelete(String var1) throws WxErrorException;

    WxMpMaterialCountResult materialCount() throws WxErrorException;

    WxMpMaterialNewsBatchGetResult materialNewsBatchGet(int var1, int var2) throws WxErrorException;

    WxMpMaterialFileBatchGetResult materialFileBatchGet(String var1, int var2, int var3) throws WxErrorException;
}
