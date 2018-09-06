package com.lyl.study.cloud.security.api.facade;

import com.lyl.study.cloud.base.dto.TreeNode;
import com.lyl.study.cloud.base.exception.NoSuchDependentedEntityException;
import com.lyl.study.cloud.base.exception.NoSuchEntityException;
import com.lyl.study.cloud.security.api.dto.request.DepartmentSaveForm;
import com.lyl.study.cloud.security.api.dto.request.DepartmentUpdateForm;
import com.lyl.study.cloud.security.api.dto.response.DepartmentDTO;

import java.util.List;

/**
 * @author liyilin
 */
public interface DepartmentFacade {
    /**
     * 新增部门
     *
     * @param departmentSaveForm 表单
     * @return 新增的部门信息
     * @throws NoSuchDependentedEntityException 找不到对应parentId的父节点部门时抛出此异常
     */
    long save(DepartmentSaveForm departmentSaveForm) throws NoSuchDependentedEntityException;

    /**
     * 修改部门
     *
     * @param departmentEditForm 表单
     * @throws NoSuchEntityException 待修改对象不存在时，抛出此异常
     */
    void update(DepartmentUpdateForm departmentEditForm) throws NoSuchEntityException;

    /**
     * 根据ID删除部门
     *
     * @param departmentId 部门ID
     * @return 删除记录数
     */
    int deleteById(Long departmentId);

    /**
     * 根据ID获取部门信息
     *
     * @param departmentId 部门ID
     * @return 若查询成功，返回包含有对应ID部门信息的Result对象；否则，返回描述错误信息的Result对象
     */
    DepartmentDTO getById(Long departmentId);


    /**
     * 列出部门树状结构
     *
     * @param id 根节点ID。若为null，则返回所有树
     * @return 若查询成功，则返回含有部门树列表的Result对象；
     * 若指定的根节点ID对应的部门不存在，则返回的Result对象数据字段中仅含有一个空列表。
     */
    List<TreeNode<DepartmentDTO>> listTree(Long id);
}
