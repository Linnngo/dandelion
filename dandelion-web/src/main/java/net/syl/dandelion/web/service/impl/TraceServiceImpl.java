package net.syl.dandelion.web.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import net.syl.dandelion.common.constant.DandelionConstant;
import net.syl.dandelion.common.entity.SimpleAnchorLog;
import net.syl.dandelion.common.enums.ChannelType;
import net.syl.dandelion.common.enums.RespStatusEnum;
import net.syl.dandelion.common.vo.BasicResultVO;
import net.syl.dandelion.support.entity.MessageTemplate;
import net.syl.dandelion.support.entity.TaskInfo;
import net.syl.dandelion.support.mapper.MessageTemplateMapper;
import net.syl.dandelion.support.mapper.TaskInfoMapper;
import net.syl.dandelion.web.service.TraceService;
import net.syl.dandelion.web.vo.UserTimeLineVo;
import net.syl.dandelion.web.vo.amis.EchartsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TraceServiceImpl implements TraceService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private MessageTemplateMapper messageTemplateMapper;
    @Autowired
    private TaskInfoMapper taskInfoMapper;

    @Override
    public BasicResultVO getTraceUserInfo(String receiver) {
        try {
            String userKey = DandelionConstant.STREAM_USER_TRACKING_PREFIX + receiver;
            Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(userKey);
            List<SimpleAnchorLog> simpleAnchorLogList = new ArrayList();

            // 取得string转换成simpleAnchorLog
            for (Map.Entry<Object, Object> entry : entries.entrySet()) {
                simpleAnchorLogList.add(JSON
                        .parseObject(entry.getValue().toString(), SimpleAnchorLog.class));
            }

            List<UserTimeLineVo.ItemsVO> items = new ArrayList();
            // 2.封装vo 给到前端渲染展示
            for (SimpleAnchorLog simpleAnchorLog : simpleAnchorLogList) {
                items.add(createUserTimeLineItem(simpleAnchorLog));
            }
            UserTimeLineVo userTimeLineVo = UserTimeLineVo.builder().items(items).build();
            return BasicResultVO.success(userTimeLineVo);
        } catch (Exception e) {
            log.error("TraceService#getTraceUserInfo,e:{},param:{}"
                    , Throwables.getStackTraceAsString(e), receiver);
        }
        return BasicResultVO.fail(RespStatusEnum.SERVICE_ERROR, receiver);
    }

    @Override
    public BasicResultVO getTraceMessageTemplateInfo(int templateId) {
        try {
            MessageTemplate messageTemplate = messageTemplateMapper.selectById((long) templateId);
            if (BeanUtil.isEmpty(messageTemplate, "")) {
                return BasicResultVO.success();
            }
            String key = DandelionConstant.STREAM_TEMPLATE_TRACKING_PREFIX + templateId;
            Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(key);

            List<String> xAxis = new ArrayList();
            List<Integer> seriesData = new ArrayList();
            for (Map.Entry<Object, Object> entry : entries.entrySet()) {
                xAxis.add(entry.getKey().toString());
                seriesData.add(Integer.parseInt(entry.getValue().toString()));
            }
            EchartsVo echartsVo = EchartsVo.builder().xAxisData(xAxis)
                    .seriesData(seriesData)
                    .titleText(messageTemplate.getName())
                    .LegendData("人数")
                    .build();
            return BasicResultVO.success(echartsVo);
        } catch (Exception e) {
            log.error("TraceService#getTraceMessageTemplateInfo,e:{},param:{}"
                    , Throwables.getStackTraceAsString(e), templateId);
        }

        return BasicResultVO.fail(RespStatusEnum.SERVICE_ERROR, String.valueOf(templateId));
    }

    @Override
    public BasicResultVO getBusinessData(long businessId) {

        return null;
    }

    private UserTimeLineVo.ItemsVO createUserTimeLineItem(SimpleAnchorLog simpleAnchorLog) {
        Long templateId = simpleAnchorLog.getTemplateId();
        MessageTemplate messageTemplate = messageTemplateMapper.selectById(templateId);
        String detail = "";

        List<SimpleAnchorLog.anchorItem> items = simpleAnchorLog.getAnchorList().stream().sorted((o1, o2) ->
                Math.toIntExact(Timestamp.valueOf(o1.getTime()).getTime() - Timestamp.valueOf(o2.getTime()).getTime()))
                .collect(Collectors.toList());

        for (SimpleAnchorLog.anchorItem item : items) {
            detail = detail + item.getTime().toString() + ":" + item.getMsg() + "==>";
        }
        if (StrUtil.isNotBlank(detail)) {
            detail = detail.substring(0, detail.length() - 3);
        }

        return UserTimeLineVo.ItemsVO.builder()
                .businessId(simpleAnchorLog.getBusinessId().toString())
                .creator(messageTemplate.getCreator())
                .sendType(ChannelType.getEnumByCode(messageTemplate.getSendChannel()).getDescription())
                .title(messageTemplate.getName())
                .detail(detail)
                .build();
    }

    @Override
    public BasicResultVO getTraceMessageTemplateInfoPie(int templateId) {
        try {
            MessageTemplate messageTemplate = messageTemplateMapper.selectById((long) templateId);
            if (BeanUtil.isEmpty(messageTemplate, "")) {
                return BasicResultVO.success();
            }
            String key = DandelionConstant.STREAM_TEMPLATE_TRACKING_PREFIX + templateId;
            Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(key);
            // 构建Amis要求的数据结构[{}、{}]
            ArrayList<Object> list = new ArrayList<>();
            Map<Object, Object> data = new HashMap<>();
            data.put("arr", list);
            entries.forEach((k, v) -> {
                System.out.println(k + " | " + v);
                HashMap<Object, Object> map = new HashMap<>();
                map.put("name", k);
                map.put("value", v);
                list.add(map);
            });
            return BasicResultVO.success(data);
        } catch (Exception e) {
            log.error("TraceService#getTraceMessageTemplateInfo,e:{},param:{}"
                    , Throwables.getStackTraceAsString(e), templateId);
        }

        return BasicResultVO.fail(RespStatusEnum.SERVICE_ERROR, String.valueOf(templateId));
    }

    @Override
    public BasicResultVO getTraceBusinessClickCount(long businessId) {
        QueryWrapper<TaskInfo> wrapper = new QueryWrapper<>();
        wrapper.select("send_time", "receivers");
        wrapper.eq("business_id", businessId);
        TaskInfo taskInfo = taskInfoMapper.selectOne(wrapper);
        int all = taskInfo.getReceivers().size();
        Long sendTime = taskInfo.getSendTime();
        Long firstFiveMin = stringRedisTemplate.opsForZSet().count(DandelionConstant.STREAM_SHORTURL_CLICKED + businessId, 0, sendTime + 60 * 5 * 1000);
        Long firstTenMin = stringRedisTemplate.opsForZSet().count(DandelionConstant.STREAM_SHORTURL_CLICKED + businessId, 0, sendTime + 60 * 10 * 1000);
        Long firstTwentyMin = stringRedisTemplate.opsForZSet().count(DandelionConstant.STREAM_SHORTURL_CLICKED + businessId, 0, sendTime + 60 * 20 * 1000);
        Long firstThirtyMin = stringRedisTemplate.opsForZSet().count(DandelionConstant.STREAM_SHORTURL_CLICKED + businessId, 0, sendTime + 60 * 30 * 1000);
        Long clicked = stringRedisTemplate.opsForZSet().size(DandelionConstant.STREAM_SHORTURL_CLICKED + businessId);
        ArrayList<String> product = new ArrayList<>();
        product.add("前5分钟内点击");
        product.add("5-10分钟内点击");
        product.add("10-20分钟内点击");
        product.add("20-30分钟内点击");
        product.add("已点击");
        product.add("未点击");
        ArrayList<Object> value = new ArrayList<>();
        value.add(firstFiveMin);
        value.add(firstTenMin - firstFiveMin);
        value.add(firstTwentyMin - firstTenMin);
        value.add(firstThirtyMin - firstTwentyMin);
        value.add(clicked);
        value.add(all - clicked);
        HashMap<Object, Object> map = new HashMap<>();
        map.put("product", product);
        map.put("count", value);
        return BasicResultVO.success(map);
    }

    @Override
    public BasicResultVO getTraceBusinessClickRate(long businessId) {
        QueryWrapper<TaskInfo> wrapper = new QueryWrapper<>();
        wrapper.select("receivers");
        wrapper.eq("business_id", businessId);
        TaskInfo taskInfo = taskInfoMapper.selectOne(wrapper);
        int all = taskInfo.getReceivers().size();
        Long clicked = stringRedisTemplate.opsForZSet().size(DandelionConstant.STREAM_SHORTURL_CLICKED + businessId);
        ArrayList<Object> data = new ArrayList<>();
        HashMap<Object, Object> map = new HashMap<>();
        map.put("name", "已点击");
        map.put("value", clicked);
        HashMap<Object, Object> map2 = new HashMap<>();
        map2.put("name", "未点击");
        map2.put("value", all - clicked);
        data.add(map);
        data.add(map2);
        HashMap<Object, Object> dataMap = new HashMap<>();
        dataMap.put("arr", data);
        return BasicResultVO.success(dataMap);
    }
}