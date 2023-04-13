package net.syl.dandelion.cron.controller;

import net.syl.dandelion.common.vo.BasicResultVO;
import net.syl.dandelion.cron.xxl.entity.XxlJobInfo;
import net.syl.dandelion.cron.xxl.service.CronTaskService;
import net.syl.dandelion.cron.xxl.utils.XxlJobUtils;
import net.syl.dandelion.support.entity.MessageTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cron")
public class CronController {
    @Autowired
    private CronTaskService cronTaskService;
    @Autowired
    private XxlJobUtils xxlJobUtils;

    @PostMapping("/save")
    public BasicResultVO save(@RequestBody MessageTemplate messageTemplate) {
        XxlJobInfo xxlJobInfo = xxlJobUtils.buildXxlJobInfo(messageTemplate);
        return cronTaskService.saveCronTask(xxlJobInfo);
    }

    @PostMapping("/stop/{id}")
    BasicResultVO stop(@PathVariable int id) {
        return cronTaskService.stopCronTask(id);
    }

    @PostMapping("/cron/start/{id}")
    BasicResultVO start(@PathVariable int id) {
        return cronTaskService.startCronTask(id);
    }

    @DeleteMapping("/cron/remove/{id}")
    BasicResultVO delete(@PathVariable int id) {
        return cronTaskService.deleteCronTask(id);
    };
}
