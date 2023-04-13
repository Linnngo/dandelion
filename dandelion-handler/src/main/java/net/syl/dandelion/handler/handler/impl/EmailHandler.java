package net.syl.dandelion.handler.handler.impl;


import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import com.google.common.base.Throwables;
import com.sun.mail.util.MailSSLSocketFactory;
import lombok.extern.slf4j.Slf4j;
import net.syl.dandelion.common.dto.model.EmailContentModel;
import net.syl.dandelion.common.enums.ChannelType;
import net.syl.dandelion.handler.handler.AbstractHandler;
import net.syl.dandelion.support.entity.MessageTemplate;
import net.syl.dandelion.support.entity.TaskInfo;
import net.syl.dandelion.support.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static net.syl.dandelion.common.constant.DandelionConstant.DANDELION_HANDLER_PREFIX;
import static net.syl.dandelion.common.constant.SendAccountConstant.EMAIL_ACCOUNT_PREFIX;

/**
 * 邮件发送处理
 */
@Component
@Slf4j
public class EmailHandler extends AbstractHandler{

    @Autowired
    private ConfigService config;

    public EmailHandler() {
        channelCode = ChannelType.EMAIL.getCode();
    }

    @Override
    public Boolean doHandler(TaskInfo taskInfo) {
        EmailContentModel emailContentModel = (EmailContentModel) taskInfo.getContentModel();
        MailAccount account = getAccountConfig(taskInfo.getSendAccount());

        try {
            MailUtil.send(account, taskInfo.getReceivers(), emailContentModel.getTitle(),
                    emailContentModel.getContent(), true, null);
        } catch (Exception e) {
            log.error("EmailHandler#handler fail!{},params:{}", Throwables.getStackTraceAsString(e), taskInfo);
            throw e;
        }
        return true;
    }

    /**
     * 获取账号信息合配置
     *
     * @return
     */
    private MailAccount getAccountConfig(Integer sendAccount) {
        MailAccount account = config.getProperty(DANDELION_HANDLER_PREFIX + EMAIL_ACCOUNT_PREFIX + sendAccount, MailAccount.class);

        try {
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
            account.setCustomProperty("mail.smtp.ssl.socketFactory", sf).setTimeout(25000).setConnectionTimeout(25000);
        } catch (Exception e) {
            log.error("EmailHandler#getAccount fail!{}", Throwables.getStackTraceAsString(e));
        }
        return account;
    }

    @Override
    public void recall(MessageTemplate messageTemplate) {

    }
}
