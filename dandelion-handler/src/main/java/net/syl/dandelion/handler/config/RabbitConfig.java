package net.syl.dandelion.handler.config;

import net.syl.dandelion.handler.receiver.Receiver;
import net.syl.dandelion.support.utils.QueueMappingUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerEndpoint;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
public class RabbitConfig {

    @Autowired
    private ApplicationContext context;

    /**
     * 如果某一个渠道阻塞，使用@RabbitListener方式会导致整个系统阻塞
     * 为每个渠道注册消费者
     * @return
     */
    @Bean
    public RabbitListenerConfigurer rabbitListenerConfigurer(){
        return registrar -> {
            List<String> routingKeys = QueueMappingUtils.getRoutingKeys();
            for (String routingKey : routingKeys) {
                SimpleRabbitListenerEndpoint endpoint = new SimpleRabbitListenerEndpoint();
                endpoint.setId(routingKey);
                endpoint.setConcurrency("2");
                endpoint.setQueueNames(routingKey);
                endpoint.setMessageListener(new MessageListenerAdapter(context.getBean(Receiver.class)));
                registrar.registerEndpoint(endpoint);
            }
        };
    }

}
