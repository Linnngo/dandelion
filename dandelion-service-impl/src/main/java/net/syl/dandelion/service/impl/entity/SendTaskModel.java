package net.syl.dandelion.service.impl.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.syl.dandelion.service.entity.MessageParam;
import net.syl.dandelion.support.entity.TaskInfo;

import java.util.List;

/**
 * @description 发送消息任务模型
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SendTaskModel{

    /**
     * 业务Id(数据追踪使用)
     * 生成逻辑参考 TaskInfoUtils
     */
    private Long businessId;

    /**
     * 消息模板Id
     */
    private Long messageTemplateId;

    /**
     * 请求参数
     */
    private List<MessageParam> messageParams;

    /**
     * 发送任务的信息
     */
    private List<TaskInfo> taskInfos;

//    /**
//     * 撤回任务的信息
//     */
//    private MessageTemplate messageTemplate;

}
