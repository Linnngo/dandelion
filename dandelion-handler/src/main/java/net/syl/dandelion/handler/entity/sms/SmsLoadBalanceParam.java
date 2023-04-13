package net.syl.dandelion.handler.entity.sms;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 对于每种消息类型的 短信配置
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SmsLoadBalanceParam {

    /**
     * 权重(决定着流量的占比)
     */
    private Integer weights;

    /**
     * script名称
     */
    private Integer supplierCode;

}
