package net.syl.dandelion.handler.flowcontrol;

import net.syl.dandelion.support.entity.TaskInfo;
import net.syl.dandelion.support.pipeline.ProcessContext;
import net.syl.dandelion.support.pipeline.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class FlowControlProcessor implements Processor<TaskInfo> {
    @Autowired
    private StringRedisTemplate redisTemplate;
    private static final long maxTime = 60 * 1000;
    private static final int maxSize = 5;

    @Override
    public void process(ProcessContext<TaskInfo> context) {
//        // 移除过期数据
//        redisTemplate.opsForZSet().removeRangeByScore(key, 0, System.currentTimeMillis() - maxTime);
//        // 统计当前窗口内有多少数据
//        Long count = redisTemplate.opsForZSet().count(key, 0, System.currentTimeMillis());
//        // 限流
//        if (count > maxSize) {
//            long time = System.currentTimeMillis();
//            redisTemplate.opsForZSet().add(Key, time, time);
//        } else {
//            context.setNeedBreak(true);
//        }
    }
}
