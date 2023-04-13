package net.syl.dandelion.web.service;

import net.syl.dandelion.common.vo.BasicResultVO;

public interface TraceService {
    BasicResultVO getTraceUserInfo(String receiver);

    BasicResultVO getTraceMessageTemplateInfo(int businessId);

    BasicResultVO getBusinessData(long businessId);

    BasicResultVO getTraceMessageTemplateInfoPie(int templateId);

    BasicResultVO getTraceBusinessClickCount(long templateId);

    BasicResultVO getTraceBusinessClickRate(long businessId);
}
