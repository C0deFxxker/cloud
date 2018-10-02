package com.lyl.study.cloud.member.core.facade.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.lyl.study.cloud.base.dto.PageInfo;
import com.lyl.study.cloud.base.exception.InvalidArgumentException;
import com.lyl.study.cloud.base.exception.NoSuchEntityException;
import com.lyl.study.cloud.base.idworker.Sequence;
import com.lyl.study.cloud.base.util.BeanUtils;
import com.lyl.study.cloud.vip.api.dto.request.MemberSaveForm;
import com.lyl.study.cloud.vip.api.dto.request.MemberUpdateForm;
import com.lyl.study.cloud.vip.api.dto.response.MemberDTO;
import com.lyl.study.cloud.vip.api.facade.MemberFacade;
import com.lyl.study.cloud.member.core.entity.Level;
import com.lyl.study.cloud.member.core.entity.Member;
import com.lyl.study.cloud.member.core.service.LevelService;
import com.lyl.study.cloud.member.core.service.MemberService;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class MemberFacadeImpl implements MemberFacade {
    private Sequence sequence;
    private MemberService memberService;
    private LevelService levelService;

    @Override
    public long save(MemberSaveForm memberSaveForm) {
        checkCredentialsExistsForSave(memberSaveForm);

        Member member = BeanUtils.transform(memberSaveForm, Member.class);
        member.setUid(UUID.randomUUID().toString().replaceAll("-", ""));
        member.setEnable(true);
        member.setLevelId(getDefaultLevelId());
        member.setId(sequence.nextId());
        memberService.insert(member);
        return member.getId();
    }

    /**
     * 新增操作的属性值重复校验
     *
     * @param memberSaveForm 表单
     */
    private void checkCredentialsExistsForSave(MemberSaveForm memberSaveForm) {
        String username = memberSaveForm.getUsername();
        String mobile = memberSaveForm.getMobile();
        String email = memberSaveForm.getEmail();

        EntityWrapper<Member> wrapper = new EntityWrapper<>();
        wrapper.eq(Member.USERNAME, username);

        if (mobile != null) {
            wrapper.or().eq(Member.MOBILE, mobile);
        }

        if (email != null) {
            wrapper.or().eq(Member.EMAIL, email);
        }

        Member member = memberService.selectOne(wrapper);

        if (member != null) {
            if (Objects.equals(member.getUsername(), username)) {
                throw new InvalidArgumentException("用户名已存在");
            }

            if (mobile != null && Objects.equals(member.getMobile(), mobile)) {
                throw new InvalidArgumentException("手机号已存在");
            }

            if (email != null && Objects.equals(member.getEmail(), email)) {
                throw new InvalidArgumentException("邮箱已存在");
            }
        }
    }

    @Override
    public void update(MemberUpdateForm memberUpdateForm) {
        checkCredentialsExistsForUpdate(memberUpdateForm);

        long id = memberUpdateForm.getId();
        Member member = memberService.selectById(id);
        if (member == null) {
            throw new NoSuchEntityException("找不到ID为" + id + "的会员");
        }


        member.setEmail(memberUpdateForm.getEmail());
        member.setMobile(memberUpdateForm.getMobile());
        memberService.updateById(member);
    }

    /**
     * 更新操作的属性值重复校验
     *
     * @param memberUpdateForm 表单
     */
    private void checkCredentialsExistsForUpdate(MemberUpdateForm memberUpdateForm) {
        long id = memberUpdateForm.getId();
        String mobile = memberUpdateForm.getMobile();
        String email = memberUpdateForm.getEmail();

        EntityWrapper<Member> wrapper = new EntityWrapper<>();
        wrapper.ne(Member.ID, id);

        if (mobile != null) {
            wrapper.or().eq(Member.MOBILE, mobile);
        }

        if (email != null) {
            wrapper.or().eq(Member.EMAIL, email);
        }

        Member member = memberService.selectOne(wrapper);

        if (member != null) {
            if (mobile != null && Objects.equals(member.getMobile(), mobile)) {
                throw new InvalidArgumentException("手机号已存在");
            }

            if (email != null && Objects.equals(member.getEmail(), email)) {
                throw new InvalidArgumentException("邮箱已存在");
            }
        }
    }

    @Override
    public void toggle(long id, boolean enable) {
        Member member = memberService.selectById(id);
        if (member == null) {
            throw new NoSuchEntityException("找不到ID为" + id + "的会员");
        }

        member.setEnable(enable);
        memberService.updateById(member);
    }

    @Override
    public PageInfo<MemberDTO> list(int pageIndex, int pageSize) {
        Page<Member> page = new Page<>(pageIndex, pageSize);
        page = memberService.selectPage(page, new EntityWrapper<>());
        List<MemberDTO> records = page.getRecords().stream()
                // TODO 添加积分和成长值
                .map(a -> BeanUtils.transform(a, MemberDTO.class))
                .collect(Collectors.toList());
        return new PageInfo<>(pageIndex, pageSize, page.getTotal(), records);
    }

    @Override
    public MemberDTO getById(long id) {
        // TODO 添加积分和成长值
        Member member = memberService.selectById(id);
        return BeanUtils.transform(member, MemberDTO.class);
    }

    private long getDefaultLevelId() {
        List<Level> levels = levelService.selectList(
                new EntityWrapper<Level>()
                        .orderBy("sort", true)
        );

        if (levels.isEmpty()) {
            throw new RuntimeException("配置有误, 没有找到等级信息");
        }

        return levels.get(0).getId();
    }
}
