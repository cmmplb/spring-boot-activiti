package com.cmmplb.activiti.service.impl;

import com.cmmplb.activiti.dto.ModelBpmnDTO;
import com.cmmplb.activiti.service.BpmnJsService;
import com.cmmplb.activiti.util.ServletUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
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
import java.util.HashMap;
import java.util.Map;

/**
 * @author penglibo
 * @date 2023-12-13 16:15:39
 * @since jdk 1.8
 */

@Slf4j
@Service
@Transactional
public class BpmnJsServiceImpl implements BpmnJsService {

    @Resource
    private ObjectMapper objectMapper;

    @Autowired
    private RepositoryService repositoryService;

    @Override
    public Map<String, Object> getBpmnInfo(String modelId) {
        Map<String, Object> map = new HashMap<>();
        Model model = repositoryService.getModel(modelId);
        byte[] modelData = repositoryService.getModelEditorSource(modelId);
        if (model != null) {
            try {
                map.put(ModelDataJsonConstants.MODEL_NAME, model.getName());
                map.put(ModelDataJsonConstants.MODEL_ID, model.getId());
                JsonNode jsonNode = objectMapper.readTree(modelData);
                // bpmn-js适配Activiti-Modeler，把资源转换为xml
                BpmnModel bpmnModel = (new BpmnJsonConverter()).convertToBpmnModel(jsonNode);
                byte[] xmlBytes = (new BpmnXMLConverter()).convertToXML(bpmnModel, "UTF-8");
                map.put("xml", new String(xmlBytes, StandardCharsets.UTF_8));
            } catch (Exception e) {
                log.error("Error creating model JSON", e);
            }
        }
        return map;
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
}
