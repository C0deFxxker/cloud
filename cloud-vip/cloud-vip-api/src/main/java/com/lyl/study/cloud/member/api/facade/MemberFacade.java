package com.lyl.study.cloud.member.api.facade;

import com.lyl.study.cloud.member.api.dto.request.MemberSaveForm;
import com.lyl.study.cloud.member.api.dto.request.MemberUpdateForm;
import com.lyl.study.cloud.member.api.dto.response.MemberDTO;

public interface MemberFacade {
    /**
     * 新增会员
     *
     * @param memberSaveForm 表单
     * @return 新增会员ID
     */
    long save(MemberSaveForm memberSaveForm);

    /**
     * 修改会员信息
     *
     * @param memberUpdateForm 表单
     */
    void update(MemberUpdateForm memberUpdateForm);

    /**
     * 根据ID获取会员信息
     *
     * @param id 会员ID
     * @return 查询成功时，返回对应的会员信息；不存在时，返回null
     */
    MemberDTO getById(long id);
}
