package net.syl.dandelion.web.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 全链路 请求参数
 *
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TraceParam {

    /**
     * 查看用户的链路信息
     */
    private String receiver;


    /**
     * 模板ID
     */
    private int templateId;


    /**
     * 日期时间(检索短信的条件使用)
     */
    private Long dateTime;




}
