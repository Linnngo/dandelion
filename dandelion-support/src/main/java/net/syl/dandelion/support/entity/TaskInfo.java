package net.syl.dandelion.support.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.syl.dandelion.common.dto.model.ContentModel;
import net.syl.dandelion.support.mapper.WriteClassNameTypeHandler;

import java.util.Set;

/**
 * 发送任务信息
 *
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "task_info", autoResultMap = true)
public class TaskInfo {

    /**
     * 业务Id(数据追踪使用)
     * 生成逻辑参考 TaskInfoUtils
     */
    @TableId
    private Long businessId;

    /**
     * 消息模板Id
     */
    private Long messageTemplateId;

    /**
     * 接收者
     */
    @TableField(value = "receivers", typeHandler = FastjsonTypeHandler.class)
    private Set<String> receivers;

    /**
     * 发送的Id类型
     */
    private Integer idType;

    /**
     * 发送渠道
     */
    private Integer sendChannel;

    /**
     * 模板类型
     */
    private Integer templateType;

    /**
     * 消息类型
     */
    private Integer msgType;

    /**
     * 屏蔽类型
     */
    private Integer shieldType;

    /**
     * 发送文案模型
     * message_template表存储的content是JSON(所有内容都会塞进去)
     * 不同的渠道要发送的内容不一样(比如发push会有img，而短信没有)
     * 所以会有ContentModel
     */
    @TableField(value = "content_model", typeHandler = WriteClassNameTypeHandler.class)
    private ContentModel contentModel;

    /**
     * 发送账号（邮件下可有多个发送账号、短信可有多个发送账号..）
     */
    private Integer sendAccount;

    /**
     * 消息消费状态
     * 0表示未到达交换机，1表示已到达交换机未消费，2表示消费成功，3表示消费失败
     */
    @Builder.Default
    private Integer status = 0;

    private Long sendTime;

//    /**
//     * 运营商返回回执
//     */
//    @TableField(value = "receipts", typeHandler = FastjsonTypeHandler.class)
//    private Set<String> receipts = new HashSet<>();
}
