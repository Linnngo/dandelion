package net.syl.dandelion.service.impl.processor;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import net.syl.dandelion.common.constant.DandelionConstant;
import net.syl.dandelion.common.dto.model.ContentModel;
import net.syl.dandelion.common.enums.ChannelType;
import net.syl.dandelion.common.enums.RespStatusEnum;
import net.syl.dandelion.common.vo.BasicResultVO;
import net.syl.dandelion.service.entity.MessageParam;
import net.syl.dandelion.service.impl.entity.SendTaskModel;
import net.syl.dandelion.support.entity.MessageTemplate;
import net.syl.dandelion.support.entity.TaskInfo;
import net.syl.dandelion.support.mapper.MessageTemplateMapper;
import net.syl.dandelion.support.pipeline.ProcessContext;
import net.syl.dandelion.support.pipeline.Processor;
import net.syl.dandelion.support.utils.PlaceholderUtils;
import net.syl.dandelion.support.utils.UrlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * @description 拼装参数
 *
 */
@Slf4j
@Component
public class AssembleProcessor implements Processor<SendTaskModel> {

    @Resource
    private MessageTemplateMapper messageTemplateMapper;
    @Autowired
    private UrlUtils urlUtils;

    @Override
    public void process(ProcessContext<SendTaskModel> context) {
        SendTaskModel sendTaskModel = context.getProcessModel();
        Long messageTemplateId = sendTaskModel.getMessageTemplateId();
        MessageTemplate messageTemplate = messageTemplateMapper.selectById(messageTemplateId);

        // 如果模板为空或者被删除，返回错误
        if (messageTemplate == null || messageTemplate.getIsDeleted().equals(DandelionConstant.TRUE)){
            context.setNeedBreak(true).setResponse(BasicResultVO.fail(RespStatusEnum.TEMPLATE_NOT_FOUND));
            return;
        }

        List<TaskInfo> taskInfos = assembleTaskInfo(sendTaskModel, messageTemplate);
        sendTaskModel.setTaskInfos(taskInfos);
    }

    /**
     * 组装 TaskInfo 任务消息
     *
     * @param sendTaskModel
     * @param messageTemplate
     */
    private List<TaskInfo> assembleTaskInfo(SendTaskModel sendTaskModel, MessageTemplate messageTemplate) {
        List<MessageParam> messageParams = sendTaskModel.getMessageParams();
        List<TaskInfo> taskInfos = new ArrayList<>();
        long time = System.currentTimeMillis();
        for (MessageParam messageParam : messageParams) {
            TaskInfo taskInfo = TaskInfo.builder()
                    .messageTemplateId(messageTemplate.getId())
                    .businessId(sendTaskModel.getBusinessId())
                    .receivers(messageParam.getReceivers())
                    .idType(messageTemplate.getIdType())
                    .sendChannel(messageTemplate.getSendChannel())
                    .templateType(messageTemplate.getTemplateType())
                    .msgType(messageTemplate.getMsgType())
                    .shieldType(messageTemplate.getShieldType())
                    .sendAccount(messageTemplate.getSendAccount())
                    .contentModel(assembleContentModel(messageTemplate, messageParam, sendTaskModel.getBusinessId()))
                    .sendTime(time)
                    .build();

            taskInfos.add(taskInfo);
        }

        return taskInfos;

    }

    private ContentModel assembleContentModel(MessageTemplate messageTemplate, MessageParam messageParam, Long businessId){
        // 得到真正的ContentModel 类型
        Integer sendChannel = messageTemplate.getSendChannel();
        Class contentModelClass = ChannelType.getChanelModelClassByCode(sendChannel);

        // 得到模板的 msgContent 和 入参
        Map<String, String> variables = messageParam.getVariables();

        // 给url转换成短链接
        if (variables != null && variables.containsKey("url")) {
            String url = variables.get("url");
            Set<String> receivers = messageParam.getReceivers();
            for (String receiver : receivers) {
                String shortUrl = urlUtils.getShortUrl(url, receiver, businessId);
                variables.put("url", "http://localhost:8080/short/" + shortUrl);
            }
        }

        String value = messageTemplate.getTemplateContent();
        if (variables != null && !variables.isEmpty()) {
            value = PlaceholderUtils.replacePlaceHolder(value, variables);
        }
        ContentModel contentModel = (ContentModel) JSONObject.parseObject(value, contentModelClass);

        return contentModel;
    }
}