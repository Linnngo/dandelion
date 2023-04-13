package net.syl.dandelion.support.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson.JSON;
import net.syl.dandelion.common.constant.DandelionConstant;
import net.syl.dandelion.support.config.ConfigServiceConfiguration;
import net.syl.dandelion.support.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@ConditionalOnProperty(prefix = "dandelion.support.config-service", name = "enable")
public class ConfigServiceImpl implements ConfigService {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 本地配置
     */
    @Autowired
    private ConfigServiceConfiguration config;
    private Props props;

    @PostConstruct
    public void init(){
        props = new Props(config.getLocalPropertiesPath(), StandardCharsets.UTF_8);
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        if (config.getEnableNacos()){
            String property = applicationContext.getEnvironment().getProperty(key);
            if (!StrUtil.isBlank(property)){
                return property;
            }
        }
        return props.getProperty(key, defaultValue);
    }

    @Override
    public <T> T getProperty(String key, Class<T> clazz){
        String property = getProperty(key, DandelionConstant.JSON_OBJECT_DEFAULT_VALUE);
        return JSON.parseObject(property, clazz);
    }

    @Override
    public <T> List<T> getPropertyList(String key, Class<T> clazz){
        String property = getProperty(key, DandelionConstant.JSON_ARRAY_DEFAULT_VALUE);
        return JSON.parseArray(property, clazz);
    }
}
