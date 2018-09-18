package com.lyl.study.cloud.admin.web.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.lyl.study.cloud.base.dto.PageInfo;
import com.lyl.study.cloud.base.dto.Result;
import com.lyl.study.cloud.cms.api.dto.request.ResourceArticleListConditions;
import com.lyl.study.cloud.cms.api.dto.request.ResourceArticleSaveForm;
import com.lyl.study.cloud.cms.api.dto.request.ResourceArticleUpdateForm;
import com.lyl.study.cloud.cms.api.dto.response.ResourceArticleDTO;
import com.lyl.study.cloud.cms.api.facade.ResourceArticleFacade;
import com.lyl.study.cloud.gateway.api.dto.response.RoleDTO;
import com.lyl.study.cloud.gateway.api.dto.response.UserDetailDTO;
import com.lyl.study.cloud.gateway.security.CurrentSessionHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.lyl.study.cloud.base.CommonErrorCode.NOT_FOUND;
import static com.lyl.study.cloud.cms.api.CmsErrorCode.OK;

@RestController
@RequestMapping("/resourceArticle")
public class ResourceArticleController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Reference
    private ResourceArticleFacade resourceArticleFacade;

    /**
     * 新增图文
     *
     * @param form 表单
     * @return 新增实体ID
     */
    @PostMapping
    public Result<Long> save(@RequestBody @Validated ResourceArticleSaveForm form) {
        UserDetailDTO user = CurrentSessionHolder.getCurrentUser();
        RoleDTO currentRole = CurrentSessionHolder.getCurrentRole();
        form.setCreatorId(user.getId());
        form.setOwnerId(user.getId());
        form.setOwnerRoleId(currentRole.getId());

        long id = resourceArticleFacade.save(form);
        return new Result<>(OK, "新增成功", id);
    }

    /**
     * 修改图文
     *
     * @param form 表单
     */
    @PutMapping
    public Result update(@RequestBody @Validated ResourceArticleUpdateForm form) {
        resourceArticleFacade.update(form);
        return new Result<>(OK, "更新成功", null);
    }

    /**
     * 删除图文
     *
     * @param id 图文ID
     * @return 删除记录数
     */
    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable("id") Long id) {
        resourceArticleFacade.deleteById(id);
        return new Result<>(OK, "删除成功", null);
    }

    /**
     * 索引图文信息
     *
     * @param id 图文ID
     * @return 返回对应ID的图文信息；找不到图文时，返回null
     */
    @GetMapping("/{id}")
    public Result<ResourceArticleDTO> getById(@PathVariable("id") Long id) {
        ResourceArticleDTO dto = resourceArticleFacade.getById(id);
        if (dto != null) {
            return new Result<>(OK, "查询成功", dto);
        } else {
            return new Result<>(NOT_FOUND, "找不到ID为" + id + "的图文", null);
        }
    }

    /**
     * 图文列表
     *
     * @param conditions 筛选条件
     * @return 图文列表
     */
    @GetMapping("/list")
    public Result<PageInfo<ResourceArticleDTO>> list(ResourceArticleListConditions conditions) {
        PageInfo<ResourceArticleDTO> pageInfo = resourceArticleFacade.list(conditions);
        return new Result<>(OK, "查询成功", pageInfo);
    }
}
