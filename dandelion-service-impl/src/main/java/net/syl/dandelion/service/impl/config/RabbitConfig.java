package net.syl.dandelion.service.impl.config;

import net.syl.dandelion.support.utils.QueueMappingUtils;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Properties;

@Configuration
public class RabbitConfig {

    public final static String SEND_EXCHANGE = "send.exchange";

    private RabbitAdmin rabbitAdmin;

    @Bean
    public RabbitAdmin rabbitAdmin(RabbitTemplate rabbitTemplate){
        rabbitAdmin = new RabbitAdmin(rabbitTemplate);
        return rabbitAdmin;
    }

    @Bean
    public Exchange sendExchange(){
        Exchange exchange = ExchangeBuilder.topicExchange(SEND_EXCHANGE).durable(true).build();
        List<String> queueNameList = QueueMappingUtils.getRoutingKeys();
        for (String queueName : queueNameList) {
            //判断队列是否存在
            Properties properties = rabbitAdmin.getQueueProperties(queueName);
            if (properties == null) {
                System.out.println(properties);
                Queue queue = new Queue(queueName, true, false, false, null);
                rabbitAdmin.declareQueue(queue);
                rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(exchange).with(queueName).noargs());
            }
        }
        return exchange;
    }
}
