package net.syl.dandelion.handler.script;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * sms发送脚本的抽象类
 *
 *
 */
@Slf4j
public abstract class AbstractSmsScript implements SmsScript {

    @Autowired
    private SmsScriptHolder smsScriptHolder;

    /**
     * 标识渠道商的Code
     * 子类初始化的时候指定
     */
    protected Integer supplierCode;

    @PostConstruct
    public void registerProcessScript() {
        smsScriptHolder.putHandler(supplierCode, this);
    }
}
