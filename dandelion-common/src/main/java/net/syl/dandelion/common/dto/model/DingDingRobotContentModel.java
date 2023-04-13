package net.syl.dandelion.common.dto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DingDingRobotContentModel extends ContentModel {

    // 消息类型，text纯文本，markdown，ActionCard等
    private String msgKey;

    // 纯文本内容
    private SampleText sampleText;

    // MarkDown内容
    private SampleMarkdown sampleMarkdown;

    // Link内容
    private SampleLink sampleLink;

    // ActionCard内容
    private SampleActionCard actionCard;

    private SampleActionCard2 actionCard2;

    private SampleActionCard3 actionCard3;

    private SampleActionCard4 actionCard4;

    private SampleActionCard5 actionCard5;

    private SampleActionCard6 actionCard6;

    @NoArgsConstructor
    @Data
    @Builder
    @AllArgsConstructor
    public static class SampleText {
        private String content;
    }

    @NoArgsConstructor
    @Data
    @AllArgsConstructor
    @Builder
    public static class SampleLink {
        private String text;
        private String title;
        private String picUrl;
        private String messageUrl;
    }

    @NoArgsConstructor
    @Data
    @AllArgsConstructor
    @Builder
    public static class SampleMarkdown {
        private String title;
        private String text;
    }

    @NoArgsConstructor
    @Data
    @AllArgsConstructor
    @Builder
    public static class SampleImageMsg {
        private String photoURL;
    }

    @NoArgsConstructor
    @Data
    @AllArgsConstructor
    @Builder
    public static class SampleActionCard {
        private String title;
        private String text;
        private String singleTitle;
        private String singleURL;
    }

    @NoArgsConstructor
    @Data
    @AllArgsConstructor
    @Builder
    public static class SampleActionCard2 {
        private String title;
        private String text;
        private String singleTitle1;
        private String singleURL1;
        private String singleTitle2;
        private String singleURL2;
    }

    @NoArgsConstructor
    @Data
    @AllArgsConstructor
    @Builder
    public static class SampleActionCard3 {
        private String title;
        private String text;
        private String singleTitle1;
        private String singleURL1;
        private String singleTitle2;
        private String singleURL2;
        private String singleTitle3;
        private String singleURL3;
    }

    @NoArgsConstructor
    @Data
    @AllArgsConstructor
    @Builder
    public static class SampleActionCard4 {
        private String title;
        private String text;
        private String singleTitle1;
        private String singleURL1;
        private String singleTitle2;
        private String singleURL2;
        private String singleTitle3;
        private String singleURL3;
        private String singleTitle4;
        private String singleURL4;
    }

    @NoArgsConstructor
    @Data
    @AllArgsConstructor
    @Builder
    public static class SampleActionCard5 {
        private String title;
        private String text;
        private String singleTitle1;
        private String singleURL1;
        private String singleTitle2;
        private String singleURL2;
        private String singleTitle3;
        private String singleURL3;
        private String singleTitle4;
        private String singleURL4;
        private String singleTitle5;
        private String singleURL5;
    }

    @NoArgsConstructor
    @Data
    @AllArgsConstructor
    @Builder
    public static class SampleActionCard6 {
        private String title;
        private String text;
        private String buttonTitle1;
        private String buttonUrl1;
        private String buttonTitle2;
        private String buttonUrl2;
    }
}
