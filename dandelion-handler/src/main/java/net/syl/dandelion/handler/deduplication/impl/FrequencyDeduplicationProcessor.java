package net.syl.dandelion.handler.deduplication.impl;

import lombok.extern.slf4j.Slf4j;
import net.syl.dandelion.handler.deduplication.AbstractDeduplicationProcessor;
import net.syl.dandelion.handler.deduplication.DeduplicationParam;
import net.syl.dandelion.support.entity.TaskInfo;
import net.syl.dandelion.support.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static net.syl.dandelion.common.constant.DandelionConstant.DANDELION_HANDLER_PREFIX;

@Component
@Slf4j
public class FrequencyDeduplicationProcessor extends AbstractDeduplicationProcessor {

    @Autowired
    private ConfigService config;
    @Autowired
    private StringRedisTemplate redisTemplate;

    protected static final String DEDUPLICATION = "deduplication.frequency";

    @Override
    public Set<String> deduplication(TaskInfo taskInfo) {
        Set<String> receivers = taskInfo.getReceivers();
        DeduplicationParam param = config.getProperty(DANDELION_HANDLER_PREFIX + DEDUPLICATION, DeduplicationParam.class);

        return receivers.stream().filter(receiver -> {
            String key = deduplicationKey(taskInfo, receiver);
            String count = redisTemplate.opsForValue().get(key);

            if (count != null && Integer.parseInt(count) >= param.getMaxCount()) {
                return true;
            }

            redisTemplate.opsForValue().increment(key);
            redisTemplate.expire(key, param.getTime(), TimeUnit.SECONDS);
            return false;
        }).collect(Collectors.toSet());
    }

    @Override
    public String deduplicationKey(TaskInfo taskInfo, String receiver) {
        return DEDUPLICATION + ":"
                + receiver + ":"
                + taskInfo.getSendChannel() + ":"
                + taskInfo.getMessageTemplateId();
    }

}
