package com.lyl.study.cloud.admin.web.vip.controller;

import com.lyl.study.cloud.base.CommonErrorCode;
import com.lyl.study.cloud.base.dto.PageInfo;
import com.lyl.study.cloud.base.dto.Result;
import com.lyl.study.cloud.base.exception.InvalidArgumentException;
import com.lyl.study.cloud.vip.api.dto.request.MemberUpdateForm;
import com.lyl.study.cloud.vip.api.dto.response.MemberDTO;
import com.lyl.study.cloud.vip.api.facade.MemberFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vip/member")
public class MemberController {
    @Autowired
    private MemberFacade memberFacade;

    /**
     * 修改会员信息
     */
    @PutMapping
    public Result update(@RequestBody @Validated MemberUpdateForm form) {
        if (form.getId() == null) {
            throw new InvalidArgumentException("必须传入会员ID");
        }
        memberFacade.update(form);
        return new Result<>(CommonErrorCode.OK, "更新成功", null);
    }

    /**
     * 根据ID索引会员
     */
    @GetMapping("/{id}")
    public Result<MemberDTO> getById(@PathVariable("id") long id) {
        MemberDTO member = memberFacade.getById(id);
        return new Result<>(CommonErrorCode.OK, "查询成功", member);
    }

    /**
     * 分页列表
     */
    @GetMapping("/list")
    public Result<PageInfo<MemberDTO>> list(@RequestParam("pageIndex") int pageIndex,
                                            @RequestParam("pageSize") int pageSize) {
        PageInfo<MemberDTO> pageInfo = memberFacade.list(pageIndex, pageSize);
        return new Result<>(CommonErrorCode.OK, "查询成功", pageInfo);
    }
}
