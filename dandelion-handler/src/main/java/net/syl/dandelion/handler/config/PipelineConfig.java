package net.syl.dandelion.handler.config;

import net.syl.dandelion.handler.deduplication.impl.FrequencyDeduplicationProcessor;
import net.syl.dandelion.handler.deduplication.impl.SlideWindowDeduplicationProcessor;
import net.syl.dandelion.handler.discard.DiscardMessageProcessor;
import net.syl.dandelion.handler.enums.BusinessCode;
import net.syl.dandelion.handler.handler.HandlerProcessor;
import net.syl.dandelion.support.pipeline.ProcessChain;
import net.syl.dandelion.support.pipeline.ProcessController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * 责任链配置
 */
@Configuration
public class PipelineConfig {

    @Autowired
    private DiscardMessageProcessor discardMessageProcessor;
    @Autowired
    private FrequencyDeduplicationProcessor frequencyDeduplicationProcessor;
    @Autowired
    private SlideWindowDeduplicationProcessor slideWindowDeduplicationProcessor;
    @Autowired
    private HandlerProcessor handlerProcessor;

    @Bean
    public ProcessController processController(){
        return new ProcessController();
    }

    /**
     * 普通发送执行流程
     * 1. 屏蔽特定模板
     * 2. 夜间屏蔽
     * 3. 消息去重
     * @return
     */
    @Bean("commonProcessChain")
    public ProcessChain sendProcessChain() {
        ProcessChain processChain = new ProcessChain();
        processChain.setProcessList(Arrays.asList(discardMessageProcessor, frequencyDeduplicationProcessor,
                slideWindowDeduplicationProcessor, handlerProcessor));
        processController().registerChain(BusinessCode.COMMON.getCode(), processChain);
        return processChain;
    }

}
