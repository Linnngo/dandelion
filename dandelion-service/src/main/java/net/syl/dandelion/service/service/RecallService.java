package net.syl.dandelion.service.service;


import net.syl.dandelion.service.entity.SendRequest;
import net.syl.dandelion.service.entity.SendResponse;

/**
 * 撤回接口
 */
public interface RecallService {


    /**
     * 根据模板ID撤回消息
     *
     * @param sendRequest
     * @return
     */
    SendResponse recall(SendRequest sendRequest);
}
