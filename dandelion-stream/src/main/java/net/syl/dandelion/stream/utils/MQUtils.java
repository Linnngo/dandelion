package net.syl.dandelion.stream.utils;

import net.syl.dandelion.stream.constants.DandelionFlinkConstant;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSource;
import org.apache.flink.streaming.connectors.rabbitmq.common.RMQConnectionConfig;
import static net.syl.dandelion.stream.constants.DandelionFlinkConstant.*;

public class MQUtils {

    public static RMQSource<String> getRMQSource(){
        final RMQConnectionConfig connectionConfig = new RMQConnectionConfig.Builder()
                .setHost(RMQ_IP)
                .setVirtualHost(VIRTUAlHOST)
                .setPort(RMQ_PORT)
                .setUserName(USERNAME)
                .setPassword(PASSWORD)
                .build();

        return new RMQSource<String>(
                connectionConfig,   // rabbitmq连接的配置
                DandelionFlinkConstant.QUEUE_NAME,  // rabbitmq的队列名，消费的队列名
                true,    // 使用相关编号，至少一次时设置为false
                new SimpleStringSchema());// 反序列化成java的对象
    }
}
