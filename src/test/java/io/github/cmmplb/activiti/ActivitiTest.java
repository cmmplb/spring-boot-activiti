package io.github.cmmplb.activiti;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.activiti.editor.constants.EditorJsonConstants;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author penglibo
 * @date 2024-10-21 13:53:53
 * @since jdk 1.8
 */

@Slf4j
@SpringBootTest
public class ActivitiTest {

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode metaInfo = objectMapper.createObjectNode();
        metaInfo.put(ModelDataJsonConstants.MODEL_NAME, "张三");
        metaInfo.put(ModelDataJsonConstants.MODEL_REVISION, 1);
        metaInfo.put(ModelDataJsonConstants.MODEL_DESCRIPTION, "李四");
        System.out.println(metaInfo.toString());


        Map<String, Object> content = new HashMap<>();
        // 为什么是 canvas 这个值? 可以去设计一个流程之后, 查看 getModelEditorSource 返回的 json 数据, 等后面写了流程设计之后查看
        content.put(EditorJsonConstants.EDITOR_SHAPE_ID, "canvas");
        content.put("1", "canvas");
       ;
        System.out.println( objectMapper.writeValueAsString(content));
    }

    @Test
    // DisplayName 用于指定单元测试的名称
    @DisplayName("部署流程")
    public void deploy() {
        // 获取流程引擎实例
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 获取 RepositoryService，管理和控制流程定义的服务接口，包括部署、查询和删除流程定义等功能。
        RepositoryService repositoryService = processEngine.getRepositoryService();
        // 创建部署操作
        Deployment deploy = repositoryService.createDeployment()
                // 部署名称
                .name("测试申请流程定义")
                // 部署资源文件
                .addClasspathResource("processes/test.bpmn20.xml")
                // 部署多个资源
                // .addClasspathResource("processes/test1.bpmn20.xml")
                .deploy();

        log.info("流程部署id:{}", deploy.getId());
        log.info("流程部署名称:{}", deploy.getName());
    }

    @Test
    @DisplayName("启动流程")
    void startProcessInstance() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 获取 RuntimeService，启动流程、查询流程实例、设置和获取流程实例变量
        RuntimeService runtimeService = processEngine.getRuntimeService();
        // 获取流程定义的 id，上一步部署模型之后生成的流程定义 ACT_RE_PROCDEF 表中的 id
        String processDefinitionId = "test:1:8fd4433b-8f90-11ef-b0ab-921ccc308835";
        // 根据流程定义的 id 启动流程实例
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinitionId);
        log.info("流程实例 id: {}", processInstance.getId());
        log.info("流程实例名称: {}", processInstance.getName());
        log.info("活动（任务）id: {}", processInstance.getActivityId());

        // ProcessInstance 对应 数据库表为 act_ru_execution
    }

    @Test
    @DisplayName("处理任务")
    void completeTask() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 获取TaskService，运行时任务查询、领取、完成、删除以及变量设置
        TaskService taskService = processEngine.getTaskService();
        // 查询任务
        Task task = taskService.createTaskQuery()
                // 根据任务 id 获取
                .taskId("91fadd75-9013-11ef-aab0-ba093d391d56")
                .singleResult();
        // 完成任务
        taskService.complete(task.getId());
    }
}
