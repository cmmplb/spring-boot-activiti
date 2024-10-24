package io.github.cmmplb.activiti.service.impl;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.cmmplb.activiti.domain.dto.ModelDTO;
import io.github.cmmplb.activiti.handler.exection.BusinessException;
import org.activiti.editor.constants.EditorJsonConstants;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.constants.StencilConstants;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ModelQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author penglibo
 * @date 2024-10-22 14:41:39
 * @since jdk 1.8
 */

@Service
public class ModelServiceImpl implements ModelService {

    @Autowired
    private RepositoryService repositoryService;

    @Override
    public boolean save(ModelDTO dto) {
        // 校验模型关键字是否重复
        ModelQuery modelQuery = repositoryService.createModelQuery();
        List<Model> list = modelQuery.modelKey(dto.getKey()).list();
        if (!CollectionUtils.isEmpty(list)) {
            throw new BusinessException("模型标识不能重复");
        }
        // 初始化一个空模型, 这个 Model 实体对应 ACT_RE_MODEL 表
        Model model = repositoryService.newModel();
        // 模型名称
        model.setName(dto.getName());
        // 模型关键字
        model.setKey(dto.getKey());
        // 模型类型, 自定义
        model.setCategory(dto.getCategory());
        // 版本号, 默认为 1, 启动流程时如果是 startProcessInstanceByKey 启动的, 则会用最新版本的流程定义执行流程.
        int revision = 1;
        model.setVersion(revision);
        Map<String, Object> metaInfo = new HashMap<>();
        metaInfo.put(ModelDataJsonConstants.MODEL_NAME, dto.getName());
        metaInfo.put(ModelDataJsonConstants.MODEL_REVISION, revision);
        metaInfo.put(ModelDataJsonConstants.MODEL_DESCRIPTION, dto.getDescription());
        // json 格式存储流程定义信息
        model.setMetaInfo(JSON.toJSONString(metaInfo));
        // 保存模型到 act_re_model 表
        repositoryService.saveModel(model);

        // 构建一个空模型 ModelEditorSource, 这个步骤是为了后续设计流程图时使用
        Map<String, Object> content = new HashMap<>();
        // 为什么是 canvas 这个值? 可以去设计一个流程之后, 查看 getModelEditorSource 返回的 json 数据, 等后面写了流程设计之后查看
        content.put(EditorJsonConstants.EDITOR_SHAPE_ID, "canvas");

        // 配置模版属性 ( stencil properties ) , 对应一个空的设计面版上的字段
        HashMap<String, String> properties = new HashMap<>();
        // 名称
        properties.put(StencilConstants.PROPERTY_NAME, dto.getName());
        // 描述
        properties.put(StencilConstants.PROPERTY_DOCUMENTATION, dto.getDescription());
        // 流程唯一标识
        properties.put(StencilConstants.PROPERTY_PROCESS_ID, model.getId());
        // 流程版本
        properties.put(StencilConstants.PROPERTY_PROCESS_VERSION, String.valueOf(revision));
        // 流程作者
        properties.put(StencilConstants.PROPERTY_PROCESS_AUTHOR, dto.getName());
        content.put(EditorJsonConstants.EDITOR_SHAPE_PROPERTIES, properties);

        HashMap<String, String> stencilset = new HashMap<>();
        stencilset.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
        content.put("stencilset", stencilset);
        // 保存模型文件到 act_ge_bytearray 表
        repositoryService.addModelEditorSource(model.getId(), JSON.toJSONBytes(content));

        return true;
    }
}
