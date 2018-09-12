package com.lyl.study.cloud.cms.api.facade;

import com.lyl.study.cloud.base.dto.PageInfo;
import com.lyl.study.cloud.cms.api.dto.request.ResourceEntityListConditions;
import com.lyl.study.cloud.cms.api.dto.request.ResourceEntitySaveForm;
import com.lyl.study.cloud.cms.api.dto.response.ResourceEntityDTO;

public interface ResourceEntityFacade {
    PageInfo<ResourceEntityDTO> list(ResourceEntityListConditions conditions);

    long save(ResourceEntitySaveForm form);

    int deleteById(long id);
}
