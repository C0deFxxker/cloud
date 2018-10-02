package com.lyl.study.cloud.system.api.facade;

import com.lyl.study.cloud.base.dto.TreeNode;
import com.lyl.study.cloud.base.exception.IllegalOperationException;
import com.lyl.study.cloud.base.exception.NoSuchDependentedEntityException;
import com.lyl.study.cloud.base.exception.NoSuchEntityException;
import com.lyl.study.cloud.system.api.dto.request.OrganizationSaveForm;
import com.lyl.study.cloud.system.api.dto.request.OrganizationUpdateForm;
import com.lyl.study.cloud.system.api.dto.response.OrganizationDTO;

import java.util.List;

/**
 * @author liyilin
 */
public interface OrganizationFacade {
    /**
     * 新增组织
     *
     * @param organizationSaveForm 表单
     * @return 新增的组织信息
     * @throws NoSuchDependentedEntityException 找不到对应parentId的父节点组织时抛出此异常
     */
    long save(OrganizationSaveForm organizationSaveForm) throws NoSuchDependentedEntityException;

    /**
     * 修改组织
     *
     * @param organizationUpdateForm 表单
     * @throws NoSuchEntityException 待修改对象不存在时，抛出此异常
     */
    void update(OrganizationUpdateForm organizationUpdateForm) throws NoSuchEntityException;

    /**
     * 根据ID删除组织
     *
     * @param id 组织ID
     * @return 删除记录数
     * @throws IllegalOperationException 组织下还有子组织或角色时，抛出此异常
     * @throws NoSuchEntityException     找不到组织
     */
    void deleteById(long id) throws NoSuchEntityException, IllegalOperationException;

    /**
     * 根据ID获取组织信息
     *
     * @param id 组织ID
     * @return 若查询成功，返回包含有对应ID组织信息的Result对象；否则，返回描述错误信息的Result对象
     */
    OrganizationDTO getById(long id);


    /**
     * 列出组织树状结构
     *
     * @param id 根节点ID。若为null，则返回所有树
     * @return 若查询成功，则返回含有组织树列表的Result对象；
     * 若指定的根节点ID对应的组织不存在，则返回的Result对象数据字段中仅含有一个空列表。
     * @throws NoSuchEntityException 方法调用指定了根结点ID，但建树过程中查询不到根结点
     */
    List<TreeNode<OrganizationDTO>> listTree(Long id) throws NoSuchEntityException;
}
