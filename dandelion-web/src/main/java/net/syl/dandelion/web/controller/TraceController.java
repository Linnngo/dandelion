package net.syl.dandelion.web.controller;

import net.syl.dandelion.common.vo.BasicResultVO;
import net.syl.dandelion.web.service.TraceService;
import net.syl.dandelion.web.vo.TraceParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trace")
@CrossOrigin(origins = "${dandelion.web.admin.host}", allowCredentials = "true", allowedHeaders = "*")
public class TraceController {

    @Autowired
    private TraceService traceService;

    @PostMapping("/user")
//    @ApiOperation("/获取【当天】用户接收消息的全链路数据")
    public BasicResultVO getUserData(@RequestBody TraceParam traceParam) {
        return traceService.getTraceUserInfo(traceParam.getReceiver());
    }

    @PostMapping("/messageTemplate/{templateId}")
//    @ApiOperation("/获取消息模板全链路数据")
    public BasicResultVO getMessageTemplateData(@PathVariable int templateId) {
        System.out.println(templateId);
        return traceService.getTraceMessageTemplateInfo(templateId);
    }

    @PostMapping("/business/{businessId}")
    public BasicResultVO getBusinessData(@PathVariable long businessId) {
        return traceService.getBusinessData(businessId);
    }

    @PostMapping("/messageTemplate/{templateId}/pie")
//    @ApiOperation("/获取消息模板全链路数据")
    public BasicResultVO getMessageTemplateDataPie(@PathVariable int templateId) {
        System.out.println(templateId);
        return traceService.getTraceMessageTemplateInfoPie(templateId);
    }

    @PostMapping("/messageTemplate/{businessId}/rate")
//    @ApiOperation("/获取消息模板全链路数据")
    public BasicResultVO getMessageTemplateClickCount(@PathVariable long businessId) {
        return traceService.getTraceBusinessClickCount(businessId);
    }

    @PostMapping("/messageTemplate/{businessId}/clickrate")
//    @ApiOperation("/获取消息模板全链路数据")
    public BasicResultVO getMessageTemplateClickClickRate(@PathVariable long businessId) {
        return traceService.getTraceBusinessClickRate(businessId);
    }
}