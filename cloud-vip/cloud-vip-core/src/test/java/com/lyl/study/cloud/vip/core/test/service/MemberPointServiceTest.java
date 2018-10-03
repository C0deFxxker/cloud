package com.lyl.study.cloud.vip.core.test.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.lyl.study.cloud.vip.core.entity.MemberPoint;
import com.lyl.study.cloud.vip.core.service.MemberPointService;
import com.lyl.study.cloud.vip.core.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class MemberPointServiceTest extends BaseTest {
    @Autowired
    private MemberPointService memberPointService;

    @Test
    public void testList() {
        List<MemberPoint> list = memberPointService.selectList(new EntityWrapper<>());
        list.forEach(System.out::println);
    }

    @Test
    public void testGetById() {
        for (int i = 1; i <= 4; i++) {
            List<MemberPoint> list = memberPointService.selectList(
                    new EntityWrapper<MemberPoint>().eq(MemberPoint.MEMBER_ID, i)
            );
            System.out.println(list);
        }
    }
}
