package com.lyl.study.cloud.member.core.facade.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.lyl.study.cloud.base.dto.PageInfo;
import com.lyl.study.cloud.base.exception.InvalidArgumentException;
import com.lyl.study.cloud.base.exception.NoSuchEntityException;
import com.lyl.study.cloud.base.idworker.Sequence;
import com.lyl.study.cloud.base.util.BeanUtils;
import com.lyl.study.cloud.member.core.entity.Level;
import com.lyl.study.cloud.member.core.entity.Member;
import com.lyl.study.cloud.member.core.entity.MemberGrow;
import com.lyl.study.cloud.member.core.entity.MemberPoint;
import com.lyl.study.cloud.member.core.service.LevelService;
import com.lyl.study.cloud.member.core.service.MemberGrowService;
import com.lyl.study.cloud.member.core.service.MemberPointService;
import com.lyl.study.cloud.member.core.service.MemberService;
import com.lyl.study.cloud.vip.api.dto.request.MemberSaveForm;
import com.lyl.study.cloud.vip.api.dto.request.MemberUpdateForm;
import com.lyl.study.cloud.vip.api.dto.response.MemberDTO;
import com.lyl.study.cloud.vip.api.facade.MemberFacade;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MemberFacadeImpl implements MemberFacade {
    @Autowired
    private Sequence sequence;
    @Autowired
    private MemberService memberService;
    @Autowired
    private LevelService levelService;
    @Autowired
    private MemberPointService memberPointService;
    @Autowired
    private MemberGrowService memberGrowService;

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
                .map(this::parseDTO)
                .collect(Collectors.toList());
        return new PageInfo<>(pageIndex, pageSize, page.getTotal(), records);
    }

    @Override
    public MemberDTO getById(long id) {
        Member member = memberService.selectById(id);
        return parseDTO(member);
    }

    @Override
    public MemberDTO getByMobile(String mobile) {
        Member member = memberService.selectOne(new EntityWrapper<Member>().eq(Member.MOBILE, mobile));
        return parseDTO(member);
    }

    @Override
    public MemberDTO getByEmail(String email) {
        Member member = memberService.selectOne(new EntityWrapper<Member>().eq(Member.EMAIL, email));
        return parseDTO(member);
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

    private MemberDTO parseDTO(Member record) {
        if (record == null) {
            return null;
        }

        MemberDTO member = BeanUtils.transform(record, MemberDTO.class);
        // 查询剩余成长值
        BigDecimal remainGrow = (BigDecimal) memberGrowService.selectObj(
                new EntityWrapper<MemberGrow>()
                        .eq(MemberGrow.MEMBER_ID, record.getId())
                        .gt(MemberGrow.EXPIRE_TIME, new Date())
                        .setSqlSelect("COALESCE(SUM(`value`), 0)")
        );
        // 查询剩余积分值
        BigDecimal remainPoint = (BigDecimal) memberPointService.selectObj(
                new EntityWrapper<MemberPoint>()
                        .eq(MemberPoint.MEMBER_ID, record.getId())
                        .gt(MemberPoint.EXPIRE_TIME, new Date())
                        .setSqlSelect("COALESCE(SUM(`value`), 0)")
        );
        member.setRemainGrow(remainGrow);
        member.setRemainPoint(remainPoint);
        return member;
    }
}
