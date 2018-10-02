package com.lyl.study.cloud.vip.api.facade;

import com.lyl.study.cloud.base.dto.PageInfo;
import com.lyl.study.cloud.vip.api.dto.request.MemberSaveForm;
import com.lyl.study.cloud.vip.api.dto.request.MemberUpdateForm;
import com.lyl.study.cloud.vip.api.dto.response.MemberDTO;

public interface MemberFacade {
    /**
     * 新增会员
     *
     * @param memberSaveForm 表单
     * @return 新增会员ID
     */
    long save(MemberSaveForm memberSaveForm);

    /**
     * 修改会员基础信息
     *
     * @param memberUpdateForm 表单
     */
    void update(MemberUpdateForm memberUpdateForm);

    /**
     * 启用/冻结会员
     *
     * @param id     会员ID
     * @param enable 是否启用
     */
    void toggle(long id, boolean enable);

    /**
     * 获取会员列表
     *
     * @param pageIndex 页码
     * @param pageSize  页面大小
     * @return 会员列表
     */
    PageInfo<MemberDTO> list(int pageIndex, int pageSize);

    /**
     * 根据ID获取会员信息
     *
     * @param id 会员ID
     * @return 查询成功时，返回对应的会员信息；不存在时，返回null
     */
    MemberDTO getById(long id);
}
