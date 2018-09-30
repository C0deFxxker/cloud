package com.lyl.study.cloud.member.core.service.impl;

import com.lyl.study.cloud.member.core.entity.Member;
import com.lyl.study.cloud.member.core.mapper.MemberMapper;
import com.lyl.study.cloud.member.core.service.MemberService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author liyilin
 * @since 2018-09-30
 */
@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements MemberService {

}
