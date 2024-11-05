package io.github.cmmplb.activiti.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.github.cmmplb.activiti.domain.dto.BpmnJsDTO;
import io.github.cmmplb.activiti.domain.vo.BpmnJsVO;
import io.github.cmmplb.activiti.handler.exection.BusinessException;
import io.github.cmmplb.activiti.service.BpmnJsService;
import io.github.cmmplb.activiti.service.ModelService;
import io.github.cmmplb.activiti.utils.XmlUtil;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * @author penglibo
 * @date 2024-10-29 09:22:47
 * @since jdk 1.8
 */

@Service
public class BpmnJsServiceImpl implements BpmnJsService {

    @Autowired
    private ModelService modelService;

    // 管理和控制流程定义的服务接口，包括部署、查询和删除流程定义等。
    @Autowired
    private RepositoryService repositoryService;

    @Override
    public boolean saveDesign(BpmnJsDTO dto) {
        Model model = repositoryService.getModel(dto.getId());
        if (null == model) {
            throw new BusinessException("模型信息不存在");
        }
        // 获取 xml 标签中的值
        String name = XmlUtil.getElementAttribute(dto.getXml(), "bpmn:process", "name");
        String description = XmlUtil.getElementContent(dto.getXml(), "bpmn:documentation");
        // 版本号, 这里直接在代码里加 1 了, 应该在数据库利用行锁 version = version + 1 的方式来修改, 或者加锁, 防止数据重复更新
        int revision = model.getVersion() + 1;
        JSONObject metaInfo = JSON.parseObject(model.getMetaInfo());
        // 设计界面也可以修改相关模型信息
        if (StringUtils.isNotEmpty(name)) {
            metaInfo.put(ModelDataJsonConstants.MODEL_NAME, name);
            model.setName(name);
        }
        if (StringUtils.isNotEmpty(description)) {
            metaInfo.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
        }
        // 版本号从模型信息中获取, 因为设计页面上的版本号是字符串, 而模型信息的版本号是 int, 防止转换异常
        metaInfo.put(ModelDataJsonConstants.MODEL_REVISION, revision);
        model.setMetaInfo(metaInfo.toString());
        model.setVersion(revision);
        // 更新模型信息
        repositoryService.saveModel(model);

        // 更新模型设计文件中的版本号
        String xml = XmlUtil.setElementAttribute(dto.getXml(), "bpmn:process", "camunda:versionTag", revision + "");
        // 更新流程设计文件, 注意, activiti-modeler 保存的是 json, bpmn-js 保存的是 xml
        repositoryService.addModelEditorSource(dto.getId(), xml.getBytes(StandardCharsets.UTF_8));

        // 更新流程图片信息
        modelService.saveSvg(dto.getId(), dto.getSvg());
        return true;
    }

    @Override
    public BpmnJsVO getBpmnInfoById(String id) {
        Model model = repositoryService.getModel(id);
        if (null == model) {
            throw new BusinessException("模型信息不存在");
        }
        byte[] modelData = repositoryService.getModelEditorSource(id);
        BpmnJsVO vo = new BpmnJsVO();
        vo.setModelId(model.getId());
        if (!Arrays.isNullOrEmpty(modelData)) {
            vo.setXml(new String(modelData, StandardCharsets.UTF_8));
        }
        return vo;
    }
}