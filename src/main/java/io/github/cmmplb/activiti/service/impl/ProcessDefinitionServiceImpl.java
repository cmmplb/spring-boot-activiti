package io.github.cmmplb.activiti.service.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.cmmplb.activiti.beans.PageResult;
import io.github.cmmplb.activiti.beans.QueryPageBean;
import io.github.cmmplb.activiti.convert.ProcessDefinitionConvert;
import io.github.cmmplb.activiti.domain.dto.ModelDTO;
import io.github.cmmplb.activiti.domain.dto.SuspendDefinitionDTO;
import io.github.cmmplb.activiti.domain.vo.ProcessDefinitionVO;
import io.github.cmmplb.activiti.handler.exection.BusinessException;
import io.github.cmmplb.activiti.service.ModelService;
import io.github.cmmplb.activiti.service.ProcessDefinitionService;
import io.github.cmmplb.activiti.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author penglibo
 * @date 2024-11-02 18:57:56
 * @since jdk 1.8
 */

@Slf4j
@Service
public class ProcessDefinitionServiceImpl implements ProcessDefinitionService {

    // 管理和控制流程定义的服务接口，包括部署、查询和删除流程定义等功能；
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ModelService modelService;

    @Override
    public PageResult<ProcessDefinitionVO> getByPaged(QueryPageBean queryPageBean) {
        ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery();
        // 根据模型名称模糊查询
        if (StringUtils.isNotBlank(queryPageBean.getKeywords())) {
            query.processDefinitionNameLike(queryPageBean.getKeywords());
        }
        List<ProcessDefinitionVO> res = new ArrayList<>();
        // count 查询总数
        long total = query.count();
        if (total > 0) {
            List<ProcessDefinition> list = query.listPage(queryPageBean.getStart(), queryPageBean.getSize());
            // .stream().map() jdk 8 语法
            return new PageResult<>(total, list.stream().map(processDefinition -> {
                        ProcessDefinitionVO vo = ConverterUtil.convert(ProcessDefinitionConvert.class, processDefinition);
                        vo.setSuspended(processDefinition.isSuspended());
                        return vo;
                    }
            ).collect(Collectors.toList()));
        }
        return new PageResult<>(total, res);
    }

    @Override
    public String show(String deploymentId, String resourceName) {
        InputStream is = repositoryService.getResourceAsStream(deploymentId, resourceName);
        byte[] xmlBytes;
        try {
            xmlBytes = IOUtils.toByteArray(is);
        } catch (IOException e) {
            throw new BusinessException("解析部署流程文件失败");
        }
        try {
            return XmlUtil.formatXml(new String(xmlBytes, StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.info("格式化 xml 失败");
        }
        // 格式化失败的话返回原始数据
        return new String(xmlBytes, StandardCharsets.UTF_8);
    }

    @Override
    public void showChart(String id) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(id);
        if (null == bpmnModel) {
            throw new BusinessException("流程文件信息不存在");
        }
        // ========activiti 7 移除了 processEngineConfiguration.getProcessDiagramGenerator() 的方法========
        // ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        // ProcessEngineConfigurationImpl processEngineConfiguration = (ProcessEngineConfigurationImpl) defaultProcessEngine.getProcessEngineConfiguration();
        // ProcessDiagramGenerator processDiagramGenerator = processEngineConfiguration.getProcessDiagramGenerator();
        // processDiagramGenerator.generateDiagram(bpmnModel, "宋体", "宋体", "宋体");

        // ========改为了直接 new 该接口的实例========
        ProcessDiagramGenerator diagramGenerator = new DefaultProcessDiagramGenerator();
        // 但是用这个会报错 nested exception is java.lang.NoClassDefFoundError: org/apache/batik/util/XMLConstants
        // batik-util:1.9 的版本移除了 XMLConstants, 迁移到 batik-constants 包里面了
        // 查看 jar 依赖版本发现这个类 org.apache.batik.dom.util.DOMUtilities 包的版本是 batik-dom:1.10, 应该是和哪个依赖冲突了
        // 找了一下在 org.activiti:activiti-image-generator 这个依赖里面发现依赖冲突, pom 里面排除一下
        InputStream is = diagramGenerator.generateDiagram(bpmnModel, "宋体", "宋体", "宋体");
        // =======================================

        HttpServletResponse response = ServletUtil.getResponse();
        try {
            // 生成的图表默认是 svg 格式的
            // response.setContentType("image/svg+xml");
            // IOUtils.copy(is, response.getOutputStream());

            // 转换 svg 为 png 响应
            response.setContentType("image/png");
            new PNGTranscoder().transcode(new TranscoderInput(is), new TranscoderOutput(response.getOutputStream()));

        } catch (Exception e) {
            throw new BusinessException("流程文件转换为图形失败");
        }
    }

    @Override
    public String showChartBpmnJs(String id) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(id);
        if (null == bpmnModel) {
            throw new BusinessException("流程文件信息不存在");
        }
        // bpmn-js 通过 xml 来生成流程图
        byte[] xmlBytes = (new BpmnXMLConverter()).convertToXML(bpmnModel, StandardCharsets.UTF_8.name());
        return new String(xmlBytes, StandardCharsets.UTF_8);
    }

    @Override
    public boolean exchangeToModel(String id, Integer designType) {
        // 根据 id 获取流程定义信息
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery().processDefinitionId(id).singleResult();
        if (null == definition) {
            throw new BusinessException("流程定义信息不存在");
        }
        // 这里看需求, 转换的模型存在是否更新版本替换, 我这里是调用新增模型了, 不允许重复, 如果要更新替换要补充一下逻辑
        ModelDTO dto = new ModelDTO();
        dto.setKey(definition.getKey());
        dto.setName(definition.getName());
        dto.setAuthor(SecurityUtil.getUserName());
        dto.setCategory(definition.getCategory());
        dto.setDescription(definition.getDescription());
        dto.setDesignType(designType);
        dto.setGenerateProcess(false);
        // 保存模型信息
        modelService.save(dto);

        // 根据 id 获取流程定义的 bpmn 文件
        BpmnModel bpmnModel = repositoryService.getBpmnModel(id);
        // 添加流程设计文件, 注意, activiti-modeler 保存的是 json, bpmn-js 保存的是 xml
        byte[] modelData;
        if (designType == 1) {
            // 使用 BpmnJsonConverter 把 bpmnModel 转换为 jsonNode
            ObjectNode objectNode = new BpmnJsonConverter().convertToJson(bpmnModel);
            modelData = objectNode.toString().getBytes(StandardCharsets.UTF_8);
        } else {
            // 使用 BpmnXMLConverter 把 bpmnModel 转换为 xml
            modelData = new BpmnXMLConverter().convertToXML(bpmnModel);
        }
        repositoryService.addModelEditorSource(dto.getId(), modelData);
        return true;
    }

    @Override
    public boolean suspend(SuspendDefinitionDTO dto) {
        return activate(false, dto);
    }

    @Override
    public boolean activate(SuspendDefinitionDTO dto) {
        return activate(true, dto);
    }

    public boolean activate(boolean isActivate, SuspendDefinitionDTO dto) {
        Date date = null;
        if (StringUtils.isNotEmpty(dto.getActivationDate())) {
            date = DateUtil.parseToDate(dto.getActivationDate(), DateUtil.FORMAT_DATE_YYYY_MM_DD_HH_MM_SS);
        }
        try {
            if (isActivate) {
                // 激活, 并且激活关联的实例
                repositoryService.activateProcessDefinitionById(dto.getId(), dto.getActivateProcessInstances(), date);
            } else {
                // 挂起
                repositoryService.suspendProcessDefinitionById(dto.getId(), dto.getActivateProcessInstances(), date);
            }
        } catch (Exception e) {
            log.error("激活流程失败", e);
            throw new BusinessException(isActivate ? "激活流程失败" : "挂起流程失败");
        }
        return true;
    }
}