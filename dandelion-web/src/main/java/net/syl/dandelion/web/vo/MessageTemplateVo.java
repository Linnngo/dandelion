package net.syl.dandelion.web.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.syl.dandelion.support.entity.MessageTemplate;

import java.util.List;


/**
 * 消息模板的Vo
 *
 *
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageTemplateVo {
    /**
     * 返回List列表
     */
    private List<MessageTemplate> rows;

    /**
     * 总条数
     */
    private Long count;
}
