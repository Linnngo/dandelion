package net.syl.dandelion.service.impl.processor;

import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import net.syl.dandelion.common.enums.RespStatusEnum;
import net.syl.dandelion.common.vo.BasicResultVO;
import net.syl.dandelion.service.entity.MessageParam;
import net.syl.dandelion.service.impl.entity.SendTaskModel;
import net.syl.dandelion.support.pipeline.ProcessContext;
import net.syl.dandelion.support.pipeline.Processor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @description 前置参数校验
 */
@Slf4j
@Service
public class PreParamCheckProcessor implements Processor<SendTaskModel> {

    /**
     * 最大的人数
     */
    private static final Integer BATCH_RECEIVER_SIZE = 100;

    @Override
    public void process(ProcessContext<SendTaskModel> context) {
        SendTaskModel sendTaskModel = context.getProcessModel();
        Long messageTemplateId = sendTaskModel.getMessageTemplateId();
        List<MessageParam> messageParams = sendTaskModel.getMessageParams();

        // 1.没有传入 消息模板Id 或者 messageParam
        if (messageTemplateId == null || CollUtil.isEmpty(messageParams)) {
            context.setNeedBreak(true).setResponse(BasicResultVO.fail(RespStatusEnum.CLIENT_BAD_PARAMETERS));
            return;
        }
        List<MessageParam> filterMessageParams = messageParams.stream()
                .filter(messageParam -> CollUtil.isEmpty(messageParam.getReceivers()))
                .collect(Collectors.toList());
        // 2.过滤 receiver=null 的messageParam
        if (CollUtil.isNotEmpty(filterMessageParams)) {
            messageParams.removeAll(filterMessageParams);
        }
        if (CollUtil.isEmpty(messageParams)) {
            context.setNeedBreak(true).setResponse(BasicResultVO.fail(RespStatusEnum.CLIENT_BAD_PARAMETERS));
            return;
        }

        // 3.过滤receiver大于100的请求
        if (messageParams.stream().anyMatch(messageParam -> messageParam.getReceivers().size() > BATCH_RECEIVER_SIZE)) {
            context.setNeedBreak(true).setResponse(BasicResultVO.fail(RespStatusEnum.TOO_MANY_RECEIVER));
        }
    }
}