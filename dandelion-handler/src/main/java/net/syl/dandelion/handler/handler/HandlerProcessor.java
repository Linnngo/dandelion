package net.syl.dandelion.handler.handler;

import net.syl.dandelion.common.enums.RespStatusEnum;
import net.syl.dandelion.common.vo.BasicResultVO;
import net.syl.dandelion.handler.config.HandlerConfig;
import net.syl.dandelion.support.entity.TaskInfo;
import net.syl.dandelion.support.mapper.TaskInfoMapper;
import net.syl.dandelion.support.pipeline.ProcessContext;
import net.syl.dandelion.support.pipeline.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;

import static net.syl.dandelion.common.constant.DandelionConstant.STREAM_SHORTURL_TIME;

@Component
public class HandlerProcessor implements Processor<TaskInfo> {

    @Autowired
    private HandlerHolder handlerHolder;
    @Autowired
    private TaskInfoMapper taskInfoMapper;
    @Autowired
    private HandlerConfig handlerConfig;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void process(ProcessContext<TaskInfo> context) {
        TaskInfo taskInfo = context.getProcessModel();

        Handler handler = handlerHolder.route(taskInfo.getSendChannel());
        if (handler != null) {
            Boolean result = false;
            if (handlerConfig.isRealSend()) {
                result = handler.doHandler(taskInfo);
            }
            System.out.println(handlerConfig.isRealSend());
            if (result) {
                taskInfo.setStatus(2);
                redisTemplate.opsForZSet().addIfAbsent(STREAM_SHORTURL_TIME + taskInfo.getBusinessId(), "", new Date().getTime());
                context.setResponse(new BasicResultVO(RespStatusEnum.MESSAGE_DISTRIBUTION_SUCCESS));
            }else {
                taskInfo.setStatus(3);
                context.setNeedBreak(true).setResponse(BasicResultVO.fail(RespStatusEnum.MESSAGE_DISTRIBUTION_FAILED));
            }
            taskInfoMapper.updateById(taskInfo);
        }
    }
}
