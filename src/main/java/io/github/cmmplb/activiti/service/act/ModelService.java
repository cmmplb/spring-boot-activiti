package io.github.cmmplb.activiti.service.act;

import com.alibaba.fastjson.JSONObject;
import io.github.cmmplb.activiti.beans.PageResult;
import io.github.cmmplb.activiti.beans.QueryPageBean;
import io.github.cmmplb.activiti.domain.dto.act.ModelDTO;
import io.github.cmmplb.activiti.domain.vo.act.ModelVO;
import org.springframework.web.multipart.MultipartFile;

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

    boolean importModel(Integer designType, MultipartFile[] files);

    void export(String id);

    boolean deployment(String id);

    void saveJsonXml(JSONObject bpmnXml, String id, String key, String name, String description, String author, int revision);

    void saveBpmnJsXml(String xmlString, String id, String key, String name, String description, String author, String category, int revision);

    void saveSvg(String id, String svgXml);
}
