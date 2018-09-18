package com.lyl.study.cloud.cms.api.facade;

import com.lyl.study.cloud.base.dto.PageInfo;
import com.lyl.study.cloud.cms.api.dto.request.ArticleMessageListConditions;
import com.lyl.study.cloud.cms.api.dto.request.ArticleMessageSaveForm;
import com.lyl.study.cloud.cms.api.dto.request.ArticleMessageUpdateForm;
import com.lyl.study.cloud.cms.api.dto.response.ArticleMessageDTO;

public interface ArticleMessageFacade {
    /**
     * 发送异步消息
     *
     * @param form 表单
     * @return 异步任务ID
     */
    long sendMessage(ArticleMessageSaveForm form);

    /**
     * 筛选消息列表
     *
     * @return 符合筛选条件的消息列表
     */
    PageInfo<ArticleMessageDTO> list(ArticleMessageListConditions conditions);

    /**
     * 更新（只能更新未发送的消息
     *
     * @param form 表单
     */
    void update(ArticleMessageUpdateForm form);

    /**
     * 撤回消息(消息正在推送期间不能cancel)
     *
     * @param messageId 消息ID
     */
    void cancel(long messageId);

    /**
     * 索引消息
     *
     * @param id 消息ID
     * @return 对应ID的消息
     */
    ArticleMessageDTO getById(long id);
}