package io.github.cmmplb.activiti.service;

import io.github.cmmplb.activiti.domain.dto.ModelerDTO;
import io.github.cmmplb.activiti.domain.vo.ModelerVO;

/**
 * @author penglibo
 * @date 2024-10-28 17:29:11
 * @since jdk 1.8
 */
public interface ModelerService {

    boolean saveDesign(ModelerDTO dto);

    ModelerVO getModelerJsonById(String id);
}