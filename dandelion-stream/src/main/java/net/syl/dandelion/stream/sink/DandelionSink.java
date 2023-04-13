package net.syl.dandelion.stream.sink;

import com.alibaba.fastjson.JSON;
import io.lettuce.core.RedisFuture;
import net.syl.dandelion.common.constant.DandelionConstant;
import net.syl.dandelion.common.entity.SimpleAnchorLog;
import net.syl.dandelion.stream.utils.RedisUtils;
import net.syl.dandelion.support.aop.AnchorLog;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息进 redis/hive
 *
 */
public class DandelionSink extends RichSinkFunction<Tuple2<String, SimpleAnchorLog>> {

    ValueState<Tuple2<String, SimpleAnchorLog>> valueState;

    @Override
    public void open(Configuration parameters) throws Exception {
        ValueStateDescriptor descriptor = new ValueStateDescriptor(
                "value", new TypeHint<Tuple2<String, SimpleAnchorLog>>(){}.getTypeInfo());
        // 设置状态删除策略
        StateTtlConfig ttlConfig = StateTtlConfig.newBuilder(Time.days(1))
                .setUpdateType(StateTtlConfig.UpdateType.OnCreateAndWrite)
                .cleanupFullSnapshot()
                .build();
        descriptor.enableTimeToLive(ttlConfig);
        valueState = getRuntimeContext().getState(descriptor);
    }

    @Override
    public void invoke(Tuple2<String, SimpleAnchorLog> tuple2, Context context) throws Exception {
        Tuple2<String, SimpleAnchorLog> stateTuple = valueState.value();
        // 第一条消息valueState为空，会触发空指针异常
        if (stateTuple != null) {
            tuple2.f1.getAnchorList().addAll(stateTuple.f1.getAnchorList());
        }
        valueState.update(tuple2);
        realTimeData(tuple2);
//        offlineDate(anchorLog);
    }


    /**
     * 实时数据存入Redis
     * 1.用户维度(查看用户当天收到消息的链路详情)，数量级大，只保留当天
     * key:receiver 数据结构:hash
     * hashKey:business  hashValue:{businessId, templateId, anchorList[anchorItem, anchorItem]}
     * anchorItem:{time:"localDateTime, msg:"消息下发成功"}
     *
     * 2.消息模板维度(查看消息模板整体下发情况)，数量级小，保留30天
     * key:templateId 数据结构:hash
     * hashKey:msg(响应的消息例如"消息下发成功") hashValue:count(对应数量)
     * @param tuple2
     *
     */
    private void realTimeData(Tuple2<String, SimpleAnchorLog> tuple2) {
        RedisUtils.pipeline(redisAsyncCommands -> {
            List<RedisFuture<?>> redisFutures = new ArrayList<>();
            // 用户维度
            String userKey = DandelionConstant.STREAM_USER_TRACKING_PREFIX + tuple2.f0;
            redisFutures.add(redisAsyncCommands.hset(
                    userKey.getBytes(), tuple2.f1.getBusinessId().toString().getBytes(), JSON.toJSONString(tuple2.f1).getBytes()));
            redisFutures.add(redisAsyncCommands.expire(userKey.getBytes(), 60*60*24));
            // 消息模板维度
            String templateKey = DandelionConstant.STREAM_TEMPLATE_TRACKING_PREFIX + tuple2.f1.getTemplateId();
            redisFutures.add(redisAsyncCommands.hincrby(
                    templateKey.getBytes(), tuple2.f1.getAnchorList().get(0).getMsg().getBytes(), 1));
            redisFutures.add(redisAsyncCommands.expire(templateKey.getBytes(), 60*60*24*30));
            return redisFutures;
        });
    }

    /**
     * 离线数据存入hive
     *
     * @param anchorLog
     */
    private void offlineDate(AnchorLog anchorLog) {

    }


}
