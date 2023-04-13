package net.syl.dandelion.web.vo.amis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 *
 * 图表的Vo
 * https://aisuda.bce.baidu.com/amis/zh-CN/components/chart
 * https://www.runoob.com/echarts/echarts-setup.html
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EchartsVo {
    /**
     * xAxis x轴
     */
    private List<String> xAxisData;
    /**
     * series 系列列表
     * <p>
     * 每个系列通过 type 决定自己的图表类型
     */
    private List<Integer> seriesData;
    /**
     * title 标题
     */
    private String titleText;
    /**
     * legend 图例
     */
    private String LegendData;
}
