package com.lyl.study.cloud.cms.core.facade.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.lyl.study.cloud.base.dto.PageInfo;
import com.lyl.study.cloud.base.exception.InvalidArgumentException;
import com.lyl.study.cloud.base.exception.NoSuchEntityException;
import com.lyl.study.cloud.base.idworker.Sequence;
import com.lyl.study.cloud.cms.api.dto.request.ArticleMessageListConditions;
import com.lyl.study.cloud.cms.api.dto.request.ArticleMessageSaveForm;
import com.lyl.study.cloud.cms.api.dto.request.ArticleMessageUpdateForm;
import com.lyl.study.cloud.cms.api.dto.response.ArticleMessageDTO;
import com.lyl.study.cloud.cms.api.facade.ArticleMessageFacade;
import com.lyl.study.cloud.cms.core.entity.ArticleMessage;
import com.lyl.study.cloud.cms.core.entity.ResourceArticle;
import com.lyl.study.cloud.cms.core.service.ArticleMessageService;
import com.lyl.study.cloud.cms.core.service.ResourceArticleService;
import com.lyl.study.cloud.gateway.api.dto.request.UserListConditions;
import com.lyl.study.cloud.gateway.api.dto.response.UserDTO;
import com.lyl.study.cloud.gateway.api.facade.UserFacade;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ArticleMessageFacadeImpl implements ArticleMessageFacade {
    @Autowired
    private Sequence sequence;
    @Autowired
    private ArticleMessageService articleMessageService;
    @Autowired
    private ResourceArticleService resourceArticleService;
    @Reference
    private UserFacade userFacade;

    @Override
    public long sendMessage(ArticleMessageSaveForm form) {
        Long articleId = form.getArticleId();
        ResourceArticle resourceArticle = resourceArticleService.selectById(articleId);
        if (resourceArticle == null) {
            throw new NoSuchEntityException("找不到ID为" + articleId + "图文资源");
        }
        if (form.getSendTime() != null && form.getSendTime().before(new Date())) {
            throw new InvalidArgumentException("发送时间不能早于当前时间");
        }
        if (form.getConditions() == null) {
            form.setConditions(new UserListConditions());
        }

        // 获取期望触达人数
        form.getConditions().setPageIndex(1).setPageSize(0);
        PageInfo<UserDTO> userPage = userFacade.list(form.getConditions());
        int expectTargetNum = userPage.getTotal();

        // 持久化记录
        ArticleMessage articleMessage = new ArticleMessage();
        BeanUtils.copyProperties(resourceArticle, articleMessage);
        articleMessage.setId(sequence.nextId());
        articleMessage.setEnableTime(form.getSendTime());
        articleMessage.setExpectTargetNum(expectTargetNum);
        articleMessageService.insert(articleMessage);

        if (articleMessage.getEnableTime() == null) {
            // TODO 如果发送时间为空，表示立即发送消息
        }

        return articleMessage.getId();
    }

    @Override
    public PageInfo<ArticleMessageDTO> list(ArticleMessageListConditions conditions) {
        EntityWrapper<ArticleMessage> wrapper = new EntityWrapper<>();
        if (conditions.getTitle() != null) {
            wrapper.like(ArticleMessage.TITLE, conditions.getTitle());
        }
        if (conditions.getAuthor() != null) {
            wrapper.like(ArticleMessage.AUTHOR, conditions.getAuthor());
        }
        if (conditions.getEnable() != null) {
            wrapper.eq(ArticleMessage.ENABLE, conditions.getEnable());
        }
        if (conditions.getEnableTimeStart() != null) {
            wrapper.ge(ArticleMessage.ENABLE_TIME, conditions.getEnableTimeStart());
        }
        if (conditions.getEnableTimeEnd() != null) {
            wrapper.le(ArticleMessage.ENABLE_TIME, conditions.getEnableTimeEnd());
        }
        if (conditions.getCreateTimeStart() != null) {
            wrapper.ge(ArticleMessage.CREATE_TIME, conditions.getCreateTimeStart());
        }
        if (conditions.getCreateTimeEnd() != null) {
            wrapper.le(ArticleMessage.CREATE_TIME, conditions.getCreateTimeEnd());
        }
        if (conditions.getSendState() != null) {
            wrapper.eq(ArticleMessage.SEND_STATE, conditions.getSendState());
        }

        Page<ArticleMessage> page = new Page<>(conditions.getPageIndex(), conditions.getPageSize());
        page = articleMessageService.selectPage(page, wrapper);

        // 转化DTO
        List<ArticleMessageDTO> dtoList = page.getRecords().stream().map(entity -> {
            ArticleMessageDTO dto = new ArticleMessageDTO();
            BeanUtils.copyProperties(entity, dto);
            wirePvUv(dto);
            return dto;
        }).collect(Collectors.toList());

        return new PageInfo<>(page.getCurrent(), page.getSize(), page.getTotal(), dtoList);
    }

    private void wirePvUv(ArticleMessageDTO dto) {
        // TODO 获取总PvUv
    }

    @Override
    public void update(ArticleMessageUpdateForm form) {

    }

    @Override
    public void cancel(long messageId) {

    }

    @Override
    public ArticleMessageDTO getById(long id) {
        return null;
    }
}
