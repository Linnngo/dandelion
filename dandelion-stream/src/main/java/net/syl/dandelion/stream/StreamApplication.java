package net.syl.dandelion.stream;

import net.syl.dandelion.common.entity.SimpleAnchorLog;
import net.syl.dandelion.stream.function.DandelionFlatMapFunction;
import net.syl.dandelion.stream.sink.DandelionSink;
import net.syl.dandelion.stream.utils.MQUtils;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSource;

public class StreamApplication {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        RMQSource<String> rmqSource = MQUtils.getRMQSource();
        KeyedStream<Tuple2<String, SimpleAnchorLog>, String> stream = env.addSource(rmqSource)
                .assignTimestampsAndWatermarks(WatermarkStrategy.noWatermarks())
                .flatMap(new DandelionFlatMapFunction())
                .keyBy(tuple -> tuple.f0 + tuple.f1.getBusinessId());

        stream.addSink(new DandelionSink());

        env.execute();
    }

}
