package net.syl.dandelion.service.impl.controller;

import net.syl.dandelion.service.entity.BatchSendRequest;
import net.syl.dandelion.service.entity.SendRequest;
import net.syl.dandelion.service.entity.SendResponse;
import net.syl.dandelion.service.service.SendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/service")
public class SendController {

    @Autowired
    private SendService sendService;

    @PostMapping("/send")
    @ResponseBody
    public SendResponse send(@RequestBody SendRequest sendRequest){
        System.out.println(sendRequest);
        return sendService.send(sendRequest);
    }

    @PostMapping("/batch")
    @ResponseBody
    public SendResponse batchSend(@RequestBody BatchSendRequest batchSendRequest){
        return sendService.batchSend(batchSendRequest);
    }
}
