package net.syl.dandelion.service.impl.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import net.syl.dandelion.common.enums.RespStatusEnum;
import net.syl.dandelion.common.vo.BasicResultVO;
import net.syl.dandelion.service.entity.BatchSendRequest;
import net.syl.dandelion.service.entity.MessageParam;
import net.syl.dandelion.service.entity.SendRequest;
import net.syl.dandelion.service.entity.SendResponse;
import net.syl.dandelion.service.impl.entity.SendTaskModel;
import net.syl.dandelion.service.service.SendService;
import net.syl.dandelion.support.aop.SPelContextHolder;
import net.syl.dandelion.support.aop.annotation.Anchor;
import net.syl.dandelion.support.pipeline.ProcessContext;
import net.syl.dandelion.support.pipeline.ProcessController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;

/**
 * 发送接口
 */
@Service
@Slf4j
public class SendServiceImpl implements SendService {

    @Autowired
    private ProcessController processController;

    @Override
    @Anchor(bizId = "#sendTaskModel.businessId", response = "#response",
            receivers = "#sendRequest.messageParam.receivers",
            templateId = "#sendRequest.messageTemplateId", tag = "sendService")
    public SendResponse send(SendRequest sendRequest) {
        SendTaskModel sendTaskModel = SendTaskModel.builder()
                .businessId(IdUtil.getSnowflake().nextId())
                .messageTemplateId(sendRequest.getMessageTemplateId())
                .messageParams(CollUtil.newArrayList(sendRequest.getMessageParam()))
                .build();

        ProcessContext context = ProcessContext.builder()
                .code(sendRequest.getCode())
                .processModel(sendTaskModel)
                .needBreak(false)
                .response(BasicResultVO.success()).build();

        try {
            processController.process(context);

        } catch (Exception e) {
            log.error("SendServiceImpl#send fail{}", Throwables.getStackTraceAsString(e));
            context.setNeedBreak(true).setResponse(BasicResultVO.fail(RespStatusEnum.SERVICE_ERROR));
        }
        SPelContextHolder.putVariables("sendTaskModel", sendTaskModel);
        SPelContextHolder.putVariables("response", context.getResponse());
        return new SendResponse(context.getResponse().getStatus(), context.getResponse().getMsg());
    }

    @Anchor(bizId = "#sendTaskModel.businessId", response = "#response",
            receivers = "#receivers",
            templateId = "#sendRequest.messageTemplateId", tag = "sendService")
    public SendResponse batchSend(BatchSendRequest sendRequest) {
        SendTaskModel sendTaskModel = SendTaskModel.builder()
                .businessId(IdUtil.getSnowflake().nextId())
                .messageTemplateId(sendRequest.getMessageTemplateId())
                .messageParams(sendRequest.getMessageParams())
                .build();
        SPelContextHolder.putVariables("sendTaskModel", sendTaskModel);
        ProcessContext context = ProcessContext.builder()
                .code(sendRequest.getCode())
                .processModel(sendTaskModel)
                .needBreak(false)
                .response(BasicResultVO.success()).build();

        try {
            processController.process(context);

        } catch (Exception e) {
            log.error("SendServiceImpl#send fail{}", Throwables.getStackTraceAsString(e));
            context.setNeedBreak(true).setResponse(BasicResultVO.fail(RespStatusEnum.SERVICE_ERROR));
        }
        HashSet<Object> receivers = CollUtil.newHashSet();
        for (MessageParam messageParam : sendRequest.getMessageParams()) {
            receivers.addAll(messageParam.getReceivers());
        }
        SPelContextHolder.putVariables("receivers", receivers);
        SPelContextHolder.putVariables("response", context.getResponse());
        return new SendResponse(context.getResponse().getStatus(), context.getResponse().getMsg());
    }
}
