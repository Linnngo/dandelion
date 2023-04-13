package net.syl.dandelion.support.aop;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.syl.dandelion.common.entity.SimpleAnchorLog;
import net.syl.dandelion.common.vo.BasicResultVO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnchorLog {

    /**
     * 业务ID
     */
    Long bizId;

    /**
     * 结果
     */
    BasicResultVO response;

    /**
     * 收信人
     */
    Set<String> receivers;

    /**
     * 时间
     */
    LocalDateTime localDateTime;

    /**
     * 模板ID
     */
    Long templateId;

    /**
     * 标签
     */
    String tag;

    public static SimpleAnchorLog getSimpleAnchorLog(AnchorLog anchorLog){
        SimpleAnchorLog.anchorItem anchorItem = SimpleAnchorLog.anchorItem.builder()
                .time(anchorLog.getLocalDateTime())
                .msg(anchorLog.getResponse().getMsg()).build();
        return SimpleAnchorLog.builder()
                .businessId(anchorLog.getBizId())
                .templateId(anchorLog.getTemplateId())
                .anchorList(new ArrayList<>(Collections.singletonList(anchorItem))).build();
    }
}
