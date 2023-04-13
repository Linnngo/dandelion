package net.syl.dandelion.support.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "dandelion.support.anchor")
public class AnchorConfiguration {

    private String dataPipeline;
    private String routingKey;
    private String exchangeName;
}
