package io.github.cmmplb.activiti.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.github.cmmplb.activiti.convert.ModelerConvert;
import io.github.cmmplb.activiti.domain.dto.ModelerDTO;
import io.github.cmmplb.activiti.domain.vo.ModelerVO;
import io.github.cmmplb.activiti.handler.exection.BusinessException;
import io.github.cmmplb.activiti.service.ModelService;
import io.github.cmmplb.activiti.service.ModelerService;
import io.github.cmmplb.activiti.utils.ConverterUtil;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.bouncycastle.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * @author penglibo
 * @date 2024-10-28 17:29:29
 * @since jdk 1.8
 */

@Service
public class ModelerServiceImpl implements ModelerService {

    @Autowired
    private ModelService modelService;

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
        modelService.saveJsonXml(JSON.parseObject(dto.getJsonXml()), dto.getId(), dto.getName(), dto.getDescription(), null, revision);

        // 更新流程图片信息
        modelService.saveSvg(dto.getId(), dto.getSvgXml());
        return true;
    }

    @Override
    public ModelerVO getModelerJsonById(String id) {
        Model model = repositoryService.getModel(id);
        if (null == model) {
            throw new BusinessException("模型信息不存在");
        }
        ModelerVO vo = ConverterUtil.convert(ModelerConvert.class, model);
        vo.setModelId(model.getId());
        // 获取流程定义文件
        byte[] modelData = repositoryService.getModelEditorSource(id);
        if (!Arrays.isNullOrEmpty(modelData)) {
            String modelInfo = new String(modelData, StandardCharsets.UTF_8);
            vo.setModel(JSON.parseObject(modelInfo));
        }
        return vo;
    }
}
