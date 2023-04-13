package net.syl.dandelion.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum BusinessCode {

    /** 普通发送流程 */
    SEND("send", "普通发送"),

    /** 撤回流程 */
    RECALL("recall", "撤回消息");


    /** code 关联着责任链的模板 */
    private String code;

    /** 类型说明 */
    private String description;


}
