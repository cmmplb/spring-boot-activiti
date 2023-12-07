package com.cmmplb.activiti.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author penglibo
 * @date 2023-12-06 13:44:34
 * @since jdk 1.8
 */

@Data
public class ApplyStatisticsVO {

    @ApiModelProperty(value = "时期,对应小时和日期", example = "13:00/9-25")
    private List<String> timeList;

    @ApiModelProperty(value = "名称集合", example = "['请假申请','出差申请']")
    private List<String> nameList;

    @ApiModelProperty(value = "发送数据", example = "1")
    private List<ApplyStatisticsData> dataList;

    @Data
    public static class ApplyStatisticsData {

        @ApiModelProperty(value = "名称", example = "请假申请")
        private String name;

        @ApiModelProperty(value = "时间/日期对应数据", example = "[11,33,22,44]")
        private List<Integer> data;
    }
}
