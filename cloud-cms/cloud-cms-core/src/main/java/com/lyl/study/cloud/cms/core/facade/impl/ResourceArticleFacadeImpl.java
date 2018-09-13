package com.lyl.study.cloud.cms.core.facade.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.lyl.study.cloud.base.dto.PageInfo;
import com.lyl.study.cloud.base.exception.NoSuchEntityException;
import com.lyl.study.cloud.base.idworker.Sequence;
import com.lyl.study.cloud.cms.api.dto.request.ResourceArticleListConditions;
import com.lyl.study.cloud.cms.api.dto.request.ResourceArticleSaveForm;
import com.lyl.study.cloud.cms.api.dto.request.ResourceArticleUpdateForm;
import com.lyl.study.cloud.cms.api.dto.response.ResourceArticleDTO;
import com.lyl.study.cloud.cms.api.facade.ResourceArticleFacade;
import com.lyl.study.cloud.cms.core.entity.ResourceArticle;
import com.lyl.study.cloud.cms.core.service.ResourceArticleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResourceArticleFacadeImpl implements ResourceArticleFacade {
    @Autowired
    private ResourceArticleService baseService;
    @Autowired
    private Sequence sequence;

    @Override
    public long save(ResourceArticleSaveForm form) {
        ResourceArticle record = new ResourceArticle();
        BeanUtils.copyProperties(form, record);

        record.setId(sequence.nextId());
        record.setCreateTime(null);
        record.setUpdateTime(null);
        baseService.insert(record);

        return record.getId();
    }

    @Override
    public void update(ResourceArticleUpdateForm form) throws NoSuchEntityException {
        ResourceArticle record = baseService.selectById(form.getId());
        if (record == null) {
            throw new NoSuchEntityException("找不到ID为" + form.getId() + "的图文");
        }
        BeanUtils.copyProperties(form, record);
        record.setUpdateTime(null);
        baseService.updateById(record);
    }

    @Override
    public void deleteById(long id) throws NoSuchEntityException {
        // 使用了MyatisPlus的逻辑删除，不能直接通过deleteById方法的返回值判断实体是否存在
        ResourceArticle record = baseService.selectById(id);
        if (record == null) {
            throw new NoSuchEntityException("找不到ID为" + id + "的图文");
        }
        baseService.deleteById(id);
    }

    @Override
    public ResourceArticleDTO getById(long id) {
        ResourceArticle record = baseService.selectById(id);
        if (record != null) {
            ResourceArticleDTO dto = new ResourceArticleDTO();
            BeanUtils.copyProperties(record, dto);
            return dto;
        } else {
            return null;
        }
    }

    @Override
    public PageInfo<ResourceArticleDTO> list(ResourceArticleListConditions conditions) {
        // 准备筛选条件
        EntityWrapper<ResourceArticle> wrapper = new EntityWrapper<>();
        if (conditions.getTitle() != null)
            wrapper.like(ResourceArticle.TITLE, conditions.getTitle());
        if (conditions.getAuthor() != null)
            wrapper.like(ResourceArticle.AUTHOR, conditions.getAuthor());
        if (conditions.getCreateTimeStart() != null)
            wrapper.ge(ResourceArticle.CREATE_TIME, conditions.getCreateTimeStart());
        if (conditions.getCreateTimeEnd() != null)
            wrapper.le(ResourceArticle.CREATE_TIME, conditions.getCreateTimeEnd());

        Page<ResourceArticle> page = new Page<>(conditions.getPageIndex(), conditions.getPageSize());
        page = baseService.selectPage(page, wrapper);

        List<ResourceArticleDTO> dtoList = page.getRecords().stream().map(entity -> {
            ResourceArticleDTO dto = new ResourceArticleDTO();
            BeanUtils.copyProperties(entity, dto);
            return dto;
        }).collect(Collectors.toList());

        return new PageInfo<>(conditions.getPageIndex(), conditions.getPageSize(), page.getTotal(), dtoList);
    }
}