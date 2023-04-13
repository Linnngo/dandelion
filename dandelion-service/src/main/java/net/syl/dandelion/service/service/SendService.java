package net.syl.dandelion.service.service;


import net.syl.dandelion.service.entity.BatchSendRequest;
import net.syl.dandelion.service.entity.SendRequest;
import net.syl.dandelion.service.entity.SendResponse;

/**
 * 发送接口
 */
public interface SendService {

    /**
     * 单文案发送接口
     * @param sendRequest
     * @return
     */
    SendResponse send(SendRequest sendRequest);


    /**
     * 多文案发送接口
     * @param batchSendRequest
     * @return
     */
    SendResponse batchSend(BatchSendRequest batchSendRequest);
}
