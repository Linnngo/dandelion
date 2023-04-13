package net.syl.dandelion.handler.handler;

import net.syl.dandelion.support.entity.MessageTemplate;
import net.syl.dandelion.support.entity.TaskInfo;

/**
 *
 * 消息处理器
 */
public interface Handler{

    /**
     * 处理器
     *
     * @param taskInfo
     */
    Boolean doHandler(TaskInfo taskInfo);

    /**
     * 撤回消息
     *
     * @param messageTemplate
     * @return
     */
    void recall(MessageTemplate messageTemplate);
}
