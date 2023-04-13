package net.syl.dandelion.support.service;

import java.util.List;

public interface ConfigService {

    String getProperty(String key, String defaultValue);

    <T> T getProperty(String key, Class<T> clazz);

    <T> List<T> getPropertyList(String key, Class<T> clazz);
}
