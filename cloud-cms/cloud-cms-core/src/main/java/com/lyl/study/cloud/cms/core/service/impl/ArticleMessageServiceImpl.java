package com.lyl.study.cloud.cms.core.service.impl;

import com.lyl.study.cloud.cms.core.entity.ArticleMessage;
import com.lyl.study.cloud.cms.core.mapper.ArticleMessageMapper;
import com.lyl.study.cloud.cms.core.service.ArticleMessageService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 图文消息（已经发送出去的） 服务实现类
 * </p>
 *
 * @author liyilin
 * @since 2018-09-13
 */
@Service
public class ArticleMessageServiceImpl extends ServiceImpl<ArticleMessageMapper, ArticleMessage> implements ArticleMessageService {

}
