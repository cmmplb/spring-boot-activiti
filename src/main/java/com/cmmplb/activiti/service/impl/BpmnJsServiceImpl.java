package com.cmmplb.activiti.service.impl;

import com.cmmplb.activiti.dto.ModelBpmnDTO;
import com.cmmplb.activiti.entity.Apply;
import com.cmmplb.activiti.service.ApplyService;
import com.cmmplb.activiti.service.BpmnJsService;
import com.cmmplb.activiti.util.ActivitiUtil;
import com.cmmplb.activiti.util.ServletUtil;
import com.cmmplb.activiti.vo.BpmnInfoVO;
import com.cmmplb.activiti.vo.BpmnProgressVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.Model;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author penglibo
 * @date 2023-12-13 16:15:39
 * @since jdk 1.8
 */

@Slf4j
@Service
@Transactional
public class BpmnJsServiceImpl implements BpmnJsService {

    @Autowired
    private ApplyService applyService;

    @Resource
    private ObjectMapper objectMapper;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RepositoryService repositoryService;

    @Override
    public BpmnInfoVO getBpmnInfo(String modelId) {
        BpmnInfoVO vo = new BpmnInfoVO();
        Model model = repositoryService.getModel(modelId);
        byte[] modelData = repositoryService.getModelEditorSource(modelId);
        if (model != null) {
            try {
                vo.setModelId(model.getId());
                vo.setName(model.getName());
                JsonNode jsonNode = objectMapper.readTree(modelData);
                // bpmn-js适配Activiti-Modeler，把资源转换为xml
                BpmnModel bpmnModel = (new BpmnJsonConverter()).convertToBpmnModel(jsonNode);
                byte[] xmlBytes = (new BpmnXMLConverter()).convertToXML(bpmnModel, "UTF-8");
                vo.setXml(new String(xmlBytes, StandardCharsets.UTF_8));
            } catch (Exception e) {
                log.error("Error creating model JSON", e);
            }
        }
        return vo;
    }

    @Override
    public void export(String modelId) {
        Model model = repositoryService.getModel(modelId);
        byte[] modelData = repositoryService.getModelEditorSource(modelId);
        // 处理bpmn-js的文件
        try {
            SAXReader saxReader = new SAXReader();
            ByteArrayInputStream in = new ByteArrayInputStream(modelData);
            Document document = saxReader.read(in);
            // 格式化输出
            OutputFormat format = OutputFormat.createPrettyPrint();
            // 指定XML字符集编码
            format.setEncoding("GBK");
            HttpServletResponse response = ServletUtil.getResponse();
            XMLWriter output = new XMLWriter(response.getOutputStream(), format);
            String filename = model.getName() + ".bpmn20.xml";
            response.setHeader("Content-Disposition", "attachment;filename=" + filename);
            response.setHeader("content-Type", "application/xml");
            response.flushBuffer();
            output.write(document);
            output.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean saveDesign(String modelId, ModelBpmnDTO dto) {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(dto.getXml().getBytes(StandardCharsets.UTF_8));
            // 创建XMLStreamReader读取XML资源
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = factory.createXMLStreamReader(in);
            // 把XML转换成BpmnModel对象,bpmn-js适配Activiti-Modeler
            BpmnModel bpmnModel = new BpmnXMLConverter().convertToBpmnModel(reader);
            ObjectNode objectNode = new BpmnJsonConverter().convertToJson(bpmnModel);
            Model model = repositoryService.getModel(modelId);
            objectNode.put(ModelDataJsonConstants.MODEL_NAME, dto.getName());
            objectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, dto.getDescription());
            model.setMetaInfo(objectNode.toString());
            model.setName(dto.getName());

            repositoryService.saveModel(model);

            repositoryService.addModelEditorSource(model.getId(), objectNode.toString().getBytes(StandardCharsets.UTF_8));

            InputStream svgStream = new ByteArrayInputStream(dto.getSvgXml().getBytes(StandardCharsets.UTF_8));
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
            throw new RuntimeException(e);
        }
        return true;
    }

    @Override
    public String showFlowChart(String processDefinitionId) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        byte[] xmlBytes = (new BpmnXMLConverter()).convertToXML(bpmnModel, "UTF-8");
        return new String(xmlBytes, StandardCharsets.UTF_8);
    }

    @Override
    public BpmnProgressVO showProgressChart(Long id) {

        Apply apply = applyService.getById(id);
        if (null == apply) {
            throw new RuntimeException("申请信息已删除");
        }
        BpmnProgressVO vo = new BpmnProgressVO();
        try {
            // 定义businessKey,一般为流程实例key与实际业务数据的结合
            String businessKey = apply.getDefKey() + ":" + apply.getId();
            // 如果流程结束(驳回)，当前流程实例为空
            ProcessInstance process = runtimeService.createProcessInstanceQuery()
                    .processDefinitionKey(apply.getDefKey())
                    .processInstanceBusinessKey(businessKey)
                    .singleResult();

            // 获取历史流程实例来查询流程进度
            HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery().processInstanceBusinessKey(businessKey).singleResult();
            if (null == processInstance) {
                throw new RuntimeException("流程信息不存在");
            }
            // 获取流程中已经执行的节点，按照执行先后顺序排序
            List<HistoricActivityInstance> historicActivityInstances = historyService.createHistoricActivityInstanceQuery()
                    // 这里，如果流程结束的话，process会为空，所以查询历史流程，这样也能看到结束的流程进度信息。
                    .processInstanceId(null == process ? processInstance.getId() : process.getId())
                    .orderByHistoricActivityInstanceStartTime().asc().list();
            BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
            byte[] xmlBytes = (new BpmnXMLConverter()).convertToXML(bpmnModel, "UTF-8");

            // highLightedActivities（需要高亮的执行流程节点集合的获取）以及
            // highLightedFlows（需要高亮流程连接线集合的获取）
            // 高亮流程已发生流转的线id集合-已执行的线
            List<String> highLightedFlowIds = ActivitiUtil.getHighLightedFlows(bpmnModel, historicActivityInstances);

            // 高亮已经执行流程节点ID集合-已执行的节点
            List<String> highLightedActivitiIds = historicActivityInstances.stream().map(HistoricActivityInstance::getActivityId).collect(Collectors.toList());

            // 正在执行的节点
            Set<String> activityIds = runtimeService.createExecutionQuery().processInstanceId(processInstance.getId()).list()
                    .stream().map(org.activiti.engine.runtime.Execution::getActivityId).filter(Objects::nonNull).collect(Collectors.toSet());

            vo.setHighLightedFlowIds(highLightedFlowIds);
            vo.setHighLightedActivitiIds(highLightedActivitiIds);
            vo.setActivityIds(activityIds);
            vo.setXml(new String(xmlBytes, StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("error", e);
        }
        return vo;
    }
}
