package net.syl.dandelion.handler.deduplication.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSON;
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
public class SlideWindowDeduplicationProcessor extends AbstractDeduplicationProcessor {

    //TODO 使用lua脚本替代
    @Autowired
    private ConfigService config;
    @Autowired
    private StringRedisTemplate redisTemplate;
    protected static final String DEDUPLICATION = "deduplication.slide-window";

    @Override
    public Set<String> deduplication(TaskInfo taskInfo) {
        Set<String> receivers = taskInfo.getReceivers();
        DeduplicationParam param = config.getProperty(DANDELION_HANDLER_PREFIX + DEDUPLICATION, DeduplicationParam.class);
        long currentTimeMillis = System.currentTimeMillis();

        return receivers.stream().filter(receiver ->{
            String key = deduplicationKey(taskInfo, receiver);
            Long time = param.getTime();
            redisTemplate.opsForZSet().removeRangeByScore(key, 0, (currentTimeMillis - time * 1000));

            Long count = redisTemplate.opsForZSet().zCard(key);
            if (count != null && count >= param.getMaxCount()){
                return true;
            }

            redisTemplate.opsForZSet().add(key, taskInfo.getBusinessId().toString(), currentTimeMillis);
            redisTemplate.expire(key, time, TimeUnit.SECONDS);
            return false;
        }).collect(Collectors.toSet());
    }

    @Override
    public String deduplicationKey(TaskInfo taskInfo, String receiver) {
        return DEDUPLICATION + ":"
                + DigestUtil.md5Hex(taskInfo.getMessageTemplateId() + receiver
                + JSON.toJSONString(taskInfo.getContentModel()));
    }
}
