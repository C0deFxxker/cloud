package com.lyl.study.cloud.cms.core.facade.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.lyl.study.cloud.base.dto.PageInfo;
import com.lyl.study.cloud.base.exception.IllegalOperationException;
import com.lyl.study.cloud.base.exception.InvalidArgumentException;
import com.lyl.study.cloud.base.exception.NoSuchEntityException;
import com.lyl.study.cloud.base.idworker.Sequence;
import com.lyl.study.cloud.base.util.BeanUtils;
import com.lyl.study.cloud.base.util.JsonUtils;
import com.lyl.study.cloud.cms.api.dto.request.ArticleMessageListConditions;
import com.lyl.study.cloud.cms.api.dto.request.ArticleMessageSaveForm;
import com.lyl.study.cloud.cms.api.dto.request.ArticleMessageUpdateForm;
import com.lyl.study.cloud.cms.api.dto.response.ArticleMessageDTO;
import com.lyl.study.cloud.cms.api.facade.ArticleMessageFacade;
import com.lyl.study.cloud.cms.core.entity.ArticleMessage;
import com.lyl.study.cloud.cms.core.entity.ResourceArticle;
import com.lyl.study.cloud.cms.core.service.ArticleMessageService;
import com.lyl.study.cloud.cms.core.service.ResourceArticleService;
import com.lyl.study.cloud.system.api.dto.request.UserListConditions;
import com.lyl.study.cloud.system.api.dto.response.UserDTO;
import com.lyl.study.cloud.system.api.facade.UserFacade;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.lyl.study.cloud.cms.api.CmsErrorCode.MESSAGE_HAS_BEEN_SENDED;

@Service
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
        ArticleMessage articleMessage = BeanUtils.transform(resourceArticle, ArticleMessage.class);
        articleMessage.setConditions(JsonUtils.toJson(form.getConditions()));
        articleMessage.setId(sequence.nextId());
        articleMessage.setEnableTime(form.getSendTime());
        articleMessage.setEnable(true);
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
        wrapper.like(conditions.getTitle() != null, ArticleMessage.TITLE, conditions.getTitle());
        wrapper.like(conditions.getAuthor() != null, ArticleMessage.AUTHOR, conditions.getAuthor());
        wrapper.eq(conditions.getEnable() != null, ArticleMessage.ENABLE, conditions.getEnable());
        wrapper.ge(conditions.getEnableTimeStart() != null, ArticleMessage.ENABLE_TIME, conditions.getEnableTimeStart());
        wrapper.le(conditions.getEnableTimeEnd() != null, ArticleMessage.ENABLE_TIME, conditions.getEnableTimeEnd());
        wrapper.ge(conditions.getCreateTimeStart() != null, ArticleMessage.CREATE_TIME, conditions.getCreateTimeStart());
        wrapper.le(conditions.getCreateTimeEnd() != null, ArticleMessage.CREATE_TIME, conditions.getCreateTimeEnd());
        wrapper.eq(conditions.getSendState() != null, ArticleMessage.SEND_STATE, conditions.getSendState());

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
        ArticleMessage articleMessage = articleMessageService.selectById(form.getMessageId());
        Date now = new Date();

        if (articleMessage == null) {
            throw new NoSuchEntityException("找不到ID为" + form.getMessageId() + "的消息");
        }
        if (!articleMessage.getEnable()) {
            throw new IllegalOperationException(MESSAGE_HAS_BEEN_SENDED, "消息已撤回");
        }
        if (articleMessage.getEnableTime() == null || articleMessage.getEnableTime().before(now)) {
            throw new IllegalOperationException(MESSAGE_HAS_BEEN_SENDED, "消息已经发送，不能修改");
        }
        if (form.getSendTime() != null && form.getSendTime().before(now)) {
            throw new InvalidArgumentException("发送时间不能早于当前时间");
        }

        articleMessage.setConditions(JsonUtils.toJson(form.getConditions()));
        articleMessage.setEnableTime(form.getSendTime());
        articleMessageService.updateById(articleMessage);

        if (articleMessage.getEnableTime() == null) {
            // TODO 如果发送时间为空，表示立即发送消息
        }
    }

    @Override
    public void cancel(long messageId) {
        ArticleMessage articleMessage = articleMessageService.selectById(messageId);
        if (articleMessage == null) {
            throw new NoSuchEntityException("找不到ID为" + messageId + "的消息");
        }
        if (!articleMessage.getEnable()) {
            throw new IllegalOperationException(MESSAGE_HAS_BEEN_SENDED, "消息已撤回");
        }
        if (articleMessage.getEnableTime() == null || articleMessage.getEnableTime().before(new Date())) {
            throw new IllegalOperationException(MESSAGE_HAS_BEEN_SENDED, "消息已经发送，不能撤销");
        }

        articleMessage.setEnable(false);
        articleMessageService.updateById(articleMessage);
    }

    @Override
    public ArticleMessageDTO getById(long id) {
        ArticleMessage articleMessage = articleMessageService.selectById(id);
        if (articleMessage == null) {
            return BeanUtils.transform(articleMessage, ArticleMessageDTO.class);
        } else {
            return null;
        }
    }
}