package io.github.cmmplb.activiti.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.github.cmmplb.activiti.convert.ModelerConvert;
import io.github.cmmplb.activiti.domain.dto.ModelerDTO;
import io.github.cmmplb.activiti.domain.vo.ModelerVO;
import io.github.cmmplb.activiti.handler.exection.BusinessException;
import io.github.cmmplb.activiti.service.ModelerService;
import io.github.cmmplb.activiti.utils.ConverterUtil;
import org.activiti.editor.constants.EditorJsonConstants;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.constants.StencilConstants;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author penglibo
 * @date 2024-10-28 17:29:29
 * @since jdk 1.8
 */

@Service
public class ModelerServiceImpl implements ModelerService {

    @Autowired
    private RepositoryService repositoryService;

    @Override
    public boolean saveDesign(ModelerDTO dto) {
        Model model = repositoryService.getModel(dto.getId());
        if (null == model) {
            throw new RuntimeException("模型信息不存在");
        }
        // 前端调用的参数：toolbar-default-actions.js：
        // var params = {
        //  jsonXml: json,
        //  svgXml: svgDOM,
        //  name: $scope.saveDialog.name,
        //  description: $scope.saveDialog.description
        // };
        JSONObject metaInfo = JSON.parseObject(model.getMetaInfo());

        // 版本号, 这里直接在代码里加 1 了, 应该在数据库利用行锁 version = version + 1 的方式来修改, 或者加锁, 防止数据重复更新
        int revision = model.getVersion() + 1;
        // 设计界面也可以修改相关模型信息
        metaInfo.put(ModelDataJsonConstants.MODEL_NAME, dto.getName());
        // 版本号从模型信息中获取, 因为设计页面上的版本号是字符串, 而模型信息的版本号是 int, 防止转换异常
        metaInfo.put(ModelDataJsonConstants.MODEL_REVISION, revision);
        metaInfo.put(ModelDataJsonConstants.MODEL_DESCRIPTION, dto.getDescription());

        model.setMetaInfo(metaInfo.toString());
        model.setName(dto.getName());
        model.setVersion(revision);
        // 更新模型信息
        repositoryService.saveModel(model);

        // 更新流程设计文件
        JSONObject bpmnXml = JSON.parseObject(dto.getJsonXml());
        // 修改流程文件中的版本号 ( 这里看需求, 是以模型的版本号还是取流程设计的版本号 )
        JSONObject properties = bpmnXml.getJSONObject(EditorJsonConstants.EDITOR_SHAPE_PROPERTIES);
        // 名称
        properties.put(StencilConstants.PROPERTY_NAME, dto.getName());
        // 描述
        properties.put(StencilConstants.PROPERTY_DOCUMENTATION, dto.getDescription());
        // 我这里是把模型的版本号作为流程设计版本号, 所以修改流程设计中的流程版本会不生效
        properties.put(StencilConstants.PROPERTY_PROCESS_VERSION, String.valueOf(revision));
        bpmnXml.put(EditorJsonConstants.EDITOR_SHAPE_PROPERTIES, properties);
        repositoryService.addModelEditorSource(model.getId(), bpmnXml.toJSONString().getBytes(StandardCharsets.UTF_8));

        // 将 svg 图片转换为 png 保存
        InputStream svgStream = new ByteArrayInputStream(dto.getSvgXml().getBytes(StandardCharsets.UTF_8));
        TranscoderInput input = new TranscoderInput(svgStream);
        // png 图片生成器
        PNGTranscoder transcoder = new PNGTranscoder();
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        TranscoderOutput output = new TranscoderOutput(outStream);

        try {
            transcoder.transcode(input, output);
            final byte[] result = outStream.toByteArray();
            // 更新流程设计图片
            repositoryService.addModelEditorSourceExtra(model.getId(), result);
            outStream.close();
        } catch (TranscoderException | IOException e) {
            throw new BusinessException("更新流程设计图片异常");
        }
        return true;
    }

    @Override
    public ModelerVO getModelerJsonById(String id) {
        Model model = repositoryService.getModel(id);
        if (null == model) {
            throw new BusinessException("模型信息不存在");
        }
        ModelerVO vo = ConverterUtil.convert(ModelerConvert.class, model);
        // 获取流程定义文件
        String modelInfo = new String(repositoryService.getModelEditorSource(model.getId()), StandardCharsets.UTF_8);
        vo.setModelId(model.getId());
        vo.setModel(JSON.parseObject(modelInfo));
        return vo;
    }
}
