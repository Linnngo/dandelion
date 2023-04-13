package net.syl.dandelion.stream.function;

import com.alibaba.fastjson.JSON;
import net.syl.dandelion.common.entity.SimpleAnchorLog;
import net.syl.dandelion.support.aop.AnchorLog;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

/**
 * process 处理
 */
public class DandelionFlatMapFunction extends RichFlatMapFunction<String, Tuple2<String, SimpleAnchorLog>> {

    @Override
    public void flatMap(String value, Collector<Tuple2<String, SimpleAnchorLog>> collector) throws Exception {
        AnchorLog anchorLog = JSON.parseObject(value, AnchorLog.class);

        for (String receiver : anchorLog.getReceivers()) {
            Tuple2<String, SimpleAnchorLog> tuple2 = new Tuple2<>();
            tuple2.f0 = receiver;
            tuple2.f1 = AnchorLog.getSimpleAnchorLog(anchorLog);
            collector.collect(tuple2);
        }
    }
}
