package net.syl.dandelion.handler.script.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import com.tencentcloudapi.sms.v20210111.models.SendStatus;
import net.syl.dandelion.common.constant.DandelionConstant;
import net.syl.dandelion.common.dto.account.TencentSmsAccount;
import net.syl.dandelion.common.dto.model.SmsContentModel;
import net.syl.dandelion.common.enums.SmsStatus;
import net.syl.dandelion.handler.script.AbstractSmsScript;
import net.syl.dandelion.support.entity.SmsRecord;
import net.syl.dandelion.support.entity.TaskInfo;
import net.syl.dandelion.support.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static net.syl.dandelion.common.constant.DandelionConstant.DANDELION_HANDLER_PREFIX;
import static net.syl.dandelion.common.constant.SendAccountConstant.SMS_TENCENT_ACCOUNT_PREFIX;
import static net.syl.dandelion.common.constant.SendAccountConstant.SMS_TENCENT_TEMPLATE_PREFIX;

@Component("tencentSmsScript")
public class TencentSmsScript extends AbstractSmsScript {

    @Autowired
    private ConfigService config;
    private static final Integer PHONE_NUM = 11;

    public TencentSmsScript(){
        supplierCode = DandelionConstant.TENCENT_SMS_CODE;
    }

    @Override
    public List<SmsRecord> send(TaskInfo taskInfo) {
        try {
            TencentSmsAccount account = config.getProperty(DANDELION_HANDLER_PREFIX +
                    SMS_TENCENT_ACCOUNT_PREFIX + taskInfo.getSendAccount(), TencentSmsAccount.class);
            SmsClient client = init(account);
            SendSmsRequest request = assembleReq(taskInfo, account);
            SendSmsResponse res = client.SendSms(request);
            List<SmsRecord> recordList = assembleSmsRecord(taskInfo, res, account);
            System.out.println(recordList);
            return recordList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private SendSmsRequest assembleReq(TaskInfo taskInfo, TencentSmsAccount account){
        SendSmsRequest req = new SendSmsRequest();
        Set<String> receivers = taskInfo.getReceivers();
        SmsContentModel contentModel = (SmsContentModel)taskInfo.getContentModel();
        String[] phoneNumberSet1 = receivers.toArray(new String[receivers.size() - 1]);
        req.setTemplateId(config.getProperty(DANDELION_HANDLER_PREFIX +
                SMS_TENCENT_TEMPLATE_PREFIX + taskInfo.getMessageTemplateId(), ""));
        req.setPhoneNumberSet(phoneNumberSet1);
        req.setSmsSdkAppId(account.getSmsSdkAppId());
        req.setSignName(account.getSignName());
        req.setTemplateParamSet(contentModel.getContent());
        req.setSessionContext(IdUtil.fastSimpleUUID());
        return req;
    }

    private List<SmsRecord> assembleSmsRecord(TaskInfo taskInfo, SendSmsResponse response, TencentSmsAccount tencentSmsAccount) {
        if (response == null) {
            return null;
        }
        SendStatus[] sendStatusSet = response.getSendStatusSet();
        if (ArrayUtil.isEmpty(sendStatusSet)){
            return null;
        }

        List<SmsRecord> smsRecordList = new ArrayList<>();
        for (SendStatus sendStatus : sendStatusSet) {

            // 腾讯返回的电话号有前缀，这里取巧直接翻转获取手机号
            String phone = new StringBuilder(new StringBuilder(sendStatus.getPhoneNumber())
                    .reverse().substring(0, PHONE_NUM)).reverse().toString();

            SmsRecord smsRecord = SmsRecord.builder()
                    .id(taskInfo.getBusinessId())
                    .sendDate(Integer.valueOf(DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN)))
                    .messageTemplateId(taskInfo.getMessageTemplateId())
                    .phone(Long.valueOf(phone))
                    .supplierId(tencentSmsAccount.getSupplierId())
                    .supplierName(tencentSmsAccount.getSupplierName())
                    .msgContent(JSON.toJSONString(taskInfo.getContentModel()))
                    .seriesId(sendStatus.getSerialNo())
                    .chargingNum(Math.toIntExact(sendStatus.getFee()))
                    .status(SmsStatus.SEND_SUCCESS.getCode())
                    .reportContent(sendStatus.getCode())
                    .created(Math.toIntExact(DateUtil.currentSeconds()))
                    .updated(Math.toIntExact(DateUtil.currentSeconds()))
                    .build();

            smsRecordList.add(smsRecord);
        }
        return smsRecordList;
    }

    /**
     * 初始化 client
     *
     * @param account
     */
    private SmsClient init(TencentSmsAccount account) {
        Credential cred = new Credential(account.getSecretId(), account.getSecretKey());
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint(account.getUrl());
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);
        SmsClient client = new SmsClient(cred, account.getRegion(), clientProfile);
        return client;
    }
}
