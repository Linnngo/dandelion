package net.syl.dandelion.service.impl.service;

import net.syl.dandelion.common.vo.BasicResultVO;
import net.syl.dandelion.service.entity.SendRequest;
import net.syl.dandelion.service.entity.SendResponse;
import net.syl.dandelion.service.impl.entity.SendTaskModel;
import net.syl.dandelion.service.service.RecallService;
import net.syl.dandelion.support.pipeline.ProcessContext;
import net.syl.dandelion.support.pipeline.ProcessController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 撤回接口
 *
 */
@Service
public class RecallServiceImpl implements RecallService {

    @Autowired
    private ProcessController processController;

    @Override
    public SendResponse recall(SendRequest sendRequest) {
        SendTaskModel sendTaskModel = SendTaskModel.builder()
                .messageTemplateId(sendRequest.getMessageTemplateId())
                .build();
        ProcessContext context = ProcessContext.builder()
                .code(sendRequest.getCode())
                .processModel(sendTaskModel)
                .needBreak(false)
                .response(BasicResultVO.success()).build();
        ProcessContext process = processController.process(context);
        return new SendResponse(process.getResponse().getStatus(), process.getResponse().getMsg());
    }
}
