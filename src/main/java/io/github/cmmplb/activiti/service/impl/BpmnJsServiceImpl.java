package io.github.cmmplb.activiti.service.impl;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cmmplb.activiti.domain.dto.BpmnJsDTO;
import io.github.cmmplb.activiti.domain.vo.BpmnJsVO;
import io.github.cmmplb.activiti.handler.exection.BusinessException;
import io.github.cmmplb.activiti.service.BpmnJsService;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author penglibo
 * @date 2024-10-29 09:22:47
 * @since jdk 1.8
 */

@Service
public class BpmnJsServiceImpl implements BpmnJsService {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RepositoryService repositoryService;

    @Override
    public boolean saveDesign(BpmnJsDTO dto) {
        // bpmn-js 的参数是 xml
        ByteArrayInputStream in = new ByteArrayInputStream(dto.getXml().getBytes(StandardCharsets.UTF_8));
        // 创建XMLStreamReader读取XML资源
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = null;
        try {
            reader = factory.createXMLStreamReader(in);
        } catch (XMLStreamException e) {
            throw new BusinessException("转换流程设计文件异常");
        }
        // 把 Xml 转换成 BpmnModel 对象
        BpmnModel bpmnModel = new BpmnXMLConverter().convertToBpmnModel(reader);

        // 把 BpmnModel 对象转换成 json 字节数组, 保存模型文件
        repositoryService.addModelEditorSource(dto.getId(), JSON.toJSONBytes(bpmnModel));

        // 将 svg 图片转换为 png 保存
        InputStream svgStream = new ByteArrayInputStream(dto.getSvg().getBytes(StandardCharsets.UTF_8));
        TranscoderInput input = new TranscoderInput(svgStream);
        // png 图片生成器
        PNGTranscoder transcoder = new PNGTranscoder();
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        TranscoderOutput output = new TranscoderOutput(outStream);

        try {
            transcoder.transcode(input, output);
            final byte[] result = outStream.toByteArray();
            // 更新流程设计图片
            repositoryService.addModelEditorSourceExtra(dto.getId(), result);
            outStream.close();
        } catch (TranscoderException | IOException e) {
            throw new BusinessException("更新流程设计图片异常");
        }

        return true;
    }

    @Override
    public BpmnJsVO getBpmnInfoById(String id) {
        Model model = repositoryService.getModel(id);
        if (null == model) {
            throw new BusinessException("模型信息不存在");
        }
        byte[] modelData = repositoryService.getModelEditorSource(id);
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(modelData);
        } catch (IOException e) {
            throw new BusinessException("解析流程设计文件异常");
        }
        // 把资源转换为xml
        BpmnModel bpmnModel = (new BpmnJsonConverter()).convertToBpmnModel(jsonNode);
        byte[] xmlBytes = (new BpmnXMLConverter()).convertToXML(bpmnModel, StandardCharsets.UTF_8.name());

        BpmnJsVO vo = new BpmnJsVO();
        vo.setModelId(model.getId());
        vo.setName(model.getName());
        vo.setXml(new String(xmlBytes, StandardCharsets.UTF_8));
        return vo;
    }
}