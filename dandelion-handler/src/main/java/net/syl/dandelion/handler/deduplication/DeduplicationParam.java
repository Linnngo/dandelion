package net.syl.dandelion.handler.deduplication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeduplicationParam {

    /**
     * 去重时间
     * 单位：秒
     */
    private Long time;

    /**
     * 需达到的次数去重
     */
    private Integer maxCount;
}
