package com.cmmplb.activiti.vo;

import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * @author penglibo
 * @date 2023-12-14 10:10:39
 * @since jdk 1.8
 */

@Data
public class BpmnProgressVO {

    /**
     * 高亮流程已发生流转的线id集合-已执行的线
     */
    private List<String> highLightedFlowIds;

    /**
     * 高亮已经执行流程节点ID集合-已执行的节点
     */
    private List<String> highLightedActivitiIds;

    /**
     * 正在执行的节点
     */
    private Set<String> activityIds;

    /**
     * 流程xml
     */
    private String xml;
}
