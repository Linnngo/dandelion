package net.syl.dandelion.support.aop.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.syl.dandelion.support.aop.service.LogListener;
import net.syl.dandelion.support.config.AnchorConfiguration;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ConditionalOnProperty(prefix = "dandelion.support.anchor", name = "data-pipeline", havingValue = "rabbitMq")
public class RabbitMQListener implements LogListener {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private AnchorConfiguration config;

    @Override
    public void createLog(String logStr) throws Exception {
        log.info(logStr);
        rabbitTemplate.convertAndSend(config.getExchangeName(), config.getRoutingKey(), logStr);
    }
}
