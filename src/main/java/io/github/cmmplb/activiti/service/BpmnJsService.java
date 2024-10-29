package io.github.cmmplb.activiti.service;

import io.github.cmmplb.activiti.domain.dto.BpmnJsDTO;
import io.github.cmmplb.activiti.domain.vo.BpmnJsVO;

/**
 * @author penglibo
 * @date 2024-10-29 09:22:39
 * @since jdk 1.8
 */
public interface BpmnJsService {

    boolean saveDesign(BpmnJsDTO dto);

    BpmnJsVO getBpmnInfoById(String id);
}
