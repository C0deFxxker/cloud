package com.lyl.study.cloud.wechat.core.service.impl;

import com.lyl.study.cloud.base.dto.PageInfo;
import com.lyl.study.cloud.base.util.BeanUtils;
import com.lyl.study.cloud.wechat.api.dto.request.WxMaterialArticleUpdateForm;
import com.lyl.study.cloud.wechat.api.dto.request.WxMaterialNewsForm;
import com.lyl.study.cloud.wechat.api.dto.request.WxMaterialUploadForm;
import com.lyl.study.cloud.wechat.api.dto.response.WxMaterialCountResult;
import com.lyl.study.cloud.wechat.api.dto.response.WxMaterialFileBatchGetNewsItem;
import com.lyl.study.cloud.wechat.api.dto.response.WxMaterialNews;
import com.lyl.study.cloud.wechat.api.dto.response.WxMaterialUploadResult;
import com.lyl.study.cloud.wechat.api.exception.WxApiRemoteException;
import com.lyl.study.cloud.wechat.core.service.MultiWxMpService;
import com.lyl.study.cloud.wechat.core.service.WxMaterialService;
import me.chanjar.weixin.common.error.WxMpErrorMsgEnum;
import me.chanjar.weixin.mp.api.WxMpMaterialService;
import me.chanjar.weixin.mp.bean.material.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WxMaterialServiceImpl implements WxMaterialService {
    @Autowired
    private MultiWxMpService multiWxMpService;

    @Override
    public WxMaterialUploadResult materialFileUpload(String appId, WxMaterialUploadForm form) {
        String mediaType = form.getMediaType();
        WxMpMaterial wxMpMaterial = new WxMpMaterial();
        BeanUtils.copyProperties(form, wxMpMaterial);

        return multiWxMpService.run(appId, wxMpService -> {
            WxMpMaterialService materialService = wxMpService.getMaterialService();
            WxMpMaterialUploadResult result = materialService.materialFileUpload(mediaType, wxMpMaterial);
            if (result.getErrCode() != WxMpErrorMsgEnum.CODE_0.getCode()) {
                throw new WxApiRemoteException(result.getErrMsg());
            }

            WxMaterialUploadResult dto = new WxMaterialUploadResult();
            BeanUtils.copyProperties(result, dto);
            return dto;
        });
    }

    @Override
    public WxMaterialUploadResult materialNewsUpload(String appId, WxMaterialNewsForm form) {
        WxMpMaterialNews news = new WxMpMaterialNews();
        BeanUtils.copyProperties(form, news);

        return multiWxMpService.run(appId, wxMpService -> {
            WxMpMaterialService materialService = wxMpService.getMaterialService();
            WxMpMaterialUploadResult result = materialService.materialNewsUpload(news);
            if (result.getErrCode() != WxMpErrorMsgEnum.CODE_0.getCode()) {
                throw new WxApiRemoteException(result.getErrMsg());
            }

            WxMaterialUploadResult dto = new WxMaterialUploadResult();
            BeanUtils.copyProperties(result, dto);
            return dto;
        });
    }

    @Override
    public WxMaterialNews materialNewsInfo(String appId, String mediaId) {
        return multiWxMpService.run(appId, wxMpService -> {
            WxMpMaterialService materialService = wxMpService.getMaterialService();
            WxMpMaterialNews news = materialService.materialNewsInfo(mediaId);

            WxMaterialNews dto = new WxMaterialNews();
            BeanUtils.copyProperties(news, dto);
            return dto;
        });
    }

    @Override
    public boolean materialNewsUpdate(String appId, WxMaterialArticleUpdateForm form) {
        WxMpMaterialArticleUpdate update = BeanUtils.transform(form, WxMpMaterialArticleUpdate.class);

        return multiWxMpService.run(appId, wxMpService -> {
            WxMpMaterialService materialService = wxMpService.getMaterialService();
            return materialService.materialNewsUpdate(update);
        });
    }

    @Override
    public boolean materialDelete(String appId, String mediaId) {
        return multiWxMpService.run(appId, wxMpService -> {
            WxMpMaterialService materialService = wxMpService.getMaterialService();
            return materialService.materialDelete(mediaId);
        });
    }

    @Override
    public WxMaterialCountResult materialCount(String appId) {
        return multiWxMpService.run(appId, wxMpService -> {
            WxMpMaterialService materialService = wxMpService.getMaterialService();
            WxMpMaterialCountResult result = materialService.materialCount();
            return BeanUtils.transform(result, WxMaterialCountResult.class);
        });
    }

    @Override
    public PageInfo<WxMaterialNews> materialNewsBatchGet(String appId, int pageIndex, int pageSize) {
        int offset = (pageIndex - 1) * pageSize;

        return multiWxMpService.run(appId, wxMpService -> {
            WxMpMaterialService materialService = wxMpService.getMaterialService();
            WxMpMaterialNewsBatchGetResult result = materialService.materialNewsBatchGet(offset, pageSize);

            List<WxMaterialNews> records = result.getItems().stream()
                    .map(entity -> BeanUtils.transform(entity, WxMaterialNews.class))
                    .collect(Collectors.toList());
            return new PageInfo<>(pageIndex, pageSize, result.getTotalCount(), records);
        });
    }

    @Override
    public PageInfo<WxMaterialFileBatchGetNewsItem> materialFileBatchGet(String appId, String type, int pageIndex, int pageSize) {
        int offset = (pageIndex - 1) * pageSize;

        return multiWxMpService.run(appId, wxMpService -> {
            WxMpMaterialService materialService = wxMpService.getMaterialService();
            WxMpMaterialFileBatchGetResult result = materialService.materialFileBatchGet(type, offset, pageSize);

            List<WxMaterialFileBatchGetNewsItem> records = result.getItems().stream()
                    .map(entity -> BeanUtils.transform(entity, WxMaterialFileBatchGetNewsItem.class))
                    .collect(Collectors.toList());
            return new PageInfo<>(pageIndex, pageSize, result.getTotalCount(), records);
        });
    }
}
