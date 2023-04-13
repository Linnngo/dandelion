package net.syl.dandelion.web.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import net.syl.dandelion.common.constant.DandelionConstant;
import net.syl.dandelion.common.enums.AuditStatus;
import net.syl.dandelion.common.enums.MessageStatus;
import net.syl.dandelion.common.enums.RespStatusEnum;
import net.syl.dandelion.common.enums.TemplateType;
import net.syl.dandelion.common.vo.BasicResultVO;
import net.syl.dandelion.support.entity.MessageTemplate;
import net.syl.dandelion.support.mapper.MessageTemplateMapper;
import net.syl.dandelion.web.client.CronClient;
import net.syl.dandelion.web.service.MessageTemplateService;
import net.syl.dandelion.web.vo.MessageTemplateParam;
import net.syl.dandelion.web.vo.MessageTemplateVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class MessageTemplateServiceImpl implements MessageTemplateService {

    @Autowired
    private MessageTemplateMapper messageTemplateMapper;
    @Autowired
    private CronClient cronClient;

    @Override
    public BasicResultVO saveOrUpdate(MessageTemplate messageTemplate) {
        messageTemplate.setUpdated(Math.toIntExact(DateUtil.currentSeconds()));
        int result;
        try {
            if (messageTemplate.getId() == null) {
                initStatus(messageTemplate);
                result = messageTemplateMapper.insert(messageTemplate);
            } else {
                resetStatus(messageTemplate);
                result = messageTemplateMapper.updateById(messageTemplate);
            }
            if (result == 1) {
                return BasicResultVO.success();
            } else {
                return BasicResultVO.fail(JSON.toJSONString(messageTemplate));
            }
        } catch (Exception e) {
            log.error("MessageTemplateService#saveOrUpdate fail,e:{},param:{}"
                    , Throwables.getStackTraceAsString(e), JSON.toJSONString(messageTemplate));
        }
        return BasicResultVO.fail(RespStatusEnum.SERVICE_ERROR, JSON.toJSONString(messageTemplate));
    }


    @Override
    public BasicResultVO queryList(MessageTemplateParam param) {
        try {
            Page<MessageTemplate> page = new Page<>(param.getPage(), param.getPerPage());
            messageTemplateMapper.selectPage(page, null);
            MessageTemplateVo messageTemplateVo = MessageTemplateVo.builder()
                    .count(page.getTotal()).rows(page.getRecords()).build();
            return BasicResultVO.success(messageTemplateVo);
        } catch (Exception e) {
            log.error("MessageTemplateService#queryList,e:{},param:{}"
                    , Throwables.getStackTraceAsString(e), JSON.toJSONString(param));
        }
        return BasicResultVO.fail(RespStatusEnum.SERVICE_ERROR, JSON.toJSONString(param));
    }

    @Override
    public BasicResultVO queryById(Long id) {
        try {
            MessageTemplate messageTemplate = messageTemplateMapper.selectById(id);
            return BasicResultVO.success(messageTemplate);
        } catch (Exception e) {
            log.error("MessageTemplateService#queryById,e:{},param:{}"
                    , Throwables.getStackTraceAsString(e), id);
        }
        return BasicResultVO.fail(RespStatusEnum.SERVICE_ERROR, id.toString());
    }

    @Override
    public BasicResultVO deleteById(List idList) {
        try {
            List<MessageTemplate> messageTemplateList = messageTemplateMapper.selectBatchIds(idList);
            for (MessageTemplate messageTemplate : messageTemplateList) {
                messageTemplate.setIsDeleted(DandelionConstant.TRUE);
                Integer taskId = messageTemplate.getCronTaskId();
                if (taskId !=null && taskId > 0) {
                    cronClient.delete(taskId);
                }
            }
            int result = messageTemplateMapper.deleteBatchIds(idList);
            if (result >= 1) {
                return BasicResultVO.success();
            } else {
                return BasicResultVO.fail();
            }
        } catch (Exception e) {
            log.error("MessageTemplateService#deleteById,e:{},param:{}"
                    , Throwables.getStackTraceAsString(e), idList);
        }
        return BasicResultVO.fail(RespStatusEnum.SERVICE_ERROR, idList.toString());
    }

    @Override
    public BasicResultVO startCronTask(Long id) {
        try {
            // 1.获取消息模板的信息
            MessageTemplate messageTemplate = messageTemplateMapper.selectById(id);

            // 2.动态创建或更新定时任务
            BasicResultVO saveResult = cronClient.save(messageTemplate);

            // 3.获取taskId(如果本身存在则复用原有任务，如果不存在则得到新建后任务ID)
            Integer taskId = messageTemplate.getCronTaskId();
            if (taskId == null && RespStatusEnum.SUCCESS.getCode().equals(saveResult.getStatus()) && saveResult.getData() != null) {
                taskId = Integer.valueOf(String.valueOf(saveResult.getData()));
            } else {
                return saveResult;
            }

            // 4. 启动定时任务
            BasicResultVO startResult = cronClient.start(taskId);
            messageTemplate.setMsgStatus(MessageStatus.RUN.getCode())
                    .setCronTaskId(taskId)
                    .setUpdated(Math.toIntExact(DateUtil.currentSeconds()));
            messageTemplateMapper.updateById(messageTemplate);
            return startResult;
        } catch (Exception e) {
            log.error("MessageTemplateService#startCronTask,e:{},param:{}"
                    , Throwables.getStackTraceAsString(e), id);
        }
        return BasicResultVO.fail(RespStatusEnum.SERVICE_ERROR);
    }

    @Override
    public BasicResultVO stopCronTask(Long id) {
        try {
            // 1.修改模板状态
            MessageTemplate messageTemplate = messageTemplateMapper.selectById(id);
            messageTemplate.setMsgStatus(MessageStatus.STOP.getCode()).setUpdated(Math.toIntExact(DateUtil.currentSeconds()));
            messageTemplateMapper.updateById(messageTemplate);

            // 2.暂停定时任务
            return cronClient.stop(messageTemplate.getCronTaskId());
        } catch (Exception e) {
            log.error("MessageTemplateService#stopCronTask,e:{},param:{}"
                    , Throwables.getStackTraceAsString(e), id);
        }
        return BasicResultVO.fail(RespStatusEnum.SERVICE_ERROR);
    }

    /**
     * 初始化状态信息
     * TODO 创建者 修改者 团队
     *
     * @param messageTemplate
     */
    private void initStatus(MessageTemplate messageTemplate) {
        messageTemplate.setFlowId(StrUtil.EMPTY)
                .setMsgStatus(MessageStatus.INIT.getCode()).setAuditStatus(AuditStatus.WAIT_AUDIT.getCode())
                .setCreator("Linnngo").setUpdator("Linnngo").setTeam("公众号Linnngo").setAuditor("Linnngo")
                .setCreated(Math.toIntExact(DateUtil.currentSeconds()))
                .setIsDeleted(DandelionConstant.FALSE);

    }

    /**
     * 1. 重置模板的状态
     * 2. 修改定时任务信息(如果存在)
     *
     * @param messageTemplate
     */
    public void resetStatus(MessageTemplate messageTemplate) {
        messageTemplate.setUpdator(messageTemplate.getUpdator())
                .setMsgStatus(MessageStatus.INIT.getCode()).setAuditStatus(AuditStatus.WAIT_AUDIT.getCode());
        MessageTemplate oldMessageTemplate = messageTemplateMapper.selectById(messageTemplate.getId());

        if (messageTemplate.getCronTaskId() != null && TemplateType.CLOCKING.getCode().equals(messageTemplate.getTemplateType())) {

            if (!StrUtil.equals(messageTemplate.getCronCrowdPath(),oldMessageTemplate.getCronCrowdPath())
                    || !StrUtil.equals(messageTemplate.getExpectPushTime(), oldMessageTemplate.getExpectPushTime())) {
                cronClient.save(messageTemplate);
                cronClient.stop(messageTemplate.getCronTaskId());
            }
        }
    }
}
