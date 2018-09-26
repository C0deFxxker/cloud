package com.lyl.study.cloud.wechat.api.facade;

import com.lyl.study.cloud.base.dto.PageInfo;
import com.lyl.study.cloud.wechat.api.dto.request.WxMaterialArticleUpdateForm;
import com.lyl.study.cloud.wechat.api.dto.request.WxMaterialNewsForm;
import com.lyl.study.cloud.wechat.api.dto.request.WxMaterialUploadForm;
import com.lyl.study.cloud.wechat.api.dto.response.WxMaterialCountResult;
import com.lyl.study.cloud.wechat.api.dto.response.WxMaterialFileBatchGetNewsItem;
import com.lyl.study.cloud.wechat.api.dto.response.WxMaterialNews;
import com.lyl.study.cloud.wechat.api.dto.response.WxMaterialUploadResult;

public interface WxMaterialFacade {
    /**
     * 新增非图文永久素材
     *
     * @param appId 公众号AppID
     * @param form  上传表单
     * @return 上传资源的URL
     */
    WxMaterialUploadResult materialFileUpload(String appId, WxMaterialUploadForm form);

    /**
     * 新增永久图文素材
     *
     * @param appId 公众号AppID
     * @param form  图文信息
     * @return 上传结果
     */
    WxMaterialUploadResult materialNewsUpload(String appId, WxMaterialNewsForm form);

    /**
     * 获取图文永久素材的信息
     *
     * @param appId   公众号AppID
     * @param mediaId 永久素材的id
     * @return 图文信息
     */
    WxMaterialNews materialNewsInfo(String appId, String mediaId);

    /**
     * 修改图文
     *
     * @param appId                     公众号AppID
     * @param wxMpMaterialArticleUpdate 修改的图文信息
     * @return 是否修改成功
     */
    boolean materialNewsUpdate(String appId, WxMaterialArticleUpdateForm wxMpMaterialArticleUpdate);

    /**
     * 删除素材
     *
     * @param appId   公众号AppID
     * @param mediaId 素材ID
     * @return 是否删除成功
     */
    boolean materialDelete(String appId, String mediaId);

    /**
     * 获取各种素材总数
     *
     * @param appId 公众号AppID
     * @return 各种素材的总数
     */
    WxMaterialCountResult materialCount(String appId);

    /**
     * 分页获取图文信息
     *
     * @param appId     公众号AppID
     * @param pageIndex 页码
     * @param pageSize  页面大小
     */
    PageInfo<WxMaterialNews> materialNewsBatchGet(String appId, int pageIndex, int pageSize);

    /**
     * 分页获取其他媒体素材列表
     *
     * @param appId     公众号AppID
     * @param type      媒体类型
     * @param pageIndex 偏移量
     * @param pageSize  获取数目
     */
    PageInfo<WxMaterialFileBatchGetNewsItem> materialFileBatchGet(String appId, String type,
                                                                  int pageIndex, int pageSize);
}
