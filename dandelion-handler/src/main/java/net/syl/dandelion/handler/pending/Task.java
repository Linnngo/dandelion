package net.syl.dandelion.handler.pending;


import com.google.common.base.Throwables;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import net.syl.dandelion.common.enums.RespStatusEnum;
import net.syl.dandelion.common.vo.BasicResultVO;
import net.syl.dandelion.handler.enums.BusinessCode;
import net.syl.dandelion.handler.handler.HandlerHolder;
import net.syl.dandelion.support.aop.SPelContextHolder;
import net.syl.dandelion.support.aop.annotation.Anchor;
import net.syl.dandelion.support.entity.TaskInfo;
import net.syl.dandelion.support.mapper.TaskInfoMapper;
import net.syl.dandelion.support.pipeline.ProcessContext;
import net.syl.dandelion.support.pipeline.ProcessController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Task 执行器
 * 0.丢弃消息
 * 2.屏蔽消息
 * 2.通用去重功能
 * 3.发送消息
 *
 *
 */
@Data
@Accessors(chain = true)
@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Task implements Runnable {

    @Autowired
    private ProcessController processController;
    @Autowired
    private HandlerHolder handlerHolder;
    @Autowired
    private TaskInfoMapper taskInfoMapper;

    private TaskInfo taskInfo;

    @Override
    @Anchor(bizId = "#taskInfo.businessId", response = "#response",
            receivers = "#taskInfo.receivers",
            templateId = "#taskInfo.messageTemplateId", tag = "handler")
    public void run() {
        ProcessContext context = ProcessContext.builder()
                .code(BusinessCode.COMMON.getCode())
                .processModel(taskInfo)
                .needBreak(false)
                .response(BasicResultVO.success()).build();

        try {
            processController.process(context);
        } catch (Exception e) {
            log.error("Handler Task fail{}", Throwables.getStackTraceAsString(e));
            context.setNeedBreak(true).setResponse(BasicResultVO.fail(RespStatusEnum.SERVICE_ERROR));
        }
        SPelContextHolder.putVariables("taskInfo", taskInfo);
        SPelContextHolder.putVariables("response", context.getResponse());
    }
}