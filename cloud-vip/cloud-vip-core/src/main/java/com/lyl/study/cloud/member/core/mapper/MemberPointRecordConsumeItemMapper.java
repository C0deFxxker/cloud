package com.lyl.study.cloud.member.core.mapper;

import com.lyl.study.cloud.member.core.entity.MemberPointRecordConsumeItem;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * <p>
 * 考虑消耗积分操作的撤回功能，需要按照同样过期时间返还 Mapper 接口
 * </p>
 *
 * @author liyilin
 * @since 2018-09-30
 */
public interface MemberPointRecordConsumeItemMapper extends BaseMapper<MemberPointRecordConsumeItem> {

}
