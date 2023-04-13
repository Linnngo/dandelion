package net.syl.dandelion.handler.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 *
 *  /11/22
 */
@Getter
@ToString
@AllArgsConstructor
public enum BusinessCode {

    /** 普通发送流程 */
    COMMON("common", "常规处理"),
    ;

    /** code 关联着责任链的模板 */
    private String code;

    /** 类型说明 */
    private String description;


}
