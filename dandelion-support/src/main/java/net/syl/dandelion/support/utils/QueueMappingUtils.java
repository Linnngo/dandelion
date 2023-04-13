package net.syl.dandelion.support.utils;

import net.syl.dandelion.common.enums.ChannelType;
import net.syl.dandelion.common.enums.MessageType;

import java.util.ArrayList;
import java.util.List;

public class QueueMappingUtils {

    /**
     * 获取所有的RoutingKey
     * (不同的渠道不同的消息类型拥有自己的RoutingKey)
     */
    public static List<String> getRoutingKeys() {
        List<String> groupIds = new ArrayList<>();
        for (ChannelType channelType : ChannelType.values()) {
            for (MessageType messageType : MessageType.values()) {
                groupIds.add(channelType.getCodeEn() + "." + messageType.getCodeEn());
            }
        }
        return groupIds;
    }

    public static String getRoutingKey(Integer channelTypeCode, Integer messageTypeCode){
        return ChannelType.getEnumByCode(channelTypeCode).getCodeEn() + "."
                + MessageType.getEnumByCode(messageTypeCode).getCodeEn();
    }

}
