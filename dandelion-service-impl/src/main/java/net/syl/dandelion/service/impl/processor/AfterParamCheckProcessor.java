package net.syl.dandelion.service.impl.processor;

import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import net.syl.dandelion.common.enums.RespStatusEnum;
import net.syl.dandelion.common.vo.BasicResultVO;
import net.syl.dandelion.service.impl.entity.SendTaskModel;
import net.syl.dandelion.support.aop.SPelContextHolder;
import net.syl.dandelion.support.aop.annotation.Anchor;
import net.syl.dandelion.support.entity.TaskInfo;
import net.syl.dandelion.support.pipeline.ProcessContext;
import net.syl.dandelion.support.pipeline.Processor;
import net.syl.dandelion.support.utils.RegexUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * 后置参数检查
 */
@Slf4j
@Service
public class AfterParamCheckProcessor implements Processor<SendTaskModel> {

    @Override
    @Anchor(bizId = "#sendTaskModel.businessId", response = "#response",
            receivers = "#receivers",
            templateId = "#sendTaskModel.messageTemplateId", tag = "sendService")
    public void process(ProcessContext<SendTaskModel> context) {
        SendTaskModel sendTaskModel = context.getProcessModel();
        List<TaskInfo> taskInfos = sendTaskModel.getTaskInfos();
        SPelContextHolder.putVariables("sendTaskModel", sendTaskModel);
        
        // 1. 过滤掉不合法的手机号、邮件
        Integer idType = taskInfos.get(0).getIdType();
        Set<String> allFilterReceivers = CollUtil.newHashSet();

        List<TaskInfo> resultTaskInfos = taskInfos.stream().filter(taskInfo -> {
            Set<String> receivers = taskInfo.getReceivers();

            Set<String> filterReceivers = receivers.stream().filter(receiver ->
                    !RegexUtils.isInvalid(idType, receiver)
            ).collect(Collectors.toSet());

            if (CollUtil.isNotEmpty(filterReceivers)) {
                receivers.removeAll(filterReceivers);
                allFilterReceivers.addAll(filterReceivers);
            }

            return CollUtil.isNotEmpty(receivers);
        }).collect(Collectors.toList());

        sendTaskModel.setTaskInfos(resultTaskInfos);

        if (CollUtil.isNotEmpty(allFilterReceivers)) {
            SPelContextHolder.putVariables("receivers", allFilterReceivers);
            SPelContextHolder.putVariables("response", new BasicResultVO(RespStatusEnum.ILLEGAL_RECEIVER));
        }

        if (CollUtil.isEmpty(resultTaskInfos)) {
            context.setNeedBreak(true).setResponse(new BasicResultVO(RespStatusEnum.CLIENT_BAD_PARAMETERS));
        }
    }
}