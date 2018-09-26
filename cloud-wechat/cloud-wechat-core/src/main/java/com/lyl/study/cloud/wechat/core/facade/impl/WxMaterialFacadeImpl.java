package com.lyl.study.cloud.wechat.core.facade.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.lyl.study.cloud.base.dto.PageInfo;
import com.lyl.study.cloud.wechat.api.dto.request.WxMaterialArticleUpdateForm;
import com.lyl.study.cloud.wechat.api.dto.request.WxMaterialNewsForm;
import com.lyl.study.cloud.wechat.api.dto.request.WxMaterialUploadForm;
import com.lyl.study.cloud.wechat.api.dto.response.WxMaterialCountResult;
import com.lyl.study.cloud.wechat.api.dto.response.WxMaterialFileBatchGetNewsItem;
import com.lyl.study.cloud.wechat.api.dto.response.WxMaterialNews;
import com.lyl.study.cloud.wechat.api.dto.response.WxMaterialUploadResult;
import com.lyl.study.cloud.wechat.api.facade.WxMaterialFacade;
import com.lyl.study.cloud.wechat.core.service.WxMaterialService;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class WxMaterialFacadeImpl implements WxMaterialFacade {
    @Autowired
    private WxMaterialService wxMaterialService;

    @Override
    public WxMaterialUploadResult materialFileUpload(String appId, WxMaterialUploadForm form) {
        return wxMaterialService.materialFileUpload(appId, form);
    }

    @Override
    public WxMaterialUploadResult materialNewsUpload(String appId, WxMaterialNewsForm form) {
        return wxMaterialService.materialNewsUpload(appId, form);
    }

    @Override
    public WxMaterialNews materialNewsInfo(String appId, String mediaId) {
        return wxMaterialService.materialNewsInfo(appId, mediaId);
    }

    @Override
    public boolean materialNewsUpdate(String appId, WxMaterialArticleUpdateForm wxMpMaterialArticleUpdate) {
        return wxMaterialService.materialNewsUpdate(appId, wxMpMaterialArticleUpdate);
    }

    @Override
    public boolean materialDelete(String appId, String mediaId) {
        return wxMaterialService.materialDelete(appId, mediaId);
    }

    @Override
    public WxMaterialCountResult materialCount(String appId) {
        return wxMaterialService.materialCount(appId);
    }

    @Override
    public PageInfo<WxMaterialNews> materialNewsBatchGet(String appId, int pageIndex, int pageSize) {
        return wxMaterialService.materialNewsBatchGet(appId, pageIndex, pageSize);
    }

    @Override
    public PageInfo<WxMaterialFileBatchGetNewsItem> materialFileBatchGet(String appId, String type,
                                                                         int pageIndex, int pageSize) {
        return wxMaterialService.materialFileBatchGet(appId, type, pageIndex, pageSize);
    }
}
