package net.syl.dandelion.common.constant;

public class DandelionConstant {

    /**
     * boolean转换
     */
    public final static Integer TRUE = 1;
    public final static Integer FALSE = 0;

    /**
     * cron时间格式
     */
    public final static String CRON_FORMAT = "ss mm HH dd MM ? yyyy-yyyy";

    /**
     * 默认的值
     */
    public final static String JSON_OBJECT_DEFAULT_VALUE = "{}";
    public final static String JSON_ARRAY_DEFAULT_VALUE = "[]";

    /**
     * 模块
     */
    public final static String DANDELION_HANDLER_PREFIX = "dandelion.handler.";

    /**
     * 短信 账号
     */
    public static final String SMS_ACCOUNT_KEY = "smsAccount";
    public static final String SMS_PREFIX = "sms_";

    /**
     * 模板映射
     */
    public static final String SMS_TEMPLATE_ID_KEY = "templateId";
    public static final String SMS_TEMPLATE_ID_PREFIX = "message_template_";

    /**
     * 动态负载均衡
     */
    public static final String SMS_LOAD_BALANCE_PREFIX = "sms.loadBalance.msg-type-";

    /**
     * 短信账号code
     */
    public static final Integer TENCENT_SMS_CODE = 10;
    public static final Integer YUN_PIAN_SMS_CODE = 20;

    /**
     * redis前缀
     */
    public static final String STREAM_USER_TRACKING_PREFIX = "dandelion:stream:tracking:user:";
    public static final String STREAM_TEMPLATE_TRACKING_PREFIX = "dandelion:stream:tracking:template:";
    public static final String STREAM_SHORTURL = "dandelion:stream:shorturl:";
    public static final String STREAM_CLICK_TRACKING_PREFIX = "dandelion:stream:tracking:click:";
    public static final String STREAM_SHORTURL_CLICKED = "dandelion:stream:shorturl:clicked:";
    public static final String STREAM_SHORTURL_TIME = "dandelion:stream:shorturl:time:";
}
