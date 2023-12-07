package com.cmmplb.activiti.dto;

import lombok.Data;

@Data
public class ApplyStatisticsTimeDTO {

    /**
     * id
     */
    private Long id;

    /**
     * 类型:1-请假;2-出差;3...
     */
    private Byte type;

    /**
     * 时间
     */
    private String time;

}