package io.github.cmmplb.activiti.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cmmplb.activiti.beans.PageResult;
import io.github.cmmplb.activiti.beans.QueryPageBean;
import io.github.cmmplb.activiti.configuration.properties.ActivitiProperties;
import io.github.cmmplb.activiti.convert.ModelConvert;
import io.github.cmmplb.activiti.domain.dto.ModelDTO;
import io.github.cmmplb.activiti.domain.vo.ModelVO;
import io.github.cmmplb.activiti.handler.exection.BusinessException;
import io.github.cmmplb.activiti.service.DeploymentService;
import io.github.cmmplb.activiti.service.ModelService;
import io.github.cmmplb.activiti.utils.ConverterUtil;
import io.github.cmmplb.activiti.utils.FileUtil;
import io.github.cmmplb.activiti.utils.ServletUtil;
import io.github.cmmplb.activiti.utils.XmlUtil;
import lombok.extern.slf4j.Slf4j;
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
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.exceptions.PersistenceException;
import org.bouncycastle.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;

/**
 * @author penglibo
 * @date 2024-10-22 14:41:39
 * @since jdk 1.8
 */

@Slf4j
@Service
public class ModelServiceImpl implements ModelService {

    @Autowired
    private ObjectMapper objectMapper;

    // 管理和控制流程定义的服务接口，包括部署、查询和删除流程定义等。
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private DeploymentService deploymentService;

    @Autowired
    private ActivitiProperties activitiProperties;

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
            throw new BusinessException("模型信息不存在");
        }
        // 校验模型关键字是否重复
        ModelQuery modelQuery = repositoryService.createModelQuery();
        List<Model> list = modelQuery.modelKey(dto.getKey()).list();
        if (!CollectionUtils.isEmpty(list) && !list.get(0).getId().equals(dto.getId())) {
            throw new BusinessException("模型标识不能重复");
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
            throw new BusinessException("模型信息不存在");
        }
        // 删除模型会同时删除关联的流程定义文件, 也就是 act_ge_bytearray 表中一条关联的数据
        repositoryService.deleteModel(id);
        return true;
    }

    @Override
    public PageResult<ModelVO> getByPaged(QueryPageBean queryPageBean) {
        ModelQuery query = repositoryService.createModelQuery();
        if (StringUtils.isNotEmpty(queryPageBean.getKeywords())) {
            // 根据模型名称模糊查询
            query.modelNameLike(queryPageBean.getKeywords());
        }
        List<ModelVO> res = new ArrayList<>();
        // count 查询总数
        long total = query.count();
        if (total > 0) {
            // 根据创建时间倒序, 分页查询
            List<Model> list = query.orderByCreateTime().desc().listPage(queryPageBean.getStart(), queryPageBean.getSize());
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
            throw new BusinessException("模型信息不存在");
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
            throw new BusinessException("流程模型文件不存在");
        }
        JSONObject metaInfo = JSON.parseObject(model.getMetaInfo());
        byte[] xmlBytes;
        // activiti modeler 存储的是 jsonXml, bpmn-js 存储的是 xml
        if (metaInfo.getInteger(ModelDTO.DESIGN_TYPE).equals(1)) {
            JsonNode jsonNode;
            try {
                jsonNode = objectMapper.readTree(modelData);
            } catch (IOException e) {
                throw new BusinessException("解析流程模型文件失败");
            }
            // 使用 activiti-json-converter 依赖中的转换器将 json 转换成 BpmnModel
            BpmnModel bpmnModel = (new BpmnJsonConverter()).convertToBpmnModel(jsonNode);
            // 之后将 BpmnModel 转换成 xml
            xmlBytes = (new BpmnXMLConverter()).convertToXML(bpmnModel, StandardCharsets.UTF_8.name());
        } else {
            try {
                xmlBytes = XmlUtil.formatXml(new String(modelData, StandardCharsets.UTF_8)).getBytes(StandardCharsets.UTF_8);
            } catch (Exception e) {
                log.error("格式化 xml 失败:{}", e.getMessage());
                // 格式化失败的话返回原始数据
                xmlBytes = modelData;
            }
        }
        HttpServletResponse response = ServletUtil.getResponse();
        try {
            // 导出模型时是否导出流程图片, 为 true 时流程文件和流程图片压缩成 zip 导出
            if (activitiProperties.getModel().isExportEditorSourceExtra()) {
                // 获取流程图片, activiti modeler 和 bpmn-js 图片都是将 svg 转为 png 存储
                byte[] pngData = repositoryService.getModelEditorSourceExtra(id);
                // 用于部署上传流程文件测试 ACT_RE_PROCDEF 表 DGRM_RESOURCE_NAME_ 字段
                String filename = model.getName() + ".zip";
                response.setContentType("application/octet-stream");
                response.setHeader("Content-Disposition", "attachment;filename=" + new String(URLEncoder.encode(filename, StandardCharsets.UTF_8.name()).getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));
                ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream(), StandardCharsets.UTF_8);
                // 压缩流程文件, 通过流程文件需要满足这个后缀条件: "bpmn20.xml", "bpmn"
                FileUtil.toZip(zipOutputStream, model.getName() + ".bpmn20.xml", new ByteArrayInputStream(xmlBytes));
                // 压缩流程 svg 图片, 图片需要满足: "png", "jpg", "gif", "svg"
                FileUtil.toZip(zipOutputStream, model.getName() + ".png", new ByteArrayInputStream(pngData));
            } else {
                ByteArrayInputStream in = new ByteArrayInputStream(xmlBytes);
                String filename = model.getName() + ".bpmn20.xml";
                response.setContentType("application/xml");
                response.setHeader("Content-Disposition", "attachment;filename=" + new String(URLEncoder.encode(filename, StandardCharsets.UTF_8.name()).getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));
                IOUtils.copy(in, response.getOutputStream());
                // 可以实时将部分结果发送给客户端, 而不是等待整个操作完成后再发送‌
                response.flushBuffer();
            }
        } catch (IOException e) {
            throw new BusinessException("导出流程模型文件失败");
        }
    }

    @Override
    public boolean deployment(String id) {
        Model model = repositoryService.getModel(id);
        if (null == model) {
            throw new BusinessException("模型信息不存在");
        }
        // 构建部署对象
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();

        // 获取流程定义文件
        byte[] modelData = repositoryService.getModelEditorSource(id);
        JSONObject metaInfo = JSON.parseObject(model.getMetaInfo());
        String resourceName = model.getKey() + ".bpmn20.xml";
        // 设计类型:1-activiti modeler;2-bpmn-js;
        if (metaInfo.getInteger(ModelDTO.DESIGN_TYPE).equals(1)) {
            JsonNode jsonNode;
            try {
                jsonNode = objectMapper.readTree(modelData);
            } catch (IOException e) {
                throw new BusinessException("解析流程模型文件失败");
            }
            // 基于 BpmnModel 部署模型
            BpmnModel bpmnModel = (new BpmnJsonConverter()).convertToBpmnModel(jsonNode);
            // 前面基础是基于 .addClasspathResource("processes/test.bpmn20.xml") 来部署的
            deploymentBuilder.addBpmnModel(resourceName, bpmnModel);
        } else {
            // 基于 xml 部署模型
            deploymentBuilder.addBytes(resourceName, modelData);
        }
        // 获取流程设计图片
        byte[] modelEditorSourceExtra = repositoryService.getModelEditorSourceExtra(id);
        if (!Arrays.isNullOrEmpty(modelEditorSourceExtra)) {
            // 对应数据库 ACT_RE_PROCDEF 表 DGRM_RESOURCE_NAME_ 字段
            deploymentBuilder.addInputStream(model.getKey() + ".png", new ByteArrayInputStream(modelEditorSourceExtra));
        }
        if (activitiProperties.getDeployment().isProjectManifestEnabled()) {
            // =================设置资源清单=================
            // 为了测试表中字段, 使用项目资源清单对应 PROJECT_RELEASE_VERSION_, 这个项目版本会保存在 ACT_RE_DEPLOYMENT 表的 PROJECT_RELEASE_VERSION_ 字段中, 同时更新 ACT_RE_PROCDEF 表的 APP_VERSION_ 字段
            // projectManifest 的使用可以在 org.activiti.engine.impl.bpmn.deployer.BpmnDeployer.setProcessDefinitionVersionsAndIds() 看到
            // - 如果设置资源清单, 则流程定义 (ProcessDefinitionEntity) 的版本号从 deployment 获取
            // - 如果未设置, 则流程定义将取 (ProcessDefinitionEntity) 最新版本 + 1, 即: latest.getVersion() + 1
            List<Deployment> list = repositoryService.createDeploymentQuery().deploymentName(model.getName()).orderByDeploymenTime().desc().list();
            // **注意** 如果设置了项目资源清单版本, 则 isAutoDeploymentEnabled 的判断规则会失效, 判断逻辑改为对比数据库中项目资源清单版本号:
            // - 版本号相同则数据过滤不做处理
            // - 版本号不同则 ACT_RE_DEPLOYMENT 表新增一条记录, 版本号 +1, ACT_RE_PROCDEF 表会根据部署时的模型文件数量新增对应 n 条数据, 同时版本 +1
            // org.activiti.engine.impl.cmd.DeployCmd.deploymentsDiffer():
            // !deployment.getProjectReleaseVersion().equals(saved.getProjectReleaseVersion());

            // 这里我们如果存在相同部署名称, 可以把版本号提取出来, 赋值给项目资源清单属性, 从列表中取第一个获取版本号
            String version = CollectionUtils.isEmpty(list) ? "1" : String.valueOf((list.get(0).getVersion() + 1));
            deploymentBuilder.setProjectManifest(deploymentService.buildProjectManifest(model.getName(), metaInfo.getString(ModelDataJsonConstants.MODEL_DESCRIPTION), version));
        }
        // 这里有个问题, 就是设置资源清单的话, deploymentBuilder 并没有设置 version 的方法, 第二次再部署的话, 会报错 act_re_procdef: UNIQUE KEY `ACT_UNIQ_PROCDEF` (`KEY_`,`VERSION_`,`TENANT_ID_`)
        // 原因就是上面说的设置资源清单版本号, 流程定义从 deployment 获取, 而 deployment 版本不会更新, 就会导致添加流程定义数据唯一索引重复: UNIQUE KEY
        // 所以这里我们使用 enableDuplicateFiltering(), 开启之后 deployment 会查询是否存在相同名称的部署信息:
        // 是否过滤重复, 默认为 false, 防止资源没有发生变化而再次执行部署方法产生的重复部署
        if (activitiProperties.getDeployment().isAutoDeploymentEnabled()) {
            // - false: 每次部署 ACT_RE_DEPLOYMENT 都会新增一条部署信息, 版本号是 1, ACT_RE_PROCDEF 会根据部署时的模型文件数量新增对应 n 条数据
            // - true: 部署时会判断部署名称和流程定义文件与数据库中是否相同:
            // * -- 名称相同, 流程定义文件内容相同, 数据过滤不做处理
            // * -- 名称相同, 流程定义文件内容不同, ACT_RE_DEPLOYMENT 表新增一条记录, 版本号 +1 , ACT_RE_PROCDEF 表不会新增数据
            // * -- 名称不同, 流程定义文件内容相同, ACT_RE_DEPLOYMENT 表新增一条记录, 版本号是 1, ACT_RE_PROCDEF 会根据部署时的模型文件数量新增对应 n 条数据, 同时版本 +1
            // * -- 名称不同, 流程定义文件内容不同, ACT_RE_DEPLOYMENT 表新增一条记录, 版本号是 1, ACT_RE_PROCDEF 会根据部署时的模型文件数量新增对应 n 条数据, 同时版本也是 1
            deploymentBuilder.enableDuplicateFiltering();
            // 这里有点绕，注释有点啰嗦
        }
        deploymentBuilder.name(model.getName()).category(model.getCategory()).key(model.getKey());
        try {
            // 通过流程文件需要满足这个后缀条件: "bpmn20.xml", "bpmn"
            // 图片需要满足: "png", "jpg", "gif", "svg"
            // 对应源码 org.activiti.engine.impl.bpmn.deployer.ResourceNameUtil
            Deployment deploy = deploymentBuilder.deploy();
            model.setDeploymentId(deploy.getId());
        } catch (PersistenceException p) {
            // 由上面的 setProjectManifest 衍生问题: 通过模型部署后, 再来使用上传文件部署会报错 act_re_procdef: UNIQUE KEY `ACT_UNIQ_PROCDEF`, 反过来亦是如此
            // 原因是上传模型部署使用的是流程文件节点 process id 赋值给 KEY_ 字段, 然后上面设置了 projectManifest, 流程定义 (ProcessDefinitionEntity) 的版本号从 deployment 获取
            // 就出现 ACT_RE_PROCDEF 表 KEY_ 和 版本号相同的情况, 这里解决办法就是规定上传的文件名称和模型名称相同才行, 否则代表流程定义信息已存在
            if (p.getCause() instanceof SQLIntegrityConstraintViolationException && p.getCause().getMessage().contains("Duplicate entry")) {
                throw new BusinessException("流程定义信息已存在, 若要更新版本, 请将上传的部署文件名称和模型名称设为相同");
            }
        } catch (Exception e) {
            throw new BusinessException("流程图不合规范，请重新设计");
        }
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
        // 流程唯一标识, 必须是下划线或者字母开头, 否则报错: cvc-datatype-valid.1.2.1: '0ee903b8-9695-11ef-beca-d6edbbd75e0a' 不是 'NCName' 的有效值。
        properties.put(StencilConstants.PROPERTY_PROCESS_ID, "_" + id);
        // 我这里是把模型的版本号作为流程设计版本号, 所以修改流程设计中的流程版本会不生效
        properties.put(StencilConstants.PROPERTY_PROCESS_VERSION, String.valueOf(revision));
        bpmnXml.put(EditorJsonConstants.EDITOR_SHAPE_PROPERTIES, properties);
        // 保存模型文件到 act_ge_bytearray 表
        repositoryService.addModelEditorSource(id, bpmnXml.toJSONString().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void saveSvg(String id, String svgXml) {
        try {
            // 更新流程设计图片
            repositoryService.addModelEditorSourceExtra(id, FileUtil.svg2Png(svgXml));
        } catch (TranscoderException | IOException e) {
            throw new BusinessException("更新流程设计图片异常");
        }
    }
}
