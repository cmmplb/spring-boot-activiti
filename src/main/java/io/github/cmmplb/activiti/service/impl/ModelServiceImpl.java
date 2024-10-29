package io.github.cmmplb.activiti.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cmmplb.activiti.beans.PageResult;
import io.github.cmmplb.activiti.beans.QueryPageBean;
import io.github.cmmplb.activiti.convert.ModelConvert;
import io.github.cmmplb.activiti.domain.dto.ModelDTO;
import io.github.cmmplb.activiti.domain.vo.ModelVO;
import io.github.cmmplb.activiti.handler.exection.BusinessException;
import io.github.cmmplb.activiti.service.ModelService;
import io.github.cmmplb.activiti.utils.ConverterUtil;
import io.github.cmmplb.activiti.utils.ServletUtil;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.EditorJsonConstants;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.constants.StencilConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ModelQuery;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author penglibo
 * @date 2024-10-22 14:41:39
 * @since jdk 1.8
 */

@Service
public class ModelServiceImpl implements ModelService {

    @Autowired
    private ObjectMapper objectMapper;

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

        // 构建一个空模型文件 ModelEditorSource, 这个步骤是为了后续设计流程图时使用
        Map<String, Object> bpmnXml = new HashMap<>();
        // 为什么是 canvas 这个值? 可以去设计一个流程之后, 查看 getModelEditorSource 返回的 json 数据, 等后面写了流程设计之后查看
        bpmnXml.put(EditorJsonConstants.EDITOR_SHAPE_ID, "canvas");

        // 配置模版属性 ( stencil properties ) , 对应设计面版上的字段
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
        properties.put(StencilConstants.PROPERTY_PROCESS_AUTHOR, dto.getAuthor());
        bpmnXml.put(EditorJsonConstants.EDITOR_SHAPE_PROPERTIES, properties);

        HashMap<String, String> stencilset = new HashMap<>();
        stencilset.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
        bpmnXml.put("stencilset", stencilset);
        // 保存模型文件到 act_ge_bytearray 表
        repositoryService.addModelEditorSource(model.getId(), JSON.toJSONBytes(bpmnXml));

        return true;
    }

    @Override
    public boolean update(ModelDTO dto) {
        Model model = repositoryService.getModel(dto.getId());
        if (null == model) {
            throw new RuntimeException("模型信息不存在");
        }
        // 校验模型关键字是否重复
        ModelQuery modelQuery = repositoryService.createModelQuery();
        List<Model> list = modelQuery.modelKey(dto.getKey()).list();
        if (!CollectionUtils.isEmpty(list) && !list.get(0).getId().equals(dto.getId())) {
            throw new RuntimeException("模型标识不能重复");
        }
        // 模型名称
        model.setName(dto.getName());
        // 模型关键字
        model.setKey(dto.getKey());
        // 模型类型
        model.setCategory(dto.getCategory());
        // 版本号, 这里直接在代码里加 1 了, 应该在数据库利用行锁 version = version + 1 的方式来修改, 或者加锁, 防止数据重复更新
        int revision = model.getVersion() + 1;
        model.setVersion(revision);
        Map<String, Object> metaInfo = new HashMap<>();
        metaInfo.put(ModelDataJsonConstants.MODEL_NAME, dto.getName());
        metaInfo.put(ModelDataJsonConstants.MODEL_REVISION, revision);
        metaInfo.put(ModelDataJsonConstants.MODEL_DESCRIPTION, dto.getDescription());
        // json 格式存储流程定义信息
        model.setMetaInfo(JSON.toJSONString(metaInfo));
        // 修改模型到 act_re_model 表（repositoryService 中修改和保存的方法是同一个）
        repositoryService.saveModel(model);

        // 更新流程设计文件信息

        // 获取流程定义文件
        byte[] modelData = repositoryService.getModelEditorSource(dto.getId());
        if (!Arrays.isNullOrEmpty(modelData)) {
            JSONObject bpmnXml = JSON.parseObject(new String(modelData, StandardCharsets.UTF_8));
            // 更新流程设计文件
            // 修改流程文件中的版本号 ( 这里看需求, 是以模型的版本号还是取流程设计的版本号 )
            JSONObject properties = bpmnXml.getJSONObject(EditorJsonConstants.EDITOR_SHAPE_PROPERTIES);
            // 名称
            properties.put(StencilConstants.PROPERTY_NAME, dto.getName());
            // 描述
            properties.put(StencilConstants.PROPERTY_DOCUMENTATION, dto.getDescription());
            // 我这里是把模型的版本号作为流程设计版本号, 所以修改流程设计中的流程版本会不生效
            properties.put(StencilConstants.PROPERTY_PROCESS_VERSION, String.valueOf(revision));
            // 作者
            properties.put(StencilConstants.PROPERTY_PROCESS_AUTHOR, dto.getAuthor());
            bpmnXml.put(EditorJsonConstants.EDITOR_SHAPE_PROPERTIES, properties);
            repositoryService.addModelEditorSource(model.getId(), bpmnXml.toJSONString().getBytes(StandardCharsets.UTF_8));
        }
        return true;
    }

    @Override
    public boolean removeById(String id) {
        Model model = repositoryService.getModel(id);
        if (null == model) {
            throw new RuntimeException("模型信息不存在");
        }
        // 删除模型会同时删除关联的流程定义文件, 也就是 act_ge_bytearray 表中一条关联的数据
        repositoryService.deleteModel(id);
        return true;
    }

    @Override
    public PageResult<ModelVO> getByPaged(QueryPageBean dto) {
        ModelQuery query = repositoryService.createModelQuery();
        if (StringUtils.isNotEmpty(dto.getKeywords())) {
            // 根据模型名称模糊查询
            query.modelNameLike(dto.getKeywords());
        }
        List<ModelVO> res = new ArrayList<>();
        // count 查询总数
        long total = query.count();
        if (total > 0) {
            // 分页查询
            List<Model> list = query.orderByCreateTime().desc().listPage(dto.getStart(), dto.getSize());
            // .stream().map() jdk 8 语法
            return new PageResult<>(total, list.stream().map(model -> ConverterUtil.convert(ModelConvert.class, model)
            ).collect(Collectors.toList()));
        }
        return new PageResult<>(total, res);
    }

    @Override
    public ModelVO getInfoById(String id) {
        Model model = repositoryService.getModel(id);
        if (null == model) {
            throw new RuntimeException("模型信息不存在");
        }
        ModelVO vo = ConverterUtil.convert(ModelConvert.class, model);
        // 获取流程定义文件
        byte[] modelData = repositoryService.getModelEditorSource(id);
        if (!Arrays.isNullOrEmpty(modelData)) {
            JSONObject bpmnXml = JSON.parseObject(new String(modelData, StandardCharsets.UTF_8));
            JSONObject properties = bpmnXml.getJSONObject(EditorJsonConstants.EDITOR_SHAPE_PROPERTIES);
            // 设置作者信息
            vo.setAuthor(properties.getString(StencilConstants.PROPERTY_PROCESS_AUTHOR));
        }
        return vo;
    }

    @Override
    public void export(String id) {
        // 获取流程定义文件
        byte[] modelData = repositoryService.getModelEditorSource(id);
        if (Arrays.isNullOrEmpty(modelData)) {
            throw new RuntimeException("流程模型文件不存在");
        }
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(modelData);
            // 使用 activiti-json-converter 依赖中的转换器将 json 转换成 BpmnModel
            BpmnModel bpmnModel = (new BpmnJsonConverter()).convertToBpmnModel(jsonNode);
            // 之后将 BpmnModel 转换成 xml
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
    public boolean deployment(String id) {
        try {
            Model model = repositoryService.getModel(id);
            if (null == model) {
                throw new BusinessException("模型信息不存在");
            }
            // 获取流程定义文件
            byte[] modelData = repositoryService.getModelEditorSource(id);
            JsonNode jsonNode = objectMapper.readTree(modelData);
            BpmnModel bpmnModel = (new BpmnJsonConverter()).convertToBpmnModel(jsonNode);
            // 基于 BpmnModel 部署模型
            Deployment deploy = repositoryService.createDeployment()
                    // 部署名称
                    .name(model.getName())
                    // 部署类型
                    .category(model.getCategory())
                    // 关键字
                    .key(model.getKey())
                    // 前面基础是基于 .addClasspathResource("processes/test.bpmn20.xml") 来部署的
                    .addBpmnModel(model.getKey() + ".bpmn20.xml", bpmnModel)
                    // 看源码发现, 如果存在多个部署名称相同的部署信息, 则会取第一个更新版本号
                    .deploy();
            model.setDeploymentId(deploy.getId());
            // 更新模型部署 id
            repositoryService.saveModel(model);

        } catch (Exception e) {
            throw new RuntimeException("流程图不合规范，请重新设计");
        }
        return true;
    }
}
