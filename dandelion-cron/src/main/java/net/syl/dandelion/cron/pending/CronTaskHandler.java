package net.syl.dandelion.cron.pending;

import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import net.syl.dandelion.cron.constants.CronConstants;
import net.syl.dandelion.service.client.SendClient;
import net.syl.dandelion.service.entity.BatchSendRequest;
import net.syl.dandelion.service.entity.MessageParam;
import net.syl.dandelion.service.enums.BusinessCode;
import net.syl.dandelion.support.entity.MessageTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class CronTaskHandler{

    @Autowired
    private SendClient sendClient;

    public void doHandle(String path, MessageTemplate messageTemplate) {
        String key = path + messageTemplate.getId();
        List<Map<String, String>> list = MessageParamHolder.get(key);
        Map<Map<String, String>, Set<String>> paramMap = new HashMap<>();

        long last = System.currentTimeMillis();
        while (true){
            if (CollUtil.isNotEmpty(list)){
                // 处理每一行数据，把参数相同的数据放在一起
                flatMap(paramMap, list.get(0));
                list.remove(0);
                last = System.currentTimeMillis();
            }

            // 等待超时
            if (System.currentTimeMillis() - last > 3000){
                MessageParamHolder.remove(key);
                break;
            }
            // 数量上限，发送一批
            if (paramMap.size() >= CronConstants.MAX_MESSAGE_PARAM) {
                send(paramMap, messageTemplate);
                paramMap.clear();
            }
        }

        send(paramMap, messageTemplate);
        MessageParamHolder.remove(key);
    }

    private void flatMap(Map<Map<String, String>, Set<String>> resultMap, Map<String, String> paramMap) {
        HashSet<String> newReceivers = CollUtil.newHashSet(paramMap.get(CronConstants.RECEIVERS_KEY).split("、"));
        paramMap.remove(CronConstants.RECEIVERS_KEY);

        Set<String> receivers = resultMap.get(paramMap);
        // 如果参数相同，把收件人组合到一起
        if (CollUtil.isEmpty(receivers)) {
            resultMap.put(paramMap, newReceivers);
        } else {
            receivers.addAll(newReceivers);
        }

    }

    private List<MessageParam> assembleMessageParam(Map<Map<String, String>, Set<String>> paramMap) {
        ArrayList<MessageParam> messageParamList = new ArrayList();

        Set<Map.Entry<Map<String, String>, Set<String>>> entries = paramMap.entrySet();
        for (Map.Entry<Map<String, String>, Set<String>> entry : entries) {
            MessageParam messageParam = MessageParam.builder().variables(entry.getKey())
                    .receivers(entry.getValue()).build();
            messageParamList.add(messageParam);
        }

        return messageParamList;
    }

    private void send(Map<Map<String, String>, Set<String>> paramMap, MessageTemplate messageTemplate) {
        // 组装参数
        List<MessageParam> messageParamList = assembleMessageParam(paramMap);
        BatchSendRequest request = BatchSendRequest.builder().code(BusinessCode.SEND.getCode())
                .messageParams(messageParamList)
                .messageTemplateId(messageTemplate.getId())
                .build();
        // 发送请求
        sendClient.batchSend(request);
    }
}
