package io.github.cmmplb.activiti.service.act.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.cmmplb.activiti.beans.PageResult;
import io.github.cmmplb.activiti.beans.QueryPageBean;
import io.github.cmmplb.activiti.configuration.properties.ActivitiProperties;
import io.github.cmmplb.activiti.constants.BizConstant;
import io.github.cmmplb.activiti.convert.act.DeploymentConvert;
import io.github.cmmplb.activiti.domain.dto.biz.DeploymentDTO;
import io.github.cmmplb.activiti.domain.vo.act.DeploymentVO;
import io.github.cmmplb.activiti.handler.exection.BusinessException;
import io.github.cmmplb.activiti.service.act.DeploymentService;
import io.github.cmmplb.activiti.utils.BpmnJsUtil;
import io.github.cmmplb.activiti.utils.ConverterUtil;
import io.github.cmmplb.activiti.utils.DateUtil;
import io.github.cmmplb.activiti.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.core.common.project.model.ProjectManifest;
import org.activiti.editor.constants.EditorJsonConstants;
import org.activiti.editor.constants.StencilConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.DeploymentQuery;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author penglibo
 * @date 2024-10-31 16:39:09
 * @since jdk 1.8
 */

@Slf4j
@Service
public class DeploymentServiceImpl implements DeploymentService {

    // 管理和控制流程定义的服务接口，包括部署、查询和删除流程定义等功能；
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ActivitiProperties activitiProperties;

    @Override
    public PageResult<DeploymentVO> getByPaged(QueryPageBean queryPageBean) {
        DeploymentQuery query = repositoryService.createDeploymentQuery();
        if (StringUtils.isNotEmpty(queryPageBean.getKeywords())) {
            // 根据模型名称模糊查询
            query.deploymentNameLike(queryPageBean.getKeywords());
        }
        List<DeploymentVO> res = new ArrayList<>();
        // count 查询总数
        long total = query.count();
        // 阿里规约, 代码中写分页查询逻辑时, 若count为0应直接返回, 避免执行后面的分页语句
        if (total > 0) {
            query.orderByDeploymenTime().desc();
            // 根据部署时间倒序, 分页查询
            List<Deployment> list = query.orderByDeploymenTime().desc().listPage(queryPageBean.getStart(), queryPageBean.getSize());
            // .stream().map() jdk 8 语法
            return new PageResult<>(total, list.stream().map(deployment -> ConverterUtil.convert(DeploymentConvert.class, deployment)
            ).collect(Collectors.toList()));
        }
        return new PageResult<>(total, res);
    }

    @Override
    public boolean upload(MultipartFile[] files) {
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            if (StringUtils.isBlank(fileName)) {
                throw new BusinessException("文件格式错误");
            }
            String suffixName = fileName;
            if (fileName.lastIndexOf(".") != -1) {
                // 文件后缀名
                suffixName = fileName.substring(fileName.lastIndexOf("."));
            }
            // 通过流程文件需要满足这个后缀条件: "bpmn20.xml", "bpmn"
            // 图片需要满足: "png", "jpg", "gif", "svg"
            // 对应源码 org.activiti.engine.impl.bpmn.deployer.ResourceNameUtil
            if (!".zip".equals(suffixName) && !".xml".equals(suffixName)) {
                throw new BusinessException("文件格式错误");
            }
            try {
                InputStream is = file.getInputStream();
                // 拷贝输入流
                ByteArrayOutputStream stream = getByteArrayOutputStream(is);

                DeploymentDTO dto = null;
                if (".xml".equals(suffixName)) {
                    // 提取属性
                    dto = extractAttribute(new ByteArrayInputStream(stream.toByteArray()));

                } else {
                    // 处理 zip 压缩文件
                    ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(stream.toByteArray()));
                    ZipEntry zipEntry;
                    while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                        String zipName = zipEntry.getName();
                        // 只处理 xml
                        if (zipName.endsWith(".xml")) {
                            // 防止 zipInputStream closed 异常, 拷贝输入流
                            dto = extractAttribute(IOUtils.toBufferedInputStream(zipInputStream));
                        }
                        zipInputStream.closeEntry();
                    }
                }
                if (null == dto) {
                    throw new BusinessException("文件解析失败");
                }
                DeploymentBuilder deploymentBuilder = repositoryService.createDeployment()
                        .name(dto.getName())
                        .key(dto.getKey())
                        .category(dto.getCategory());
                if (activitiProperties.getDeployment().isProjectManifestEnabled()) {
                    // =================设置资源清单=================
                    // 为了测试表中字段, 使用项目资源清单对应 PROJECT_RELEASE_VERSION_, 这个项目版本会保存在 ACT_RE_DEPLOYMENT 表的 PROJECT_RELEASE_VERSION_ 字段中, 同时更新 ACT_RE_PROCDEF 表的 APP_VERSION_ 字段
                    // projectManifest 的使用可以在 org.activiti.engine.impl.bpmn.deployer.BpmnDeployer.setProcessDefinitionVersionsAndIds() 看到
                    // - 如果设置资源清单, 则流程定义 (ProcessDefinitionEntity) 的版本号从 deployment 获取
                    // - 如果未设置, 则流程定义将取 (ProcessDefinitionEntity) 最新版本 + 1, 即: latest.getVersion() + 1
                    List<Deployment> list = repositoryService.createDeploymentQuery().deploymentName(dto.getName()).orderByDeploymenTime().desc().list();
                    // **注意** 如果设置了项目资源清单版本, 则 isAutoDeploymentEnabled 的判断规则会失效, 判断逻辑改为对比数据库中项目资源清单版本号:
                    // - 版本号相同则数据过滤不做处理
                    // - 版本号不同则 ACT_RE_DEPLOYMENT 表新增一条记录, 版本号 +1, ACT_RE_PROCDEF 表会根据部署时的模型文件数量新增对应 n 条数据, 同时版本 +1
                    // org.activiti.engine.impl.cmd.DeployCmd.deploymentsDiffer():
                    // !deployment.getProjectReleaseVersion().equals(saved.getProjectReleaseVersion());

                    // 这里我们如果存在相同部署名称, 可以把版本号提取出来, 赋值给项目资源清单属性, 从列表中取第一个获取版本号
                    String version = CollectionUtils.isEmpty(list) ? "1" : String.valueOf((list.get(0).getVersion() + 1));
                    deploymentBuilder.setProjectManifest(buildProjectManifest(dto.getName(), dto.getDescription(), version));
                }
                // 这里有个问题, 就是设置资源清单的话, deploymentBuilder 并没有设置 version 的方法, 第二次再部署的话, 会报错 act_re_procdef: UNIQUE KEY `ACT_UNIQ_PROCDEF` (`KEY_`,`VERSION_`,`TENANT_ID_`)
                // 原因就是上面说的设置资源清单版本号, 流程定义从 deployment 获取, 而 deployment 版本不会更新, 就会导致添加流程定义数据唯一索引重复: UNIQUE KEY
                // 开启 isAutoDeploymentEnabled 之后 deployment 会查询是否存在相同名称的部署信息:
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
                ByteArrayInputStream inputStream = new ByteArrayInputStream(stream.toByteArray());
                if (".zip".equals(suffixName)) {
                    deploymentBuilder.addZipInputStream(new ZipInputStream(inputStream));
                } else {
                    deploymentBuilder.addInputStream(fileName, inputStream);
                }
                deploymentBuilder.deploy();
            } catch (PersistenceException p) {
                // 由上面的 setProjectManifest 衍生问题: 通过模型部署后, 再来使用上传文件部署会报错 act_re_procdef: UNIQUE KEY `ACT_UNIQ_PROCDEF`, 反过来亦是如此
                // 原因是上传模型部署使用的是流程文件节点 process id 赋值给 KEY_ 字段, 然后上面设置了 projectManifest, 流程定义 (ProcessDefinitionEntity) 的版本号从 deployment 获取
                // 就出现 ACT_RE_PROCDEF 表 KEY_ 和 版本号相同的情况, 这里解决办法就是规定上传的文件名称和模型名称相同才行, 否则代表流程定义信息已存在
                if (p.getCause() instanceof SQLIntegrityConstraintViolationException && p.getCause().getMessage().contains("Duplicate entry")) {
                    throw new BusinessException("流程定义信息已存在, 若要更新版本, 请将上传的部署文件名称和模型名称设为相同");
                }
            } catch (Exception e) {
                log.error("部署失败", e);
                throw new BusinessException("部署失败");
            }
        }
        return true;
    }

    private static ByteArrayOutputStream getByteArrayOutputStream(InputStream input) throws IOException {
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = input.read(buffer)) > -1) {
                stream.write(buffer, 0, len);
            }
            // flush会将缓存中数据强制输出到文件，然后清空缓存中的所有数据
            stream.flush();
            return stream;
        }
    }

    private static DeploymentDTO extractAttribute(InputStream is) throws IOException {
        DeploymentDTO dto = new DeploymentDTO();
        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = factory.createXMLStreamReader(is);
            //  使用 activiti-json-converter 依赖中的转换器 将 xml 转换成 BpmnModel
            BpmnModel bpmnModel = new BpmnXMLConverter().convertToBpmnModel(reader);
            // 之后将 BpmnModel 转换成 json
            ObjectNode jsonNodes = new BpmnJsonConverter().convertToJson(bpmnModel);
            JSONObject bpmnXml = JSON.parseObject(new String(jsonNodes.toString().getBytes(), StandardCharsets.UTF_8));
            JSONObject properties = bpmnXml.getJSONObject(EditorJsonConstants.EDITOR_SHAPE_PROPERTIES);
            // key
            dto.setKey(properties.getString(StencilConstants.PROPERTY_PROCESS_ID));
            // 名称
            dto.setName(properties.getString(StencilConstants.PROPERTY_NAME));
            // modeler 没有该属性, 这里匹配定义的常量
            dto.setCategory(BizConstant.ProcessEnum.getCategory(dto.getKey()));
            dto.setDescription(properties.getString(StencilConstants.PROPERTY_DOCUMENTATION));

        } catch (Exception e) {
            // 转换失败, 解析 bpmn-js
            String xml = new String(IOUtils.toByteArray(is), StandardCharsets.UTF_8);
            dto.setKey(BpmnJsUtil.getId(xml));
            dto.setName(BpmnJsUtil.getName(xml));
            dto.setCategory(BpmnJsUtil.getCategory(xml));
            dto.setDescription(BpmnJsUtil.getDescription(xml));
        }
        return dto;
    }

    @Override
    public boolean removeById(String id) {
        // 删除部署，如果有同时在运行的流程则会抛出异常
        // repositoryService.deleteDeployment(id);
        // 删除部署，同时及联删除关联的流程
        repositoryService.deleteDeployment(id, true);
        return true;
    }

    // 构建项目清单
    @Override
    public ProjectManifest buildProjectManifest(String name, String description, String version) {
        ProjectManifest manifest = new ProjectManifest();
        manifest.setId(UUID.randomUUID().toString());
        manifest.setCreatedBy(SecurityUtil.getUserName());
        manifest.setCreationDate(DateUtil.getCurrentDateTimeString());
        manifest.setLastModifiedBy(SecurityUtil.getUserName());
        manifest.setLastModifiedDate(DateUtil.getCurrentDateTimeString());
        manifest.setName(name);
        manifest.setDescription(description);
        manifest.setVersion(version);
        return manifest;
    }
}
