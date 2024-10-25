package io.github.cmmplb.activiti.service;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.cmmplb.activiti.beans.PageResult;
import io.github.cmmplb.activiti.beans.QueryPageBean;
import io.github.cmmplb.activiti.domain.dto.ModelDTO;
import io.github.cmmplb.activiti.domain.vo.ModelVO;

/**
 * @author penglibo
 * @date 2024-10-22 14:36:22
 * @since jdk 1.8
 */
public interface ModelService {

    boolean save(ModelDTO dto);

    boolean update(ModelDTO dto);

    boolean removeById(String id);

    PageResult<ModelVO> getByPaged(QueryPageBean dto);

    ModelVO getInfoById(String id);

    void export(String id);

    boolean saveDesign(ModelDTO dto);

    ModelVO getEditorJson(String id);

    boolean deployment(String id);
}
