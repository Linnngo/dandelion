package net.syl.dandelion.web.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageClickVO {

    /**
     * 点击率
     */
    private double CRT;

    /**
     * 前五分钟点击的人
     */
    private List<String> first5MinClick;

    /**
     * 前十分钟点击的人
     */
    private List<String> first10MinClick;

    /**
     * 前三十分钟点击的人
     */
    private List<String> first30MinClick;
}
