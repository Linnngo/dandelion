package net.syl.dandelion.cron.xxl.job;

import cn.hutool.core.util.StrUtil;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import net.syl.dandelion.cron.resolver.CSVResolver;
import net.syl.dandelion.cron.pending.CronTaskHandler;
import net.syl.dandelion.support.entity.MessageTemplate;
import net.syl.dandelion.support.mapper.MessageTemplateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class DandelionJob {

    @Autowired
    private CronTaskHandler cronTaskHandler;
    @Autowired
    private ApplicationContext context;
    @Autowired
    private CSVResolver csvResolver;
    @Autowired
    private MessageTemplateMapper messageTemplateMapper;

    @XxlJob("dandelionJob")
    public void execute() throws InterruptedException {
        long messageTemplateId = Long.parseLong(XxlJobHelper.getJobParam());
        MessageTemplate messageTemplate = messageTemplateMapper.selectById(messageTemplateId);

        String path = messageTemplate.getCronCrowdPath();
        if (messageTemplate == null || StrUtil.isBlank(path)) {
            log.error("TaskHandler#handle crowdPath empty! messageTemplateId:{}", messageTemplateId);
            return;
        }
        csvResolver.resolve(path, messageTemplateId);
        cronTaskHandler.doHandle(path, messageTemplate);
    }
}
