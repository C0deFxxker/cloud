package com.lyl.study.cloud.cms.api.facade;

import com.lyl.study.cloud.base.dto.PageInfo;
import com.lyl.study.cloud.base.exception.NoSuchEntityException;
import com.lyl.study.cloud.cms.api.dto.request.ResourceArticleListConditions;
import com.lyl.study.cloud.cms.api.dto.request.ResourceArticleSaveForm;
import com.lyl.study.cloud.cms.api.dto.request.ResourceArticleUpdateForm;
import com.lyl.study.cloud.cms.api.dto.response.ResourceArticleDTO;

public interface ResourceArticleFacade {
    /**
     * 新增图文
     *
     * @param form 表单
     * @return 新增实体ID
     */
    long save(ResourceArticleSaveForm form);

    /**
     * 修改图文
     *
     * @param form 表单
     * @throws NoSuchEntityException 找不到图文
     */
    void update(ResourceArticleUpdateForm form) throws NoSuchEntityException;

    /**
     * 删除图文
     *
     * @param id 图文ID
     * @return 删除记录数
     * @throws NoSuchEntityException 找不到图文
     */
    void deleteById(long id) throws NoSuchEntityException;

    /**
     * 索引图文信息
     *
     * @param id 图文ID
     * @return 返回对应ID的图文信息；找不到图文时，返回null
     */
    ResourceArticleDTO getById(long id);

    /**
     * 图文列表
     *
     * @param conditions 筛选条件
     * @return 图文列表
     */
    PageInfo<ResourceArticleDTO> list(ResourceArticleListConditions conditions);
}