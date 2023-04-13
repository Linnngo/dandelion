package net.syl.dandelion.handler.handler.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenResponse;
import com.aliyun.dingtalkrobot_1_0.models.OrgGroupSendHeaders;
import com.aliyun.dingtalkrobot_1_0.models.OrgGroupSendRequest;
import com.aliyun.dingtalkrobot_1_0.models.OrgGroupSendResponse;
import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import net.syl.dandelion.common.dto.account.DingDingRobotAccount;
import net.syl.dandelion.common.dto.model.DingDingRobotContentModel;
import net.syl.dandelion.common.enums.ChannelType;
import net.syl.dandelion.handler.handler.AbstractHandler;
import net.syl.dandelion.support.entity.MessageTemplate;
import net.syl.dandelion.support.entity.TaskInfo;
import net.syl.dandelion.support.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static net.syl.dandelion.common.constant.DandelionConstant.DANDELION_HANDLER_PREFIX;
import static net.syl.dandelion.common.constant.SendAccountConstant.DING_DING_ROBOT_ACCOUNT_PREFIX;

@Slf4j
@Component
public class DingDingRobotHandler extends AbstractHandler {

    @Autowired
    private ConfigService configService;

    private Config config = new Config();
    private com.aliyun.dingtalkrobot_1_0.Client robotClient;
    private com.aliyun.dingtalkoauth2_1_0.Client oauthClient;

    public DingDingRobotHandler() {
        channelCode = ChannelType.DING_DING_ROBOT.getCode();
    }

    @Override
    public Boolean doHandler(TaskInfo taskInfo) {
        DingDingRobotContentModel contentModel = (DingDingRobotContentModel) taskInfo.getContentModel();
        JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(contentModel));
        String msgKey = jsonObject.getString("msgKey");
        String msgParam = jsonObject.getString(msgKey);
        DingDingRobotAccount account = getAccount(taskInfo.getSendAccount());
        OrgGroupSendHeaders orgGroupSendHeaders = new OrgGroupSendHeaders();
        orgGroupSendHeaders.xAcsDingtalkAccessToken = account.getAccessToken();
        OrgGroupSendRequest orgGroupSendRequest = new OrgGroupSendRequest()
                .setRobotCode(account.getAppKey())
                .setOpenConversationId(taskInfo.getReceivers().toArray()[0].toString())
                .setMsgKey(msgKey)
                .setMsgParam(msgParam);
        try {
            OrgGroupSendResponse res = robotClient.orgGroupSendWithOptions(orgGroupSendRequest, orgGroupSendHeaders, new com.aliyun.teautil.models.RuntimeOptions());
//            taskInfo.getReceipts().add(res.body.processQueryKey);
            return true;
        } catch (TeaException err) {
            System.out.println(err.code);
            System.out.println(err.message);
            System.out.println(err.statusCode);
            System.out.println(err.data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void recall(MessageTemplate messageTemplate) {

    }

    /**
     * 获取钉钉机器人access_token
     */
    private DingDingRobotAccount getAccount(Integer sendAccount){
        DingDingRobotAccount account = configService.getProperty(DANDELION_HANDLER_PREFIX + DING_DING_ROBOT_ACCOUNT_PREFIX + sendAccount, DingDingRobotAccount.class);
        com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenRequest getAccessTokenRequest = new com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenRequest()
                .setAppKey(account.getAppKey())
                .setAppSecret(account.getAppSecret());
        try {
            GetAccessTokenResponse res = oauthClient.getAccessToken(getAccessTokenRequest);
            account.setAccessToken(res.body.accessToken);
            System.out.println(res.body.accessToken);
        } catch (TeaException err) {
            System.out.println(err.code);
            System.out.println(err.message);
            System.out.println(err.statusCode);
            System.out.println(err.data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return account;
    }

    @PostConstruct
    public void initClient() {
        config.protocol = "https";
        config.regionId = "central";
        try {
            robotClient = new com.aliyun.dingtalkrobot_1_0.Client(config);
            oauthClient = new com.aliyun.dingtalkoauth2_1_0.Client(config);
        } catch (Exception e) {
            log.error(Throwables.getStackTraceAsString(e));
        }
    }
}