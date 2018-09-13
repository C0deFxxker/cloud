package com.lyl.study.cloud.cms.core.facade.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.enums.SqlLike;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.lyl.study.cloud.base.dto.PageInfo;
import com.lyl.study.cloud.base.idworker.Sequence;
import com.lyl.study.cloud.cms.api.CmsConstants;
import com.lyl.study.cloud.cms.api.dto.request.ResourceEntityListConditions;
import com.lyl.study.cloud.cms.api.dto.request.ResourceEntitySaveForm;
import com.lyl.study.cloud.cms.api.dto.response.ResourceEntityDTO;
import com.lyl.study.cloud.cms.api.facade.ResourceEntityFacade;
import com.lyl.study.cloud.cms.core.entity.ResourceEntity;
import com.lyl.study.cloud.cms.core.service.ResourceEntityService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResourceEntityFacadeImpl implements ResourceEntityFacade {
    @Autowired
    private ResourceEntityService resourceEntityService;
    @Autowired
    private Sequence sequence;

    @Override
    public PageInfo<ResourceEntityDTO> list(ResourceEntityListConditions conditions) {
        Assert.notNull(conditions.getPageIndex(), "pageIndex cannot be null");
        Assert.notNull(conditions.getPageSize(), "pageSize cannot be null");

        // 处理筛选条件
        EntityWrapper<ResourceEntity> wrapper = new EntityWrapper<>();
        if (conditions.getMediaType() != null) {
            String mediaTypeValue = CmsConstants.getMediaTypeByIdx(conditions.getMediaType());
            if (mediaTypeValue != null) {
                mediaTypeValue = mediaTypeValue.substring(0, mediaTypeValue.length() - 1);
            }
            wrapper.like(ResourceEntity.MEDIATYPE, mediaTypeValue, SqlLike.RIGHT);
        }
        wrapper.orderBy(ResourceEntity.ID, false);

        Page<ResourceEntity> page = new Page<>(conditions.getPageIndex(), conditions.getPageSize());
        page = resourceEntityService.selectPage(page, wrapper);

        // 转化DTO
        List<ResourceEntityDTO> dtoList = page.getRecords().stream()
                .map(entity -> {
                    ResourceEntityDTO dto = new ResourceEntityDTO();
                    BeanUtils.copyProperties(entity, dto);
                    return dto;
                }).collect(Collectors.toList());
        return new PageInfo<>(conditions.getPageIndex(), conditions.getPageSize(), page.getTotal(), dtoList);
    }

    @Override
    public long save(ResourceEntitySaveForm form) {
        ResourceEntity record = new ResourceEntity();
        BeanUtils.copyProperties(form, record);
//        if (form.getProperties() != null) {
//            record.setProperties(JsonUtils.toJson(form));
//        }
        record.setId(sequence.nextId());
        resourceEntityService.insert(record);
        return record.getId();
    }

    @Override
    public int deleteById(long id) {
        return resourceEntityService.deleteById(id) ? 1 : 0;
    }

    @Override
    public ResourceEntityDTO getById(long id) {
        ResourceEntity resourceEntity = resourceEntityService.selectById(id);
        if (resourceEntity != null) {
            ResourceEntityDTO dto = new ResourceEntityDTO();
            BeanUtils.copyProperties(resourceEntity, dto);
            return dto;
        }
        return null;
    }
}
