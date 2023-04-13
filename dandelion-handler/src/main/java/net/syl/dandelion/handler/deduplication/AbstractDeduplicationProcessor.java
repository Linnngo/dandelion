package net.syl.dandelion.handler.deduplication;

import cn.hutool.core.collection.CollUtil;
import net.syl.dandelion.common.enums.RespStatusEnum;
import net.syl.dandelion.common.vo.BasicResultVO;
import net.syl.dandelion.support.aop.SPelContextHolder;
import net.syl.dandelion.support.aop.annotation.Anchor;
import net.syl.dandelion.support.entity.TaskInfo;
import net.syl.dandelion.support.pipeline.ProcessContext;
import net.syl.dandelion.support.pipeline.Processor;

import java.util.Set;

public abstract class AbstractDeduplicationProcessor implements Processor<TaskInfo>, DeduplicationService{

    @Override
    @Anchor(bizId = "#taskInfo.businessId", response = "#response",
            receivers = "#receivers",
            templateId = "#taskInfo.messageTemplateId", tag = "handler")
    public void process(ProcessContext<TaskInfo> context) {
        TaskInfo taskInfo = context.getProcessModel();
        SPelContextHolder.putVariables("taskInfo", taskInfo);

        Set<String> filterReceivers = deduplication(taskInfo);
        if (CollUtil.isNotEmpty(filterReceivers)) {
            taskInfo.getReceivers().removeAll(filterReceivers);
            SPelContextHolder.putVariables("receivers", filterReceivers);
            SPelContextHolder.putVariables("response", new BasicResultVO(RespStatusEnum.MESSAGE_IS_DEDUPLICATED));
        }
        if (CollUtil.isEmpty(taskInfo.getReceivers())){
            context.setNeedBreak(true).setResponse(new BasicResultVO(RespStatusEnum.MESSAGE_IS_DEDUPLICATED));
        }
    }

    @Override
    public abstract Set<String> deduplication(TaskInfo taskInfo);

    @Override
    public abstract String deduplicationKey(TaskInfo taskInfo, String receiver);

}
