package net.syl.dandelion.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 全局响应状态枚举
 *
 **/
@Getter
@ToString
@AllArgsConstructor
public enum RespStatusEnum {
  /**
   * OK：操作成功
   */
  SUCCESS("0", "操作成功"),
  FAIL("-1", "操作失败"),


  /**
   * 客户端
   */
  CLIENT_BAD_PARAMETERS("C0001", "客户端参数错误"),
  TEMPLATE_NOT_FOUND("C0002", "找不到模板或模板已被删除"),
  TOO_MANY_RECEIVER("C0003", "传入的接收者大于100个"),
  ILLEGAL_RECEIVER("C0004", "不合法号码"),

  /**
   * 系统
   */
  SERVICE_ERROR("S0001", "服务执行异常"),
  RESOURCE_NOT_FOUND("S0404", "资源不存在"),


  /**
   * API层
   */
  CONTEXT_IS_NULL("A0001","流程上下文为空"),
  BUSINESS_CODE_IS_NULL("A0002","业务代码为空"),
  PROCESS_TEMPLATE_IS_NULL("A0003","流程模板配置为空"),
  PROCESS_LIST_IS_NULL("A0004","业务处理器配置为空" ),
  MESSAGE_SEND_MQ_SUCCESS("A0005", "消息发送到MQ成功"),
  MESSAGE_SEND_MQ_FAIL("A00056", "消息发送到MQ失败"),

  /**
   * handler
   */
  MESSAGE_IS_RECEIVED("H0010", "消息接收成功"),
  MESSAGE_TEMPLATE_IS_DISCARDED("H0020","消息模板被屏蔽"),
  MESSAGE_IS_DEDUPLICATED("H0031","消息被去重"),
  MESSAGE_DISTRIBUTION_SUCCESS("H0041", "消息下发成功"),
  MESSAGE_DISTRIBUTION_FAILED("H0042", "消息下发失败")
  ;

  /**
   * 响应状态
   */
  private final String code;
  /**
   * 响应编码
   */
  private final String msg;
}
