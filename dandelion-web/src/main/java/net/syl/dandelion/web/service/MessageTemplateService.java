package net.syl.dandelion.web.service;

import net.syl.dandelion.common.vo.BasicResultVO;
import net.syl.dandelion.support.entity.MessageTemplate;
import net.syl.dandelion.web.vo.MessageTemplateParam;

import java.util.List;

public interface MessageTemplateService {

    BasicResultVO queryById(Long id);

    BasicResultVO queryList(MessageTemplateParam messageTemplateParam);

    BasicResultVO saveOrUpdate(MessageTemplate messageTemplate);

    BasicResultVO deleteById(List<Long> idList);

    BasicResultVO startCronTask(Long id);

    BasicResultVO stopCronTask(Long id);
}
