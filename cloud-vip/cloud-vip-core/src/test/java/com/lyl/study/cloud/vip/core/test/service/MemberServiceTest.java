package com.lyl.study.cloud.vip.core.test.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.lyl.study.cloud.member.core.entity.Member;
import com.lyl.study.cloud.member.core.service.MemberService;
import com.lyl.study.cloud.vip.core.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class MemberServiceTest extends BaseTest {
    @Autowired
    private MemberService memberService;

    @Test
    public void testList() {
        List<Member> members = memberService.selectList(
                new EntityWrapper<Member>()
                        .orderBy("create_time", false)
        );
        members.forEach(System.out::println);
    }

    @Test
    public void testSelectById() {
        Member member = memberService.selectById(1);
        System.out.println(member);

        member = memberService.selectById(2);
        System.out.println(member);
    }
}
