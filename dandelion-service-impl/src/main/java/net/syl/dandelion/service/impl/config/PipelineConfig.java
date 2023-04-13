package net.syl.dandelion.service.impl.config;

import net.syl.dandelion.service.enums.BusinessCode;
import net.syl.dandelion.service.impl.processor.*;
import net.syl.dandelion.support.pipeline.ProcessController;
import net.syl.dandelion.support.pipeline.ProcessChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * api层的pipeline配置类
 *
 */
@Configuration
//@ComponentScan(basePackages = {"net.syl.dandelion.service.impl"})
public class PipelineConfig {

    @Autowired
    private PreParamCheckProcessor preParamCheckProcessor;
    @Autowired
    private AssembleProcessor assembleProcessor;
    @Autowired
    private AfterParamCheckProcessor afterParamCheckProcessor;
    @Autowired
    private SendMqProcessor sendMqProcessor;
    @Autowired
    private DieFilterProcessor dieFilterProcessor;

    /**
     * pipeline流程控制器
     * 后续扩展则加BusinessCode和processChain
     * @return
     */
    @Bean
    public ProcessController processController() {
        return new ProcessController();
    }

    /**
     * 普通发送执行流程
     * 1. 前置参数校验
     * 2. 组装参数
     * 3. 后置参数校验
     * 4. 发送消息至MQ
     * @return
     */
    @Bean("sendProcessChain")
    public ProcessChain sendProcessChain() {
        ProcessChain processChain = new ProcessChain();
        processChain.setProcessList(Arrays.asList(preParamCheckProcessor, assembleProcessor,
                afterParamCheckProcessor, dieFilterProcessor, sendMqProcessor));
        processController().registerChain(BusinessCode.SEND.getCode(), processChain);
        return processChain;
    }

    /**
     * 消息撤回执行流程
     * 1.组装参数
     * 2.发送MQ
     * @return
     */
    @Bean("recallProcessChain")
    public ProcessChain recallProcessChain() {
        ProcessChain processChain = new ProcessChain();
        processChain.setProcessList(Arrays.asList(assembleProcessor, sendMqProcessor));
        processController().registerChain(BusinessCode.RECALL.getCode(), processChain);
        return processChain;
    }
}