package net.syl.dandelion.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 发送ID类型枚举
 */
@Getter
@ToString
@AllArgsConstructor
public enum IdType {
    PHONE(30, "phone"),
    EMAIL(50, "email"),
    OPEN_CONVERSATION_ID(70, "openConversationId"),
    ;

    private Integer code;
    private String type;

    /**
     * 通过code获取type
     * @param code
     * @return
     */
    public static String getIdTypeByCode(Integer code) {
        IdType[] values = values();
        for (IdType value : values) {
            if (value.getCode().equals(code)) {
                return value.getType();
            }
        }
        return null;
    }

}
