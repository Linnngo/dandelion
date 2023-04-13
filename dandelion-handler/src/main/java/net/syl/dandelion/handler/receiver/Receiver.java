package net.syl.dandelion.handler.receiver;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import net.syl.dandelion.common.enums.RespStatusEnum;
import net.syl.dandelion.common.vo.BasicResultVO;
import net.syl.dandelion.handler.pending.Task;
import net.syl.dandelion.handler.pending.TaskPendingHolder;
import net.syl.dandelion.support.aop.SPelContextHolder;
import net.syl.dandelion.support.aop.annotation.Anchor;
import net.syl.dandelion.support.entity.TaskInfo;
import net.syl.dandelion.support.utils.QueueMappingUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Receiver{

    private static final String LOG_BIZ_TYPE = "Receiver#consumer";
    @Autowired
    private ApplicationContext context;
    @Autowired
    private TaskPendingHolder taskPendingHolder;

    public Receiver(ApplicationContext context, TaskPendingHolder taskPendingHolder) {
        this.context = context;
        this.taskPendingHolder = taskPendingHolder;
    }

    @Anchor(bizId = "#taskInfo.businessId", response = "#response",
            receivers = "#taskInfo.receivers",
            templateId = "#taskInfo.messageTemplateId", tag = "receiver")
    public void handleMessage(String message){
        TaskInfo taskInfo = JSON.parseObject(message, TaskInfo.class);
        if (BeanUtil.isEmpty(taskInfo, "")) {
            return;
        }
        SPelContextHolder.putVariables("taskInfo", taskInfo);

        String routingKey = QueueMappingUtils.getRoutingKey(taskInfo.getSendChannel(), taskInfo.getMsgType());
        try {
            Task task = context.getBean(Task.class).setTaskInfo(taskInfo);
            taskPendingHolder.route(routingKey).execute(task);
            SPelContextHolder.putVariables("response", BasicResultVO.fail(RespStatusEnum.MESSAGE_IS_RECEIVED));
        } catch (Exception e) {
            log.error("Receiver#handleMessage fail{}", Throwables.getStackTraceAsString(e));
            SPelContextHolder.putVariables("response", BasicResultVO.fail(RespStatusEnum.SERVICE_ERROR));
        }
    }
}
