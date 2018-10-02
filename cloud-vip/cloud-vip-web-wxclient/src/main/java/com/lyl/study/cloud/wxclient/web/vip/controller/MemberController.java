package com.lyl.study.cloud.wxclient.web.vip.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.lyl.study.cloud.base.CommonErrorCode;
import com.lyl.study.cloud.base.dto.Result;
import com.lyl.study.cloud.base.util.BeanUtils;
import com.lyl.study.cloud.vip.api.dto.request.MemberUpdateForm;
import com.lyl.study.cloud.vip.api.dto.response.MemberDTO;
import com.lyl.study.cloud.vip.api.facade.MemberFacade;
import com.lyl.study.cloud.wxclient.security.CurrentSessionHolder;
import com.lyl.study.cloud.wxclient.web.vip.vo.request.CurrentMemberUpdateForm;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
public class MemberController {
    @Reference
    private MemberFacade memberFacade;

    /**
     * 更新个人资料
     */
    @PutMapping
    public Result update(@Validated @RequestBody CurrentMemberUpdateForm form) {
        MemberUpdateForm memberUpdateForm = BeanUtils.transform(form, MemberUpdateForm.class);
        MemberDTO currentUser = CurrentSessionHolder.getCurrentUser();
        memberUpdateForm.setId(currentUser.getId());
        memberFacade.update(memberUpdateForm);
        return new Result<>(CommonErrorCode.OK, "修改成功", null);
    }
}
