package net.syl.dandelion.cron.xxl.client;

import com.xxl.job.core.biz.model.ReturnT;
import net.syl.dandelion.cron.xxl.entity.XxlJobGroup;
import net.syl.dandelion.cron.xxl.entity.XxlJobInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "xxl-job-client", url = "${xxl.job.admin.addresses}")
public interface JobInfoClient {

    @PostMapping(value = "/jobinfo/add", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ReturnT<String>  addCronTask(@RequestBody XxlJobInfo jobInfo);

    @PostMapping(value = "/jobinfo/update", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ReturnT<String>  updateCronTask(@RequestBody XxlJobInfo jobInfo);

    @PostMapping("/jobinfo/remove")
    ReturnT<String>  deleteCronTask(@RequestParam("id") Integer taskId);

    @PostMapping("/jobinfo/start")
    ReturnT<String>  startCronTask(@RequestParam("id") Integer taskId);

    @PostMapping("/jobgroup/pageList")
    String getGroup(@RequestParam String appname, @RequestParam String title);

    @PostMapping(value = "/jobgroup/save", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ReturnT<String> createGroup(XxlJobGroup xxlJobGroup);

    @PostMapping("/jobinfo/stop")
    ReturnT<String> stopCronTask(@RequestParam("id") int taskId);
}
