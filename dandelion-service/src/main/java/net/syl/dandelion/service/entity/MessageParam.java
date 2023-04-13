package net.syl.dandelion.service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Map;
import java.util.Set;

/**
 * 消息参数
 * single
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageParam {

    /**
     * @Description: 接收者
     * 【不能大于100个】
     * 必传
     */
    private Set<String> receivers;

    /**
     * @Description: 消息内容中的可变部分(占位符替换)
     * 可选
     */
    private Map<String, String> variables;

    /**
     * @Description: URL(占位符替换)
     * 可选
     */
    private Map<String,String> url;
}
