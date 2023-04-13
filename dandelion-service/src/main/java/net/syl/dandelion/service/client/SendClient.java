package net.syl.dandelion.service.client;

import net.syl.dandelion.service.entity.BatchSendRequest;
import net.syl.dandelion.service.entity.SendRequest;
import net.syl.dandelion.service.entity.SendResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "dandelion-service", url = "localhost:8081")
public interface SendClient {

    @PostMapping(value = "/service/send", consumes = MediaType.APPLICATION_JSON_VALUE)
    SendResponse send(SendRequest sendRequest);

    @PostMapping(value = "/service/batch", consumes = MediaType.APPLICATION_JSON_VALUE)
    SendResponse batchSend(BatchSendRequest sendRequest);
}
