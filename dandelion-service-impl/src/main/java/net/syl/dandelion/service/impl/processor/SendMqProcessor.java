package net.syl.dandelion.service.impl.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import net.syl.dandelion.common.enums.RespStatusEnum;
import net.syl.dandelion.common.vo.BasicResultVO;
import net.syl.dandelion.service.impl.entity.SendTaskModel;
import net.syl.dandelion.support.entity.TaskInfo;
import net.syl.dandelion.support.mapper.TaskInfoMapper;
import net.syl.dandelion.support.pipeline.ProcessContext;
import net.syl.dandelion.support.pipeline.Processor;
import net.syl.dandelion.support.utils.QueueMappingUtils;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static net.syl.dandelion.service.impl.config.RabbitConfig.SEND_EXCHANGE;

/**
 *
 * 将消息发送到MQ
 */
@Slf4j
@Service
public class SendMqProcessor implements Processor<SendTaskModel> {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Resource
    private TaskInfoMapper taskInfoMapper;

    @Override
    public void process(ProcessContext<SendTaskModel> context) {
        List<TaskInfo> taskInfos = context.getProcessModel().getTaskInfos();
        try {
            taskInfoMapper.batchInsert(taskInfos);
        }catch (Exception e){
            log.error("SendMqProcessor#process insert taskInfos fail exception:{}"
                    , Throwables.getStackTraceAsString(e));
            throw e;
        }

        String routingKey = QueueMappingUtils.getRoutingKey(taskInfos.get(0).getSendChannel(), taskInfos.get(0).getMsgType());

//        for (TaskInfo taskInfo : taskInfos) {
//            rabbitTemplate.convertAndSend(SEND_EXCHANGE, routingKey, JSON.toJSONString(taskInfo
//                    , SerializerFeature.WriteClassName, SerializerFeature.NotWriteRootClassName)
//                    );
//        }

        for (TaskInfo taskInfo : taskInfos) {
            rabbitTemplate.convertAndSend(SEND_EXCHANGE, routingKey, JSON.toJSONString(taskInfo
                    , SerializerFeature.WriteClassName, SerializerFeature.NotWriteRootClassName)
                    , getCorrelationData(taskInfo));
        }
        context.setResponse(new BasicResultVO(RespStatusEnum.MESSAGE_SEND_MQ_SUCCESS));
    }

    public CorrelationData getCorrelationData(TaskInfo taskInfo) {
        CorrelationData correlationData = new CorrelationData(taskInfo.getBusinessId().toString());
        correlationData.getFuture().addCallback(
                result -> {
                    if (result.isAck()){
                        taskInfo.setStatus(1);
                        taskInfoMapper.updateById(taskInfo);
                    }else {
                        log.error("消息发送失败,ID{}，原因{}", correlationData.getId(), result.getReason());
                    }
                },
                ex -> {
                    log.error("消息发送异常,ID{}，原因{}", correlationData.getId(), ex.getMessage());
                }
        );
        return correlationData;
    }
}

