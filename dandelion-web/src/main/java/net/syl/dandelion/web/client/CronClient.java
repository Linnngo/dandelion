package net.syl.dandelion.web.client;

import net.syl.dandelion.common.vo.BasicResultVO;
import net.syl.dandelion.support.entity.MessageTemplate;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "dandelion-cron")
public interface CronClient {

    @PostMapping("/cron/save")
    BasicResultVO save(@RequestBody MessageTemplate messageTemplate);

    @PostMapping("/cron/stop/{id}")
    BasicResultVO stop(@PathVariable int id);

    @PostMapping("/cron/start/{id}")
    BasicResultVO start(@PathVariable int id);

    @DeleteMapping("/cron/remove/{id}")
    BasicResultVO delete(@PathVariable int id);
}
