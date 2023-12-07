package com.cmmplb.activiti.service;

import com.cmmplb.activiti.beans.PageResult;
import com.cmmplb.activiti.beans.QueryPageBean;
import com.cmmplb.activiti.dto.ModelDTO;
import com.cmmplb.activiti.vo.ModelVO;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author penglibo
 * @date 2023-11-15 10:23:11
 * @since jdk 1.8
 */
public interface ModelService {

    boolean save(ModelDTO dto);

    boolean saveDesign(String modelId, ModelDTO dto);

    ObjectNode getEditorJson(String modelId);

    boolean deployment(String modelId);

    void export(String modelId);

    boolean removeById(String modelId);

    PageResult<ModelVO> getByPaged(QueryPageBean dto);

    ModelVO getInfoById(String modelId);
}
