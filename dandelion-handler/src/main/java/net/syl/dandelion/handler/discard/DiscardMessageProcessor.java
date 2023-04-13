package net.syl.dandelion.handler.discard;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import net.syl.dandelion.common.constant.DandelionConstant;
import net.syl.dandelion.common.enums.RespStatusEnum;
import net.syl.dandelion.common.vo.BasicResultVO;
import net.syl.dandelion.support.aop.SPelContextHolder;
import net.syl.dandelion.support.entity.TaskInfo;
import net.syl.dandelion.support.pipeline.ProcessContext;
import net.syl.dandelion.support.pipeline.Processor;
import net.syl.dandelion.support.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static net.syl.dandelion.common.constant.DandelionConstant.DANDELION_HANDLER_PREFIX;

@Component
public class DiscardMessageProcessor implements Processor<TaskInfo> {

    @Autowired
    private ConfigService config;

    protected static final String DISCARD_MSGIDS = "discard.msgIds";

    @Override
    public void process(ProcessContext<TaskInfo> context) {
        TaskInfo taskInfo = context.getProcessModel();
        SPelContextHolder.putVariables("taskInfo", taskInfo);
        JSONArray array = JSON.parseArray(config.getProperty(DANDELION_HANDLER_PREFIX + DISCARD_MSGIDS,
                DandelionConstant.JSON_ARRAY_DEFAULT_VALUE));

        if (array.contains(String.valueOf(taskInfo.getMessageTemplateId()))){
            context.setNeedBreak(true).setResponse(BasicResultVO.fail(RespStatusEnum.MESSAGE_TEMPLATE_IS_DISCARDED));
        }
    }
}
