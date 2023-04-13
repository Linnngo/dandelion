package net.syl.dandelion.handler.handler.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import net.syl.dandelion.common.enums.ChannelType;
import net.syl.dandelion.handler.entity.sms.SmsLoadBalanceParam;
import net.syl.dandelion.handler.handler.AbstractHandler;
import net.syl.dandelion.handler.script.SmsScriptHolder;
import net.syl.dandelion.support.entity.MessageTemplate;
import net.syl.dandelion.support.entity.SmsRecord;
import net.syl.dandelion.support.entity.TaskInfo;
import net.syl.dandelion.support.mapper.SmsRecordMapper;
import net.syl.dandelion.support.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static net.syl.dandelion.common.constant.DandelionConstant.DANDELION_HANDLER_PREFIX;
import static net.syl.dandelion.common.constant.DandelionConstant.SMS_LOAD_BALANCE_PREFIX;

@Component
@Slf4j
public class SmsHandler extends AbstractHandler {

    @Autowired
    private SmsScriptHolder smsScriptHolder;
    @Autowired
    private ConfigService config;
    @Autowired
    private SmsRecordMapper smsRecordMapper;

    public SmsHandler() {
        channelCode = ChannelType.SMS.getCode();
    }

    @Override
    public Boolean doHandler(TaskInfo taskInfo) {
        try {
            Integer[] supplierCodes = loadBalance(getSmsLoadBalanceConfig(taskInfo.getMsgType()));
            for (Integer supplierCode : supplierCodes) {
                List<SmsRecord> recordList = smsScriptHolder.route(supplierCode).send(taskInfo);
                if (CollUtil.isNotEmpty(recordList)) {
                    smsRecordMapper.batchInsert(recordList);
                    return true;
                }
            }
        } catch (Exception e) {
            log.error("SmsHandler#handler fail:{},params:{}",
                    Throwables.getStackTraceAsString(e), JSON.toJSONString(taskInfo));
        }
        return false;
    }

    /**
     * 负载均衡，挑选出第一个，后面的按顺序当备用。
     * @param list
     * @return
     */
    public Integer[] loadBalance(List<SmsLoadBalanceParam> list){
        // list中只有一个元素
        int size = list.size();
        if (size == 1) {
            return new Integer[]{list.get(0).getSupplierCode()};
        }
        
        int total = 0;
        for (SmsLoadBalanceParam smsLoadBalanceParam : list) {
            total += smsLoadBalanceParam.getWeights();
        }

        // 生成一个随机数[1,total]，看落到哪个区间
        int index = RandomUtil.randomInt(0, total) + 1;
        int border = 0;

        Integer[] supplierCodes = new Integer[size];
        for (int i = 0; i < size; i++) {
            border += list.get(i).getWeights();
            if (index <= border){
                supplierCodes[0] = list.get(i).getSupplierCode();
                // 备用渠道商
                for (int j = 1; j < size; j++){
                    supplierCodes[j] = list.get((i+j) % size).getSupplierCode();
                }
                return supplierCodes;
            }
        }
        return new Integer[]{list.get(0).getSupplierCode()};
    }

    @Override
    public void recall(MessageTemplate messageTemplate) {

    }

    private List<SmsLoadBalanceParam> getSmsLoadBalanceConfig(Integer msgType) {
        List<SmsLoadBalanceParam> paramList = config.getPropertyList(DANDELION_HANDLER_PREFIX + SMS_LOAD_BALANCE_PREFIX +
                msgType, SmsLoadBalanceParam.class);

        if (CollUtil.isEmpty(paramList)){
            // 默认使用腾讯云
            paramList.add(new SmsLoadBalanceParam(100, 10));
            return paramList;
        }

        return paramList;
    }
}
