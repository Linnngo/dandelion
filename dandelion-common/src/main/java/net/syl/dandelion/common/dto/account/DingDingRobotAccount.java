package net.syl.dandelion.common.dto.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 钉钉机器人参数
 *
 * 参数示例：
 * [{"sms_10":{"regionId":"central","AppKey":"***","appSecret":"***"}}]
 *
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DingDingRobotAccount {

//    private String regionId;
    private String appKey;
    private String appSecret;
    private String accessToken;
}
