package com.lyl.study.cloud.member.core.service.impl;

import com.lyl.study.cloud.member.core.entity.MemberPointRecordConsumeItem;
import com.lyl.study.cloud.member.core.mapper.MemberPointRecordConsumeItemMapper;
import com.lyl.study.cloud.member.core.service.MemberPointRecordConsumeItemService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 考虑消耗积分操作的撤回功能，需要按照同样过期时间返还 服务实现类
 * </p>
 *
 * @author liyilin
 * @since 2018-09-30
 */
@Service
public class MemberPointRecordConsumeItemServiceImpl extends ServiceImpl<MemberPointRecordConsumeItemMapper, MemberPointRecordConsumeItem> implements MemberPointRecordConsumeItemService {

}
