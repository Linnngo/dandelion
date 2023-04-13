package net.syl.dandelion.service.impl.processor;

import net.syl.dandelion.common.vo.BasicResultVO;
import net.syl.dandelion.service.entity.MessageParam;
import net.syl.dandelion.service.impl.entity.SendTaskModel;
import net.syl.dandelion.support.pipeline.ProcessContext;
import net.syl.dandelion.support.pipeline.Processor;
import org.springframework.stereotype.Component;

@Component
public class DieFilterProcessor implements Processor<SendTaskModel> {

    @Override
    public void process(ProcessContext<SendTaskModel> context) {
        for (MessageParam messageParam : context.getProcessModel().getMessageParams()) {
            if (messageParam.getVariables() == null) {
                break;
            }
            messageParam.getVariables().forEach((k, v) -> {
                if (v.contains("死")) {
                    context.setNeedBreak(true);
                    context.setResponse(BasicResultVO.fail("死字不吉利，已被过滤"));
                }
            });
        }
    }
}
