package net.syl.dandelion.support.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "dandelion.support.config-service")
public class ConfigServiceConfiguration {

    private Boolean enable;
    private Boolean enableNacos;
    private String localPropertiesPath;
}
