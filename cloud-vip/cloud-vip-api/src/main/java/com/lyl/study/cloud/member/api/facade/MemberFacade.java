package com.lyl.study.cloud.member.api.facade;

import com.lyl.study.cloud.member.api.dto.request.MemberSaveForm;
import com.lyl.study.cloud.member.api.dto.request.MemberUpdateForm;

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
}
