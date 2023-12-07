package com.cmmplb.activiti.service.impl;

import com.cmmplb.activiti.beans.PageResult;
import com.cmmplb.activiti.beans.QueryPageBean;
import com.cmmplb.activiti.dto.SuspendActivateProcessDefinitionDTO;
import com.cmmplb.activiti.image.ProcessDiagramGeneratorImpl;
import com.cmmplb.activiti.service.DeployService;
import com.cmmplb.activiti.util.ServletUtil;
import com.cmmplb.activiti.vo.ProcessDefinitionVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipInputStream;

/**
 * @author penglibo
 * @date 2023-11-15 10:34:32
 * @since jdk 1.8
 */

@Slf4j
@Service
@Transactional
public class DeployServiceImpl implements DeployService {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Override
    public PageResult<ProcessDefinitionVO> getByPaged(QueryPageBean queryPageBean) {
        ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery();
        List<ProcessDefinitionVO> processList = new ArrayList<>();
        long count = query.count();
        if (count > 0) {
            if (StringUtils.isNotBlank(queryPageBean.getKeywords())) {
                query.processDefinitionNameLike(queryPageBean.getKeywords());
            }
            List<ProcessDefinition> definitionList = query.orderByDeploymentId().desc().listPage(queryPageBean.getStart(), queryPageBean.getSize());
            // 提取所有的流程名称
            ProcessDefinitionVO vo;
            for (ProcessDefinition definition : definitionList) {
                vo = new ProcessDefinitionVO();
                vo.setId(definition.getId());
                vo.setName(definition.getName());
                vo.setCategory(definition.getCategory());
                vo.setKey(definition.getKey());
                vo.setDescription(definition.getDescription());
                vo.setVersion(definition.getVersion());
                vo.setResourceName(definition.getResourceName());
                vo.setDeploymentId(definition.getDeploymentId());
                vo.setIsSuspended(definition.isSuspended());
                processList.add(vo);
            }
        }
        return new PageResult<>(count, processList);
    }

    @Override
    public boolean upload(MultipartFile[] files) {
        try {
            for (MultipartFile file : files) {
                String filename = file.getOriginalFilename();
                if (StringUtils.isBlank(filename)) {
                    throw new RuntimeException("文件格式错误");
                }
                InputStream is = file.getInputStream();
                if (filename.endsWith("zip")) {
                    repositoryService.createDeployment().name(filename).addZipInputStream(new ZipInputStream(is)).deploy();
                } else if (filename.endsWith("bpmn") || filename.endsWith("xml")) {
                    repositoryService.createDeployment().name(filename).addInputStream(filename, is).deploy();
                } else {
                    throw new RuntimeException("文件格式错误");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("部署失败");
        }
        return true;
    }

    @Override
    public boolean removeById(String deploymentId) {
        // 删除部署，如果有同时在运行的流程则会抛出异常
        // repositoryService.deleteDeployment(deploymentId);
        // 删除部署，同时及联删除关联的流程
        repositoryService.deleteDeployment(deploymentId, true);
        return true;
    }

    @Override
    public void showProcessDefinition(String deploymentId, String resource) {
        InputStream is = repositoryService.getResourceAsStream(deploymentId, resource);
        HttpServletResponse response = ServletUtil.getResponse();
        ServletOutputStream output = null;
        try {
            output = response.getOutputStream();
            response.setContentType("text/xml;charset=utf-8");
            IOUtils.copy(is, output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void showProcessChart(String processDefinitionId) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        // activiti7移除了静态方法创建，需要DefaultProcessDiagramGenerator实例。
        // ProcessDiagramGenerator diagramGenerator = new DefaultProcessDiagramGenerator();
        // 由于是创建的新实例，这里的DiagramGenerator就不用注入到配置类里面了，当然ActivitiConfiguration配置类也移除了set的方法。
        ProcessDiagramGeneratorImpl diagramGenerator = new ProcessDiagramGeneratorImpl();
        InputStream is = diagramGenerator.generateDiagram(bpmnModel, "宋体", "宋体", "宋体");
        try {
            HttpServletResponse response = ServletUtil.getResponse();

            // 响应svg到客户端
            // response.setContentType("image/svg+xml");
            // IOUtils.copy(is, response.getOutputStream());

            // 转换svg为png响应
            response.setContentType("image/png");
            new PNGTranscoder().transcode(new TranscoderInput(is), new TranscoderOutput(response.getOutputStream()));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean exchangeProcessToModel(String processDefinitionId) {
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(definition.getId());
        ObjectNode objectNode = new BpmnJsonConverter().convertToJson(bpmnModel);
        Model modelData = repositoryService.newModel();
        modelData.setKey(definition.getKey());
        modelData.setName(definition.getName());
        modelData.setCategory(definition.getCategory());
        ObjectNode modelJson = new ObjectMapper().createObjectNode();
        modelJson.put(ModelDataJsonConstants.MODEL_NAME, definition.getName());
        modelJson.put(ModelDataJsonConstants.MODEL_DESCRIPTION, definition.getDescription());
        List<Model> models = repositoryService.createModelQuery().modelKey(definition.getKey()).list();
        if (models.size() > 0) {
            Integer version = models.get(0).getVersion();
            version++;
            modelJson.put(ModelDataJsonConstants.MODEL_REVISION, version);
            // 删除旧模型
            repositoryService.deleteModel(models.get(0).getId());
            modelData.setVersion(version);
        } else {
            modelJson.put(ModelDataJsonConstants.MODEL_REVISION, 1);
        }
        modelData.setMetaInfo(modelJson.toString());
        modelData.setDeploymentId(definition.getDeploymentId());
        // 保存新模型
        repositoryService.saveModel(modelData);
        // 保存模型json
        repositoryService.addModelEditorSource(modelData.getId(), objectNode.toString().getBytes(StandardCharsets.UTF_8));
        return true;
    }

    @Override
    public boolean suspendProcessDefinition(String processDefinitionId, SuspendActivateProcessDefinitionDTO dto) {
        if (StringUtils.isNotEmpty(dto.getActivationDate())) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 挂起关联的实例，suspendProcessInstances
            try {
                repositoryService.suspendProcessDefinitionById(processDefinitionId, dto.getActivateProcessInstances(), sdf.parse(dto.getActivationDate()));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        } else {
            repositoryService.suspendProcessDefinitionById(processDefinitionId, dto.getActivateProcessInstances(), null);
        }
        return true;
    }

    @Override
    public boolean activateProcessDefinition(String processDefinitionId, SuspendActivateProcessDefinitionDTO dto) {
        if (StringUtils.isNotEmpty(dto.getActivationDate())) {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                repositoryService.activateProcessDefinitionById(processDefinitionId, dto.getActivateProcessInstances(), sdf.parse(dto.getActivationDate()));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        } else {
            repositoryService.activateProcessDefinitionById(processDefinitionId, dto.getActivateProcessInstances(), null);
        }
        return true;
    }
}
