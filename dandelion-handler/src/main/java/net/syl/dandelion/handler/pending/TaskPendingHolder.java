package net.syl.dandelion.handler.pending;

import net.syl.dandelion.handler.config.HandlerThreadPoolConfig;
import net.syl.dandelion.support.utils.QueueMappingUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;


/**
 * 存储 每种消息类型 与 TaskPending 的关系
 *
 */
@Component
public class TaskPendingHolder {

    private Map<String, ExecutorService> taskPendingHolder = new HashMap<>(32);

    /**
     * 获取得到所有的groupId
     */
    private static List<String> routingKeys = QueueMappingUtils.getRoutingKeys();

    /**
     * 给每个渠道，每种消息类型初始化一个线程池
     */
    @PostConstruct
    public void init() {
        /**
         * example ThreadPoolName:austin.im.notice
         * 可以通过apollo配置：dynamic-tp-apollo-dtp.yml  动态修改线程池的信息
         */
        for (String routingKey : routingKeys) {
            ExecutorService executor = HandlerThreadPoolConfig.getExecutor();
            taskPendingHolder.put(routingKey, executor);
        }
    }

    /**
     * 得到对应的线程池
     *
     * @param groupId
     * @return
     */
    public ExecutorService route(String groupId) {
        return taskPendingHolder.get(groupId);
    }
}