package net.syl.dandelion.common.dto.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 腾讯短信参数
 *
 * 参数示例：
 * [{"sms_10":{"url":"sms.tencentcloudapi.com","region":"ap-guangzhou","secretId":"***","secretKey":"***","smsSdkAppId":"***","templateId":"***","signName":"***","supplierId":10,"supplierName":"腾讯云"}}]
 *
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TencentSmsAccount {

    /**
     * api相关
     */
    private String url;
    private String region ;

    /**
     * 账号相关
     */
    private String secretId;
    private String secretKey;
    private String smsSdkAppId;
    private String signName;

    /**
     * 标识渠道商Id
     */
    private Integer supplierId;

    /**
     * 标识渠道商名字
     */
    private String supplierName;

}
