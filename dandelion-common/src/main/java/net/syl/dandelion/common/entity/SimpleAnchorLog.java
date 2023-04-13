package net.syl.dandelion.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SimpleAnchorLog {

    /**
     * 业务ID
     */
    Long businessId;

    /**
     * 模板ID
     */
    Long templateId;

    /**
     * 埋点整合
     */
    List<anchorItem> anchorList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class anchorItem {
        LocalDateTime time;
        String msg;
    }
}
