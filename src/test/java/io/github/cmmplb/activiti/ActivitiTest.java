package io.github.cmmplb.activiti;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author penglibo
 * @date 2024-10-21 13:53:53
 * @since jdk 1.8
 */

@Slf4j
@SpringBootTest
public class ActivitiTest {

    public static void main(String[] args) {
        System.out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><bpmn2:definitions xmlns:bpmn2=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:activiti=\"http://activiti.org/bpmn\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:camunda=\"http://camunda.org/schema/1.0/bpmn\" xmlns:omgdc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:omgdi=\"http://www.omg.org/spec/DD/20100524/DI\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" expressionLanguage=\"http://www.w3.org/1999/XPath\" targetNamespace=\"http://www.activiti.org/processdef\" typeLanguage=\"http://www.w3.org/2001/XMLSchema\">\n  <bpmn2:process camunda:versionTag=\"2\" id=\"leave-apply\" isExecutable=\"true\" name=\"请假申请\">\n    <bpmn2:documentation>这是一个请假申请</bpmn2:documentation>\n    <bpmn2:startEvent id=\"sid-1a4f1b43-e737-43ff-8636-0de4c0b15065\" name=\"开始\">\n      <bpmn2:outgoing>sid-ddc2b2de-0b9e-421a-9c89-caf15e132a32</bpmn2:outgoing>\n    </bpmn2:startEvent>\n    <bpmn2:userTask camunda:assignee=\"${assignee0}\" id=\"sid-2c4f7c32-40fa-4f51-9340-85af7ee37e16\" name=\"创建申请\">\n      <extensionElements>\n        <modeler:initiator-can-complete xmlns:modeler=\"http://activiti.com/modeler\"><![CDATA[false]]></modeler:initiator-can-complete>\n      </extensionElements>\n    </bpmn2:userTask>\n    <bpmn2:userTask camunda:assignee=\"${assignee1}\" id=\"sid-18647353-7512-450c-aa98-f7de48b1b742\" name=\"部门审批\">\n      <extensionElements>\n        <modeler:initiator-can-complete xmlns:modeler=\"http://activiti.com/modeler\"><![CDATA[false]]></modeler:initiator-can-complete>\n      </extensionElements>\n    </bpmn2:userTask>\n    <bpmn2:userTask camunda:assignee=\"${assignee2}\" id=\"sid-7320689c-c96a-4721-95ff-60ab694411cc\" name=\"领导审批\">\n      <extensionElements>\n        <modeler:initiator-can-complete xmlns:modeler=\"http://activiti.com/modeler\"><![CDATA[false]]></modeler:initiator-can-complete>\n      </extensionElements>\n    </bpmn2:userTask>\n    <bpmn2:endEvent id=\"sid-1a4f1b43-e737-43ff-8636-0de4c0b15064\" name=\"结束\">\n      <bpmn2:incoming>sid-3F9B3A67-5536-4516-B3A1-9EF83ED5900C</bpmn2:incoming>\n      <bpmn2:incoming>sid-81F01344-0F12-4192-B890-0BF148580F10</bpmn2:incoming>\n      <bpmn2:incoming>sid-F2678D1C-EE2F-4A07-8797-53ADBA45F8C1</bpmn2:incoming>\n    </bpmn2:endEvent>\n    <bpmn2:exclusiveGateway id=\"sid-9D5BEE35-CEF6-423B-B9DA-5A8FD7FAD0B5\">\n      <bpmn2:incoming>sid-4e6f844e-9e88-4548-b9c5-a78ecb1d52de</bpmn2:incoming>\n      <bpmn2:outgoing>sid-3F9B3A67-5536-4516-B3A1-9EF83ED5900C</bpmn2:outgoing>\n      <bpmn2:outgoing>sid-81F01344-0F12-4192-B890-0BF148580F10</bpmn2:outgoing>\n    </bpmn2:exclusiveGateway>\n    <bpmn2:exclusiveGateway id=\"sid-4311EA1C-C3F6-4898-ACB7-3E1470E978DE\">\n      <bpmn2:incoming>sid-c7f52123-24cf-4f6c-bfb7-e79b31174c3e</bpmn2:incoming>\n      <bpmn2:outgoing>sid-87A9DDFA-C806-4B96-8900-85BAE744D1BC</bpmn2:outgoing>\n      <bpmn2:outgoing>sid-F2678D1C-EE2F-4A07-8797-53ADBA45F8C1</bpmn2:outgoing>\n    </bpmn2:exclusiveGateway>\n    <bpmn2:sequenceFlow id=\"sid-43163c70-0e34-44cc-a417-7cb84a726d9c\" sourceRef=\"sid-2c4f7c32-40fa-4f51-9340-85af7ee37e16\" targetRef=\"sid-18647353-7512-450c-aa98-f7de48b1b742\"/>\n    <bpmn2:sequenceFlow id=\"sid-4e6f844e-9e88-4548-b9c5-a78ecb1d52de\" sourceRef=\"sid-7320689c-c96a-4721-95ff-60ab694411cc\" targetRef=\"sid-9D5BEE35-CEF6-423B-B9DA-5A8FD7FAD0B5\"/>\n    <bpmn2:sequenceFlow id=\"sid-c7f52123-24cf-4f6c-bfb7-e79b31174c3e\" sourceRef=\"sid-18647353-7512-450c-aa98-f7de48b1b742\" targetRef=\"sid-4311EA1C-C3F6-4898-ACB7-3E1470E978DE\"/>\n    <bpmn2:sequenceFlow id=\"sid-3F9B3A67-5536-4516-B3A1-9EF83ED5900C\" name=\"驳回\" sourceRef=\"sid-9D5BEE35-CEF6-423B-B9DA-5A8FD7FAD0B5\" targetRef=\"sid-1a4f1b43-e737-43ff-8636-0de4c0b15064\">\n      <conditionExpression xsi:type=\"tFormalExpression\"><![CDATA[${status==2}]]></conditionExpression>\n    </bpmn2:sequenceFlow>\n    <bpmn2:sequenceFlow id=\"sid-87A9DDFA-C806-4B96-8900-85BAE744D1BC\" name=\"同意\" sourceRef=\"sid-4311EA1C-C3F6-4898-ACB7-3E1470E978DE\" targetRef=\"sid-7320689c-c96a-4721-95ff-60ab694411cc\">\n      <conditionExpression xsi:type=\"tFormalExpression\"><![CDATA[${status==1}]]></conditionExpression>\n    </bpmn2:sequenceFlow>\n    <bpmn2:sequenceFlow id=\"sid-ddc2b2de-0b9e-421a-9c89-caf15e132a32\" sourceRef=\"sid-1a4f1b43-e737-43ff-8636-0de4c0b15065\" targetRef=\"sid-2c4f7c32-40fa-4f51-9340-85af7ee37e16\"/>\n    <bpmn2:sequenceFlow id=\"sid-81F01344-0F12-4192-B890-0BF148580F10\" name=\"同意\" sourceRef=\"sid-9D5BEE35-CEF6-423B-B9DA-5A8FD7FAD0B5\" targetRef=\"sid-1a4f1b43-e737-43ff-8636-0de4c0b15064\">\n      <conditionExpression xsi:type=\"tFormalExpression\"><![CDATA[${status==1}]]></conditionExpression>\n    </bpmn2:sequenceFlow>\n    <bpmn2:sequenceFlow id=\"sid-F2678D1C-EE2F-4A07-8797-53ADBA45F8C1\" name=\"驳回\" sourceRef=\"sid-4311EA1C-C3F6-4898-ACB7-3E1470E978DE\" targetRef=\"sid-1a4f1b43-e737-43ff-8636-0de4c0b15064\">\n      <conditionExpression xsi:type=\"tFormalExpression\"><![CDATA[${status==2}]]></conditionExpression>\n    </bpmn2:sequenceFlow>\n  <bpmn2:extensionElements><camunda:properties><camunda:property name=\"author\" value=\"管理员\"/><camunda:property name=\"category\" value=\"考勤管理\"/></camunda:properties></bpmn2:extensionElements></bpmn2:process>\n  <bpmndi:BPMNDiagram id=\"BPMNDiagram_leave-apply\">\n    <bpmndi:BPMNPlane bpmnElement=\"leave-apply\" id=\"BPMNPlane_leave-apply\">\n      <bpmndi:BPMNShape bpmnElement=\"sid-1a4f1b43-e737-43ff-8636-0de4c0b15065\" id=\"BPMNShape_sid-1a4f1b43-e737-43ff-8636-0de4c0b15065\">\n        <omgdc:Bounds height=\"30.0\" width=\"30.0\" x=\"75.0\" y=\"70.0\"/>\n      </bpmndi:BPMNShape>\n      <bpmndi:BPMNShape bpmnElement=\"sid-2c4f7c32-40fa-4f51-9340-85af7ee37e16\" id=\"BPMNShape_sid-2c4f7c32-40fa-4f51-9340-85af7ee37e16\">\n        <omgdc:Bounds height=\"80.0\" width=\"100.0\" x=\"165.0\" y=\"45.0\"/>\n      </bpmndi:BPMNShape>\n      <bpmndi:BPMNShape bpmnElement=\"sid-18647353-7512-450c-aa98-f7de48b1b742\" id=\"BPMNShape_sid-18647353-7512-450c-aa98-f7de48b1b742\">\n        <omgdc:Bounds height=\"80.0\" width=\"100.0\" x=\"315.0\" y=\"45.0\"/>\n      </bpmndi:BPMNShape>\n      <bpmndi:BPMNShape bpmnElement=\"sid-7320689c-c96a-4721-95ff-60ab694411cc\" id=\"BPMNShape_sid-7320689c-c96a-4721-95ff-60ab694411cc\">\n        <omgdc:Bounds height=\"80.0\" width=\"100.0\" x=\"585.0\" y=\"45.0\"/>\n      </bpmndi:BPMNShape>\n      <bpmndi:BPMNShape bpmnElement=\"sid-1a4f1b43-e737-43ff-8636-0de4c0b15064\" id=\"BPMNShape_sid-1a4f1b43-e737-43ff-8636-0de4c0b15064\">\n        <omgdc:Bounds height=\"28.0\" width=\"28.0\" x=\"885.0\" y=\"180.0\"/>\n      </bpmndi:BPMNShape>\n      <bpmndi:BPMNShape bpmnElement=\"sid-9D5BEE35-CEF6-423B-B9DA-5A8FD7FAD0B5\" id=\"BPMNShape_sid-9D5BEE35-CEF6-423B-B9DA-5A8FD7FAD0B5\">\n        <omgdc:Bounds height=\"40.0\" width=\"40.0\" x=\"735.0\" y=\"65.0\"/>\n      </bpmndi:BPMNShape>\n      <bpmndi:BPMNShape bpmnElement=\"sid-4311EA1C-C3F6-4898-ACB7-3E1470E978DE\" id=\"BPMNShape_sid-4311EA1C-C3F6-4898-ACB7-3E1470E978DE\">\n        <omgdc:Bounds height=\"40.0\" width=\"40.0\" x=\"465.0\" y=\"65.0\"/>\n      </bpmndi:BPMNShape>\n      <bpmndi:BPMNEdge bpmnElement=\"sid-43163c70-0e34-44cc-a417-7cb84a726d9c\" id=\"BPMNEdge_sid-43163c70-0e34-44cc-a417-7cb84a726d9c\">\n        <omgdi:waypoint x=\"265.0\" y=\"85.0\"/>\n        <omgdi:waypoint x=\"315.0\" y=\"85.0\"/>\n      </bpmndi:BPMNEdge>\n      <bpmndi:BPMNEdge bpmnElement=\"sid-87A9DDFA-C806-4B96-8900-85BAE744D1BC\" id=\"BPMNEdge_sid-87A9DDFA-C806-4B96-8900-85BAE744D1BC\">\n        <omgdi:waypoint x=\"505.0\" y=\"85.0\"/>\n        <omgdi:waypoint x=\"585.0\" y=\"85.0\"/>\n      </bpmndi:BPMNEdge>\n      <bpmndi:BPMNEdge bpmnElement=\"sid-3F9B3A67-5536-4516-B3A1-9EF83ED5900C\" id=\"BPMNEdge_sid-3F9B3A67-5536-4516-B3A1-9EF83ED5900C\">\n        <omgdi:waypoint x=\"755.3603603603603\" y=\"104.63963963963964\"/>\n        <omgdi:waypoint x=\"757.0\" y=\"194.0\"/>\n        <omgdi:waypoint x=\"885.0\" y=\"194.0\"/>\n      </bpmndi:BPMNEdge>\n      <bpmndi:BPMNEdge bpmnElement=\"sid-81F01344-0F12-4192-B890-0BF148580F10\" id=\"BPMNEdge_sid-81F01344-0F12-4192-B890-0BF148580F10\">\n        <omgdi:waypoint x=\"774.8620689655172\" y=\"84.86206896551724\"/>\n        <omgdi:waypoint x=\"899.0\" y=\"84.0\"/>\n        <omgdi:waypoint x=\"899.0\" y=\"180.0\"/>\n      </bpmndi:BPMNEdge>\n      <bpmndi:BPMNEdge bpmnElement=\"sid-4e6f844e-9e88-4548-b9c5-a78ecb1d52de\" id=\"BPMNEdge_sid-4e6f844e-9e88-4548-b9c5-a78ecb1d52de\">\n        <omgdi:waypoint x=\"685.0\" y=\"85.0\"/>\n        <omgdi:waypoint x=\"735.0\" y=\"85.0\"/>\n      </bpmndi:BPMNEdge>\n      <bpmndi:BPMNEdge bpmnElement=\"sid-F2678D1C-EE2F-4A07-8797-53ADBA45F8C1\" id=\"BPMNEdge_sid-F2678D1C-EE2F-4A07-8797-53ADBA45F8C1\">\n        <omgdi:waypoint x=\"485.0\" y=\"105.0\"/>\n        <omgdi:waypoint x=\"485.0\" y=\"194.0\"/>\n        <omgdi:waypoint x=\"885.0\" y=\"194.0\"/>\n      </bpmndi:BPMNEdge>\n      <bpmndi:BPMNEdge bpmnElement=\"sid-c7f52123-24cf-4f6c-bfb7-e79b31174c3e\" id=\"BPMNEdge_sid-c7f52123-24cf-4f6c-bfb7-e79b31174c3e\">\n        <omgdi:waypoint x=\"415.0\" y=\"85.0\"/>\n        <omgdi:waypoint x=\"465.0\" y=\"85.0\"/>\n      </bpmndi:BPMNEdge>\n      <bpmndi:BPMNEdge bpmnElement=\"sid-ddc2b2de-0b9e-421a-9c89-caf15e132a32\" id=\"BPMNEdge_sid-ddc2b2de-0b9e-421a-9c89-caf15e132a32\">\n        <omgdi:waypoint x=\"105.0\" y=\"85.0\"/>\n        <omgdi:waypoint x=\"165.0\" y=\"85.0\"/>\n      </bpmndi:BPMNEdge>\n    </bpmndi:BPMNPlane>\n  </bpmndi:BPMNDiagram>\n</bpmn2:definitions>");
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
