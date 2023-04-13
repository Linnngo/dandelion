package net.syl.dandelion.cron.xxl.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;
import com.xxl.job.core.biz.model.ReturnT;
import lombok.extern.slf4j.Slf4j;
import net.syl.dandelion.common.enums.RespStatusEnum;
import net.syl.dandelion.common.vo.BasicResultVO;
import net.syl.dandelion.cron.xxl.client.JobInfoClient;
import net.syl.dandelion.cron.xxl.entity.XxlJobGroup;
import net.syl.dandelion.cron.xxl.entity.XxlJobInfo;
import net.syl.dandelion.cron.xxl.service.CronTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CronTaskServiceImpl implements CronTaskService {

    @Autowired
    private JobInfoClient jobInfoClient;

    @Override
    public BasicResultVO saveCronTask(XxlJobInfo xxlJobInfo) {
        ReturnT<String> returnT = null;

        try {
            returnT = xxlJobInfo.getId() == 0 ? jobInfoClient.addCronTask(xxlJobInfo) : jobInfoClient.updateCronTask(xxlJobInfo);;
            if (ReturnT.SUCCESS_CODE == returnT.getCode()){
                if (returnT.getContent() == null) {
                    return BasicResultVO.success();
                } else {
                    return new BasicResultVO(RespStatusEnum.SUCCESS, returnT.getContent());
                }
            }
        } catch (Exception e) {
            log.error("CronTaskService#saveTask fail,e:{},param:{},response:{}", Throwables.getStackTraceAsString(e)
                    , JSON.toJSONString(xxlJobInfo), JSON.toJSONString(returnT));
        }

        return BasicResultVO.fail(RespStatusEnum.SERVICE_ERROR, JSON.toJSONString(returnT));
    }

    @Override
    public BasicResultVO deleteCronTask(Integer taskId) {
        ReturnT<String> returnT = null;

        try {
            returnT = jobInfoClient.deleteCronTask(taskId);
            if (ReturnT.SUCCESS_CODE == returnT.getCode()) {
                return BasicResultVO.success();
            }
        } catch (Exception e) {
            log.error("CronTaskService#deleteTask fail,e:{},param:{},response:{}", Throwables.getStackTraceAsString(e)
                    , taskId, JSON.toJSONString(returnT));
        }
        return BasicResultVO.fail(RespStatusEnum.SERVICE_ERROR, JSON.toJSONString(returnT));
    }

    @Override
    public BasicResultVO startCronTask(Integer taskId) {
        ReturnT<String> returnT = null;

        try {
            returnT = jobInfoClient.startCronTask(taskId);
            if (ReturnT.SUCCESS_CODE == returnT.getCode()){
                return BasicResultVO.success();
            }
        } catch (Exception e) {
            log.error("CronTaskService#startTask fail,e:{},param:{},response:{}", Throwables.getStackTraceAsString(e)
                    , taskId, JSON.toJSONString(returnT));
        }
        return BasicResultVO.fail(RespStatusEnum.SERVICE_ERROR, JSON.toJSONString(returnT));
    }

    @Override
    public BasicResultVO stopCronTask(Integer taskId) {
        ReturnT<String> returnT = null;

        try {
            returnT = jobInfoClient.stopCronTask(taskId);
            if (ReturnT.SUCCESS_CODE == returnT.getCode()){
                return BasicResultVO.success();
            }
        } catch (Exception e) {
            log.error("CronTaskService#stopTask fail,e:{},param:{},response:{}", Throwables.getStackTraceAsString(e)
                    , taskId, JSON.toJSONString(returnT));
        }
        return BasicResultVO.fail(RespStatusEnum.SERVICE_ERROR, JSON.toJSONString(returnT));
    }

    @Override
    public BasicResultVO getGroupId(String appname, String title) {
        String result = null;

        try {
            result = jobInfoClient.getGroup(appname, title);
            Integer id = JSON.parseObject(result).getJSONArray("data").getJSONObject(0).getInteger("id");
            if (id != null){
                return BasicResultVO.success(id);
            }
        } catch (Exception e) {
            log.error("CronTaskService#getGroupId fail,e:{},param:{},response:{}", Throwables.getStackTraceAsString(e)
                    , "appname:" + appname + ", title:" + title, JSON.toJSONString(result));
        }
        return BasicResultVO.fail(RespStatusEnum.SERVICE_ERROR, JSON.toJSONString(result));
    }

    @Override
    public BasicResultVO createGroup(XxlJobGroup xxlJobGroup) {
        ReturnT<String> returnT = null;

        try {
            returnT = jobInfoClient.createGroup(xxlJobGroup);
            if (ReturnT.SUCCESS_CODE == returnT.getCode()){
                return BasicResultVO.success();
            }
        } catch (Exception e) {
            log.error("CronTaskService#createGroup fail,e:{},param:{},response:{}", Throwables.getStackTraceAsString(e)
                    , JSON.toJSONString(xxlJobGroup), JSON.toJSONString(returnT));
        }
        return BasicResultVO.fail(RespStatusEnum.SERVICE_ERROR, JSON.toJSONString(returnT));
    }
}
