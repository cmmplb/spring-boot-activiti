package com.cmmplb.activiti;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

/**
 * @author penglibo
 * @date 2021-03-29 14:10:17
 */

@Slf4j
@SpringBootTest
public class ActivitiTest {

    /**
     * 部署流程 --RepositoryService
     */
    @Test
    public void deploy() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deploy = repositoryService.createDeployment()
                .name("请假申请流程定义")
                .addClasspathResource("processes/leave-apply.bpmn20.xml")
                .deploy();
        log.info("流程部署id:{}", deploy.getId());
        log.info("流程部署名称:{}", deploy.getName());
        // 查看数据库表 act_re_procdef
    }

    /**
     * 启动流程
     */
    @Test
    void startProcessInstance() {
        // 启动流程时传递的参数列表 这里根据实际情况 也可以选择不传
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("assignee0", "张三");
        map.put("assignee1", "王经理");

        // 根据流程定义ID查询流程定义  leave:1:10004是我们刚才部署的流程定义的id
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId("test:1:4074e2cf-5cfc-11ee-8577-7667b3d4735a")
                .singleResult();

        // 获取流程定义的Key
        String processDefinitionKey = processDefinition.getKey();
        log.info("processDefinitionKey:{}", processDefinitionKey);

        // 定义businessKey  businessKey一般为流程实例key与实际业务数据的结合
        // 假设一个请假的业务 在数据库中的id是1001
        String businessKey = processDefinitionKey + ":" + "1001";
        // 设置启动流程的人
        Authentication.setAuthenticatedUserId("cmmplb");
        RuntimeService runtimeService = processEngine.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey, map);
        System.out.println("流程启动成功：" + processInstance);
        System.out.println("流程定义id:" + processInstance.getProcessDefinitionId());
        System.out.println("流程实例 id:" + processInstance.getId());
        System.out.println("当前活动的id:" + processInstance.getActivityId());

        System.out.println("================================================");
        // 启动 key 标识的流程定义，并指定 流程定义中的两个参数：assignee0和assignee1
        processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, map);
        log.info("流程实例的内容：{}", processInstance.getProcessDefinitionName());
        System.out.println("流程启动成功：" + processInstance);
        System.out.println("流程定义id:" + processInstance.getProcessDefinitionId());
        System.out.println("流程实例 id:" + processInstance.getId());
        System.out.println("当前活动的id:" + processInstance.getActivityId());

        // 查看数据库表 act_ru_task
    }
}
