package com.cmmplb.activiti.service.impl;

import com.cmmplb.activiti.beans.PageResult;
import com.cmmplb.activiti.beans.QueryPageBean;
import com.cmmplb.activiti.dto.ModelDTO;
import com.cmmplb.activiti.service.ModelService;
import com.cmmplb.activiti.util.ServletUtil;
import com.cmmplb.activiti.vo.ModelVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ModelQuery;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author penglibo
 * @date 2023-11-15 10:23:19
 * @since jdk 1.8
 */

@Slf4j
@Service
@Transactional
public class ModelServiceImpl implements ModelService {

    @Autowired
    private RepositoryService repositoryService;

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public boolean save(ModelDTO dto) {
        Model model;
        if (StringUtils.isBlank(dto.getId())) {
            model = repositoryService.newModel();
        } else {
            model = repositoryService.getModel(dto.getId());
        }
        model.setCategory(dto.getCategory());
        model.setKey(dto.getKey());
        ObjectNode modelNode = objectMapper.createObjectNode();
        modelNode.put(ModelDataJsonConstants.MODEL_NAME, dto.getName());
        modelNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, dto.getDescription());
        model.setMetaInfo(modelNode.toString());
        model.setName(dto.getName());
        model.setVersion(1);
        ModelQuery modelQuery = repositoryService.createModelQuery();
        List<Model> list = modelQuery.modelKey(dto.getKey()).list();
        if (list.size() > 0) {
            if (list.size() == 1) {
                if (!list.get(0).getId().equals(model.getId())) {
                    throw new RuntimeException("模型标识不能重复");
                }
            } else {
                throw new RuntimeException("模型标识不能重复");
            }
        }
        // 保存模型到act_re_model表
        repositoryService.saveModel(model);
        if (StringUtils.isBlank(dto.getId())) {
            Map<String, Object> content = new HashMap<>();
            content.put("resourceId", model.getId());
            HashMap<String, String> properties = new HashMap<>();
            properties.put("process_id", dto.getKey());
            properties.put("name", dto.getName());
            properties.put("category", dto.getCategory());
            content.put("properties", properties);
            HashMap<String, String> stencilset = new HashMap<>();
            stencilset.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
            content.put("stencilset", stencilset);
            // 保存模型文件到act_ge_bytearray表
            try {
                repositoryService.addModelEditorSource(model.getId(), objectMapper.writeValueAsBytes(content));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }

    @Override
    public boolean saveDesign(String modelId, ModelDTO dto) {
        try {
            Model model = repositoryService.getModel(modelId);

            ObjectNode modelJson = (ObjectNode) objectMapper.readTree(model.getMetaInfo());

            modelJson.put(ModelDataJsonConstants.MODEL_NAME, dto.getName());
            modelJson.put(ModelDataJsonConstants.MODEL_DESCRIPTION, dto.getDescription());
            model.setMetaInfo(modelJson.toString());
            model.setName(dto.getName());

            repositoryService.saveModel(model);

            repositoryService.addModelEditorSource(model.getId(), dto.getJson_xml().getBytes(StandardCharsets.UTF_8));

            InputStream svgStream = new ByteArrayInputStream(dto.getSvg_xml().getBytes(StandardCharsets.UTF_8));
            TranscoderInput input = new TranscoderInput(svgStream);

            PNGTranscoder transcoder = new PNGTranscoder();
            // Setup output
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            TranscoderOutput output = new TranscoderOutput(outStream);

            // Do the transformation
            transcoder.transcode(input, output);
            final byte[] result = outStream.toByteArray();
            repositoryService.addModelEditorSourceExtra(model.getId(), result);
            outStream.close();

        } catch (Exception e) {
            log.error("Error saving model", e);
            throw new ActivitiException("Error saving model", e);
        }
        return true;
    }

    @Override
    public ObjectNode getEditorJson(String modelId) {
        ObjectNode modelNode = null;
        Model model = repositoryService.getModel(modelId);
        if (model != null) {
            try {
                if (StringUtils.isNotEmpty(model.getMetaInfo())) {
                    modelNode = (ObjectNode) objectMapper.readTree(model.getMetaInfo());
                } else {
                    modelNode = objectMapper.createObjectNode();
                    modelNode.put(ModelDataJsonConstants.MODEL_NAME, model.getName());
                }
                modelNode.put(ModelDataJsonConstants.MODEL_ID, model.getId());
                ObjectNode editorJsonNode = (ObjectNode) objectMapper.readTree(
                        new String(repositoryService.getModelEditorSource(model.getId()), StandardCharsets.UTF_8));
                modelNode.put("model", editorJsonNode);

            } catch (Exception e) {
                log.error("Error creating model JSON", e);
                throw new ActivitiException("Error creating model JSON", e);
            }
        }
        return modelNode;
    }

    @Override
    public boolean deployment(String modelId) {
        try {
            Model model = repositoryService.getModel(modelId);
            byte[] modelData = repositoryService.getModelEditorSource(modelId);
            JsonNode jsonNode = objectMapper.readTree(modelData);
            BpmnModel bpmnModel = (new BpmnJsonConverter()).convertToBpmnModel(jsonNode);
            Deployment deploy = repositoryService.createDeployment()
                    .name(model.getName())
                    .key(model.getKey())
                    .category(model.getCategory())
                    .addBpmnModel(model.getKey() + ".bpmn20.xml", bpmnModel)
                    .deploy();
            model.setDeploymentId(deploy.getId());
            repositoryService.saveModel(model);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("流程图不合规范，请重新设计");
        }
        return true;
    }

    @Override
    public void export(String modelId) {
        byte[] modelData = repositoryService.getModelEditorSource(modelId);
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(modelData);
            BpmnModel bpmnModel = (new BpmnJsonConverter()).convertToBpmnModel(jsonNode);
            byte[] xmlBytes = (new BpmnXMLConverter()).convertToXML(bpmnModel, "UTF-8");
            ByteArrayInputStream in = new ByteArrayInputStream(xmlBytes);
            HttpServletResponse response = ServletUtil.getResponse();
            IOUtils.copy(in, response.getOutputStream());
            String filename = bpmnModel.getMainProcess().getId() + ".bpmn20.xml";
            response.setHeader("Content-Disposition", "attachment;filename=" + filename);
            response.setHeader("content-Type", "application/xml");
            response.flushBuffer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean removeById(String modelId) {
        repositoryService.deleteModel(modelId);
        return true;
    }

    @Override
    public PageResult<ModelVO> getByPaged(QueryPageBean dto) {
        ModelQuery query = repositoryService.createModelQuery();
        if (StringUtils.isNotEmpty(dto.getKeywords())) {
            query.modelNameLike(dto.getKeywords());
        }
        List<ModelVO> res = new ArrayList<>();
        long total = query.count();
        if (total > 0) {
            List<Model> list = query.orderByCreateTime().desc().listPage(dto.getStart(), dto.getSize()); // 分页
            ModelVO vo;
            for (Model model : list) {
                vo = new ModelVO();
                vo.setId(model.getId());
                vo.setName(model.getName());
                vo.setKey(model.getKey());
                vo.setCategory(model.getCategory());
                vo.setCreateTime(model.getCreateTime());
                vo.setLastUpdateTime(model.getLastUpdateTime());
                vo.setVersion(model.getVersion());
                vo.setMetaInfo(model.getMetaInfo());
                vo.setDeploymentId(model.getDeploymentId());
                res.add(vo);
            }
        }
        return new PageResult<>(total, res);
    }

    @Override
    public ModelVO getInfoById(String modelId) {
        ModelQuery query = repositoryService.createModelQuery();
        query.modelId(modelId);
        Model model = query.singleResult();
        ModelVO vo = new ModelVO();
        vo.setId(model.getId());
        vo.setName(model.getName());
        vo.setKey(model.getKey());
        vo.setCategory(model.getCategory());
        vo.setCreateTime(model.getCreateTime());
        vo.setLastUpdateTime(model.getLastUpdateTime());
        vo.setVersion(model.getVersion());
        vo.setMetaInfo(model.getMetaInfo());
        vo.setDeploymentId(model.getDeploymentId());
        return vo;
    }
}
