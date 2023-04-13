package net.syl.dandelion.handler.script;

import net.syl.dandelion.support.entity.SmsRecord;
import net.syl.dandelion.support.entity.TaskInfo;

import java.util.List;


/**
 * 短信脚本 接口
 *
 */
public interface SmsScript {

    /**
     * 发送短信
     * @param taskInfo
     * @return 渠道商接口返回值

     */
    List<SmsRecord> send(TaskInfo taskInfo);

}
