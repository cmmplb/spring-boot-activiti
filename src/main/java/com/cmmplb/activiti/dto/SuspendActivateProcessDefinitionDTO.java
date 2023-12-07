package com.cmmplb.activiti.dto;

import lombok.Data;

/**
 * @author penglibo
 * @date 2023-11-14 16:36:21
 * @since jdk 1.8
 */

@Data
public class SuspendActivateProcessDefinitionDTO {

    /**
     * 是否挂起/激活关联的实例
     */
    private Boolean activateProcessInstances;

    /**
     * 定时挂起/激活时间
     */
    private String activationDate;
}
