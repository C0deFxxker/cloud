package com.lyl.study.cloud.wechat.core.rest.api;

import com.lyl.study.cloud.base.dto.PageInfo;
import com.lyl.study.cloud.base.dto.Result;
import com.lyl.study.cloud.wechat.api.dto.request.WxMaterialArticleUpdateForm;
import com.lyl.study.cloud.wechat.api.dto.request.WxMaterialNewsForm;
import com.lyl.study.cloud.wechat.api.dto.request.WxMaterialUploadForm;
import com.lyl.study.cloud.wechat.api.dto.response.WxMaterialCountResult;
import com.lyl.study.cloud.wechat.api.dto.response.WxMaterialFileBatchGetNewsItem;
import com.lyl.study.cloud.wechat.api.dto.response.WxMaterialNews;
import com.lyl.study.cloud.wechat.api.dto.response.WxMaterialUploadResult;
import com.lyl.study.cloud.wechat.core.service.WxMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.lyl.study.cloud.wechat.api.ErrorCode.OK;
import static com.lyl.study.cloud.wechat.api.ErrorCode.WX_ERROR;

@RestController
@RequestMapping("/{appId}/material")
public class WxMaterialController {
    @Autowired
    private WxMaterialService wxMaterialService;

    @PostMapping("/file")
    public Result<WxMaterialUploadResult> materialFileUpload(String appId, WxMaterialUploadForm form) {
        return new Result<>(OK, "上传成功", wxMaterialService.materialFileUpload(appId, form));
    }

    @PostMapping("/news")
    public Result<WxMaterialUploadResult> materialNewsUpload(String appId, WxMaterialNewsForm form) {
        return new Result<>(OK, "上传成功", wxMaterialService.materialNewsUpload(appId, form));
    }

    @GetMapping("/news/{mediaId}")
    public Result<WxMaterialNews> materialNewsInfo(String appId, String mediaId) {
        return new Result<>(OK, "获取成功", wxMaterialService.materialNewsInfo(appId, mediaId));
    }

    @PostMapping("/news/{mediaId}")
    public Result materialNewsUpdate(String appId, WxMaterialArticleUpdateForm wxMpMaterialArticleUpdate) {
        if (wxMaterialService.materialNewsUpdate(appId, wxMpMaterialArticleUpdate)) {
            return new Result<>(OK, "修改成功", null);
        } else {
            return new Result<>(WX_ERROR, "修改失败", null);
        }
    }

    @DeleteMapping("/{mediaId}")
    public Result materialDelete(String appId, String mediaId) {
        if (wxMaterialService.materialDelete(appId, mediaId)) {
            return new Result<>(OK, "删除成功", null);
        } else {
            return new Result<>(WX_ERROR, "删除失败", null);
        }
    }

    @GetMapping("/count")
    public Result<WxMaterialCountResult> materialCount(String appId) {
        return new Result<>(OK, "查询成功", wxMaterialService.materialCount(appId));
    }

    @GetMapping("/news/batchGet")
    public Result<PageInfo<WxMaterialNews>> materialNewsBatchGet(String appId, int pageIndex, int pageSize) {
        PageInfo<WxMaterialNews> pageInfo = wxMaterialService.materialNewsBatchGet(appId, pageIndex, pageSize);
        return new Result<>(OK, "查询成功", pageInfo);
    }

    @GetMapping("/file/batchGet")
    public Result<PageInfo<WxMaterialFileBatchGetNewsItem>> materialFileBatchGet(String appId,
                                                                                 String type,
                                                                                 @RequestParam("pageIndex") int pageIndex,
                                                                                 @RequestParam("pageSize") int pageSize) {
        PageInfo<WxMaterialFileBatchGetNewsItem> pageInfo
                = wxMaterialService.materialFileBatchGet(appId, type, pageIndex, pageSize);
        return new Result<>(OK, "查询成功", pageInfo);
    }
}