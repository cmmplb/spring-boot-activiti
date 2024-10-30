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
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ModelQuery;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
        // 添加一个模型设计类型字段
        metaInfo.put(ModelDTO.DESIGN_TYPE, dto.getDesignType());
        // json 格式存储流程定义信息
        model.setMetaInfo(JSON.toJSONString(metaInfo));
        // 保存模型到 act_re_model 表
        repositoryService.saveModel(model);

        // 构建一个空模型文件 ModelEditorSource, 这个步骤是为了后续设计流程图时使用, 如果没有的话, activiti modeler 页面空白操作不了
        // 设计类型:1-activiti modeler;2-bpmn-js; bpmn-js 没有 properties 属性
        if (dto.getDesignType().equals(1)) {
            JSONObject bpmnXml = new JSONObject();
            // 为什么是 canvas 这个值? 可以去设计一个流程之后, 查看 getModelEditorSource 返回的 json 数据, 等后面写了流程设计之后查看
            bpmnXml.put(EditorJsonConstants.EDITOR_SHAPE_ID, "canvas");

            // 创建 activiti modeler 空节点
            HashMap<String, String> stencilset = new HashMap<>();
            stencilset.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
            bpmnXml.put("stencilset", stencilset);

            // 更新流程设计文件
            saveJsonXml(bpmnXml, model.getId(), dto.getName(), dto.getDescription(), dto.getAuthor(), revision);
        } else {
            // 初始化一个开始节点的 bpmn-js 模型, 注意, 这里的 xml 我在设计界面中-> 常规-可执行文件勾选好了, 如果不勾选部署的时候会报错:
            // All process definition are set to be non-executable (property 'isExecutable' on process). This is not allowed. - [Extra info : ]
            String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<bpmn:definitions xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:bpmn=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\" id=\"Definitions_1\" targetNamespace=\"http://bpmn.io/schema/bpmn\"><bpmn:process id=\"Process_1\" isExecutable=\"true\"><bpmn:startEvent id=\"StartEvent_1\" /></bpmn:process><bpmndi:BPMNDiagram id=\"BPMNDiagram_1\"><bpmndi:BPMNPlane id=\"BPMNPlane_1\" bpmnElement=\"Process_1\"><bpmndi:BPMNShape id=\"_BPMNShape_StartEvent_2\" bpmnElement=\"StartEvent_1\"><dc:Bounds x=\"173\" y=\"102\" width=\"36\" height=\"36\" /></bpmndi:BPMNShape></bpmndi:BPMNPlane></bpmndi:BPMNDiagram></bpmn:definitions>";
            String svg = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<!-- created with bpmn-js / http://bpmn.io -->\n<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" width=\"46\" height=\"46\" viewBox=\"168 97 46 46\" version=\"1.1\"><g class=\"djs-group\"><g class=\"djs-element djs-shape\" data-element-id=\"StartEvent_1\" style=\"display: block;\" transform=\"matrix(1 0 0 1 173 102)\"><g class=\"djs-visual\"><circle cx=\"18\" cy=\"18\" r=\"18\" style=\"stroke-linecap: round; stroke-linejoin: round; stroke: rgb(34, 36, 42); stroke-width: 2px; fill: white; fill-opacity: 0.95;\"/></g><rect class=\"djs-hit djs-hit-all\" x=\"0\" y=\"0\" width=\"36\" height=\"36\" style=\"fill: none; stroke-opacity: 0; stroke: white; stroke-width: 15px;\"/><circle cx=\"18\" cy=\"18\" r=\"23\" class=\"djs-outline\" style=\"fill: none;\"/></g></g></svg>";
            // 添加流程设计文件, 注意, activiti-modeler 保存的是 json, bpmn-js 保存的是 xml
            repositoryService.addModelEditorSource(model.getId(), xml.getBytes(StandardCharsets.UTF_8));

            // 添加流程图片信息
            saveSvg(model.getId(), svg);
        }
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
        JSONObject metaInfo = JSON.parseObject(model.getMetaInfo());
        metaInfo.put(ModelDataJsonConstants.MODEL_NAME, dto.getName());
        metaInfo.put(ModelDataJsonConstants.MODEL_REVISION, revision);
        metaInfo.put(ModelDataJsonConstants.MODEL_DESCRIPTION, dto.getDescription());
        // json 格式存储流程定义信息
        model.setMetaInfo(JSON.toJSONString(metaInfo));
        // 修改模型到 act_re_model 表（repositoryService 中修改和保存的方法是同一个）
        repositoryService.saveModel(model);

        byte[] modelData = repositoryService.getModelEditorSource(dto.getId());
        // 如果设计类型是 activiti modeler, 更新流程设计文件信息
        if (!Arrays.isNullOrEmpty(modelData) && dto.getDesignType().equals(1)) {
            saveJsonXml(JSON.parseObject(new String(modelData, StandardCharsets.UTF_8)),
                    dto.getId(), dto.getName(), dto.getDescription(), dto.getAuthor(), revision);
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
            String metaInfo = model.getMetaInfo();
            JSONObject metaInfoJson = JSON.parseObject(metaInfo);
            if (metaInfoJson.getInteger(ModelDTO.DESIGN_TYPE).equals(1)) {
                JSONObject bpmnXml = JSON.parseObject(new String(modelData, StandardCharsets.UTF_8));
                JSONObject properties = bpmnXml.getJSONObject(EditorJsonConstants.EDITOR_SHAPE_PROPERTIES);
                // 设置作者信息, 用于回显 activiti modeler 编辑模型
                vo.setAuthor(properties.getString(StencilConstants.PROPERTY_PROCESS_AUTHOR));
            }
        }
        return vo;
    }

    @Override
    public void export(String id) {
        Model model = repositoryService.getModel(id);
        if (null == model) {
            throw new BusinessException("模型信息不存在");
        }

        // 获取流程定义文件
        byte[] modelData = repositoryService.getModelEditorSource(id);
        if (Arrays.isNullOrEmpty(modelData)) {
            throw new RuntimeException("流程模型文件不存在");
        }
        JSONObject metaInfo = JSON.parseObject(model.getMetaInfo());
        byte[] xmlBytes;
        // activiti modeler 存储的是 jsonXml, bpmn-js 存储的是 xml
        if (metaInfo.getInteger(ModelDTO.DESIGN_TYPE).equals(1)) {
            JsonNode jsonNode = null;
            try {
                jsonNode = objectMapper.readTree(modelData);
            } catch (IOException e) {
                throw new RuntimeException("解析流程模型文件失败");
            }
            // 使用 activiti-json-converter 依赖中的转换器将 json 转换成 BpmnModel
            BpmnModel bpmnModel = (new BpmnJsonConverter()).convertToBpmnModel(jsonNode);
            // 之后将 BpmnModel 转换成 xml
            xmlBytes = (new BpmnXMLConverter()).convertToXML(bpmnModel, StandardCharsets.UTF_8.name());
        } else {
            xmlBytes = modelData;
        }
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(xmlBytes);
            HttpServletResponse response = ServletUtil.getResponse();
            IOUtils.copy(in, response.getOutputStream());
            String filename = model.getKey() + ".bpmn20.xml";
            response.setHeader("Content-Disposition", "attachment;filename=" + filename);
            response.setHeader("content-Type", "application/xml");
            response.flushBuffer();
        } catch (IOException e) {
            throw new RuntimeException("导出流程模型文件失败");
        }
    }

    @Override
    public boolean deployment(String id) {
        Model model = repositoryService.getModel(id);
        if (null == model) {
            throw new BusinessException("模型信息不存在");
        }
        // 构建部署对象
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment()
                .name(model.getName()).category(model.getCategory()).key(model.getKey());

        // 获取流程定义文件
        byte[] modelData = repositoryService.getModelEditorSource(id);
        JSONObject metaInfo = JSON.parseObject(model.getMetaInfo());
        if (metaInfo.getInteger(ModelDTO.DESIGN_TYPE).equals(1)) {
            JsonNode jsonNode = null;
            try {
                jsonNode = objectMapper.readTree(modelData);
            } catch (IOException e) {
                throw new RuntimeException("解析流程模型文件失败");
            }
            // 基于 BpmnModel 部署模型
            BpmnModel bpmnModel = (new BpmnJsonConverter()).convertToBpmnModel(jsonNode);
            // 前面基础是基于 .addClasspathResource("processes/test.bpmn20.xml") 来部署的
            deploymentBuilder.addBpmnModel(model.getKey() + ".bpmn20.xml", bpmnModel);
        } else {
            // 基于 xml 部署模型
            deploymentBuilder.addBytes(model.getKey() + ".bpmn20.xml", modelData);
        }
        // 看源码发现, 如果存在多个部署名称相同的部署信息, 则会取第一个更新版本号
        Deployment deploy = null;
        try {
            deploy = deploymentBuilder.deploy();
        } catch (Exception e) {
            throw new RuntimeException("流程图不合规范，请重新设计");
        }
        model.setDeploymentId(deploy.getId());
        // 更新模型信息部署 id 字段
        repositoryService.saveModel(model);
        return true;
    }

    @Override
    public void saveJsonXml(JSONObject bpmnXml, String id, String name, String description, String author, int revision) {
        // 修改流程文件中的版本号 ( 这里看需求, 是以模型的版本号还是取流程设计的版本号 )
        JSONObject properties = bpmnXml.getJSONObject(EditorJsonConstants.EDITOR_SHAPE_PROPERTIES);
        if (null == properties) {
            // 配置模版属性 ( stencil properties ) , 对应设计面版上的字段
            properties = new JSONObject();
        }
        // 名称
        properties.put(StencilConstants.PROPERTY_NAME, name);
        // 描述
        properties.put(StencilConstants.PROPERTY_DOCUMENTATION, description);
        if (StringUtils.isNotEmpty(author)) {
            // 新增和编辑模型页面修改作者
            properties.put(StencilConstants.PROPERTY_PROCESS_AUTHOR, author);
        }
        // 流程唯一标识
        properties.put(StencilConstants.PROPERTY_PROCESS_ID, id);
        // 我这里是把模型的版本号作为流程设计版本号, 所以修改流程设计中的流程版本会不生效
        properties.put(StencilConstants.PROPERTY_PROCESS_VERSION, String.valueOf(revision));
        bpmnXml.put(EditorJsonConstants.EDITOR_SHAPE_PROPERTIES, properties);
        // 保存模型文件到 act_ge_bytearray 表
        repositoryService.addModelEditorSource(id, bpmnXml.toJSONString().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void saveSvg(String id, String svgXml) {
        // 将 svg 图片转换为 png 保存
        InputStream svgStream = new ByteArrayInputStream(svgXml.getBytes(StandardCharsets.UTF_8));
        TranscoderInput input = new TranscoderInput(svgStream);
        // png 图片生成器
        PNGTranscoder transcoder = new PNGTranscoder();
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        TranscoderOutput output = new TranscoderOutput(outStream);

        try {
            transcoder.transcode(input, output);
            final byte[] result = outStream.toByteArray();
            // 更新流程设计图片
            repositoryService.addModelEditorSourceExtra(id, result);
            outStream.close();
        } catch (TranscoderException | IOException e) {
            throw new BusinessException("更新流程设计图片异常");
        }
    }
}
