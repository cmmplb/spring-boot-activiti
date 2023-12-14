package com.cmmplb.activiti.service;

import com.cmmplb.activiti.dto.ModelBpmnDTO;

import java.util.Map;

/**
 * @author penglibo
 * @date 2023-12-13 16:15:32
 * @since jdk 1.8
 */
public interface BpmnJsService {

    Map<String,Object> getBpmnInfo(String modelId);

    void export(String modelId);

    boolean saveDesign(String modelId, ModelBpmnDTO dto);
}
