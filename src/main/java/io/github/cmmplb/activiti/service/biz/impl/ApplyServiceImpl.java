package io.github.cmmplb.activiti.service.biz.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.cmmplb.activiti.beans.PageResult;
import io.github.cmmplb.activiti.constants.BizConstant;
import io.github.cmmplb.activiti.dao.biz.ApplyMapper;
import io.github.cmmplb.activiti.domain.dto.biz.ApplyDTO;
import io.github.cmmplb.activiti.domain.dto.biz.ApplyQueryDTO;
import io.github.cmmplb.activiti.domain.entity.biz.Apply;
import io.github.cmmplb.activiti.domain.vo.biz.ApplyDetailsVO;
import io.github.cmmplb.activiti.domain.vo.biz.ApplyProgressBpmnJsVO;
import io.github.cmmplb.activiti.domain.vo.biz.ApplyVO;
import io.github.cmmplb.activiti.domain.vo.biz.AttendanceApplyDetailsVO;
import io.github.cmmplb.activiti.factory.ApplyFactory;
import io.github.cmmplb.activiti.handler.exection.BusinessException;
import io.github.cmmplb.activiti.service.biz.ApplyService;
import io.github.cmmplb.activiti.utils.ActivitiUtil;
import io.github.cmmplb.activiti.utils.SecurityUtil;
import io.github.cmmplb.activiti.utils.ServletUtil;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author penglibo
 * @date 2024-11-08 10:48:05
 * @since jdk 1.8
 */
@Service
public class ApplyServiceImpl extends ServiceImpl<ApplyMapper, Apply> implements ApplyService {

    @Autowired
    private TaskService taskService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RepositoryService repositoryService;

    @Override
    public boolean save(ApplyDTO dto) {
        // 这里为了方便掩饰流程, 就在这一个发起申请里面处理不同类型的数据了，实际项目中是分开业务类型添加的数据
        Long businessId;
        // [ 策略模式 + 工厂模式 + 模板方法模式 ] 来处理不同类型, 实际就是 if else 调用不同的 Service 实现类
        // 类型: 1-考勤管理; 2-行政管理; 3-财务管理; 4-人事管理; 5-...
        try {
            String jsonParams = JSON.toJSONString(dto.getAttendanceApplyDTO());
            businessId = ApplyFactory.getInstance(dto.getType()).save(jsonParams);
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
        // 添加事项申请信息
        Apply apply = new Apply();
        apply.setTitle(dto.getTitle());
        apply.setUserId(SecurityUtil.getUser().getId());
        apply.setType(dto.getType());
        apply.setSubtype(dto.getSubtype());
        apply.setBusinessId(businessId);
        apply.setStatus(BizConstant.StatusEnum.IN_PROGRESS.getStatus());
        apply.setDefKey(processDefinition.getKey());
        apply.setCreateTime(new Date());
        apply.setUpdateTime(new Date());
        baseMapper.insert(apply);

        extracted(dto, businessId);
        return true;
    }

    private void extracted(ApplyDTO dto, Long businessId) {
        // 这里查询数据库版本最新的流程定义
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(BizConstant.ProcessEnum.LEAVE_APPLY.getKey())
                // 使用最新版本, 如果后续有指定主版本的功能, 则添加一个版本查询参数
                // .processDefinitionVersion(1)
                .latestVersion()
                .singleResult();
        if (null == processDefinition) {
            throw new BusinessException("流程定义信息不存在, 请先部署流程");
        }
        if (processDefinition.isSuspended()) {
            throw new BusinessException("流程已挂起, 暂时无法申请");
        }

        // 设置流程启动人
        Authentication.setAuthenticatedUserId(SecurityUtil.getUser().getId().toString());

        // 发起请假流程
        HashMap<String, Object> variables = new HashMap<>();
        // 这里的 assignee0 是设计流程时自定义的
        // bpmn-js 在用户任务框中点击用户分配-代理人中的输入框, modeler 是在设计界面下方的代理输入框

        // 这里存的用户 id, 第一个申请的任务由本人完成, 也就是用户申请任务, 如果第一步不是本人, 则需要指定流程负责人
        // assignee x 字段对应流程文件 activiti:assignee="${assignee0}" 或 camunda:assignee
        variables.put("assignee0", SecurityUtil.getUser().getId());
        // 这里直接指定流程的负责人, 其他情况也可以在完成任务的时候设置或者转让负责人, 看后续有空在流程设计时任务节点选择用户审核
        // 指定的用户为数据库存储 id 为 2 和 3 的, 也就是 gg蹦和沸羊羊
        variables.put("assignee1", 2);
        variables.put("assignee2", 3);

        // 定义 businessKey, 一般为流程实例 key 与实际业务数据的结合, 看自己业务需求, 能通过实例 key 追溯到流程
        String businessKey = processDefinition.getKey() + ":" + apply.getId();
        // 启动流程
        runtimeService.startProcessInstanceByKey(processDefinition.getKey(), businessKey, variables);
        // 查询节点任务
        Task task = taskService.createTaskQuery()
                .processDefinitionKey(processDefinition.getKey())
                .processInstanceBusinessKey(businessKey)
                .singleResult();
        // 第一个申请的任务由本人完成
        taskService.complete(task.getId());
    }

    @Override
    public PageResult<ApplyVO> getByPaged(ApplyQueryDTO dto) {
        Page<ApplyVO> page = baseMapper.selectByPaged(new Page<>(dto.getCurrent(), dto.getSize()), dto);
        return new PageResult<>(page.getTotal(), page.getRecords());
    }

    @Override
    public ApplyDetailsVO getApplyDetailsById(Long id) {
        ApplyDetailsVO vo = baseMapper.selectDetailsById(id);
        // 类型: 1-考勤管理; 2-行政管理; 3-财务管理; 4-人事管理; 5-...
        // 获取考勤详情
        String json = ApplyFactory.getInstance(vo.getType()).getDetailsById(vo.getBusinessId());
        vo.setAttendanceApplyDetails(JSON.parseObject(json, AttendanceApplyDetailsVO.class));
        return vo;
    }

    @Override
    public void showProgressChart(Long id) {
        Apply apply = baseMapper.selectById(id);
        if (null == apply) {
            log.error("申请信息已删除");
            return;
        }
        try {
            Result result = getResult(apply);
            if (result == null) {
                return;
            }

            // ========activiti 7 移除了 processEngineConfiguration.getProcessDiagramGenerator() 的方法========
            // ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
            // ProcessEngineConfigurationImpl processEngineConfiguration = (ProcessEngineConfigurationImpl) defaultProcessEngine.getProcessEngineConfiguration();
            // ProcessDiagramGenerator processDiagramGenerator = processEngineConfiguration.getProcessDiagramGenerator();
            // processDiagramGenerator.generateDiagram(bpmnModel, "宋体", "宋体", "宋体");

            // ========改为了直接 new 该接口的实例========
            ProcessDiagramGenerator diagramGenerator = new DefaultProcessDiagramGenerator();
            InputStream is = diagramGenerator.generateDiagram(result.bpmnModel,
                    result.highLightedActivitiIds,
                    result.highLightedFlowIds,
                    "宋体",
                    "宋体",
                    "宋体"
            );
            HttpServletResponse response = ServletUtil.getResponse();

            // 响应svg到客户端
            // response.setContentType("image/svg+xml");
            // IOUtils.copy(is, response.getOutputStream());

            // 转换svg为png响应
            response.setContentType("image/png");
            new PNGTranscoder().transcode(new TranscoderInput(is), new TranscoderOutput(response.getOutputStream()));
        } catch (Exception e) {
            log.error("error", e);
        }
    }

    private Result getResult(Apply apply) {
        // 定义 businessKey, 一般为流程实例 key 与实际业务数据的结合, 看自己业务需求, 能通过实例 key 追溯到流程
        String businessKey = apply.getDefKey() + ":" + apply.getId();
        // 如果流程结束(驳回)，当前流程实例为空
        ProcessInstance process = runtimeService.createProcessInstanceQuery()
                .processDefinitionKey(apply.getDefKey())
                .processInstanceBusinessKey(businessKey)
                .singleResult();

        // 获取历史流程实例来查询流程进度
        HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceBusinessKey(businessKey).singleResult();

        // 根据实例 id 获取也行
        // processInstance = historyService.createHistoricProcessInstanceQuery()
        //         .processInstanceId(process.getId()).singleResult();

        if (null == processInstance) {
            log.error("流程信息不存在");
            return null;
        }
        // 获取流程中已经执行的节点，按照执行先后顺序排序
        List<HistoricActivityInstance> historicActivityInstances = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(null == process ? processInstance.getId() : process.getId())
                .orderByHistoricActivityInstanceStartTime().asc().list();

        BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());

        // highLightedActivities（需要高亮的执行流程节点集合的获取）以及
        // highLightedFlows（需要高亮流程连接线集合的获取）
        // 高亮流程已发生流转的线id集合
        List<String> highLightedFlowIds = ActivitiUtil.getHighLightedFlows(bpmnModel, historicActivityInstances);

        // 高亮已经执行流程节点ID集合
        List<String> highLightedActivitiIds = historicActivityInstances.stream().map(HistoricActivityInstance::getActivityId).collect(Collectors.toList());
        return new Result(bpmnModel, highLightedFlowIds, highLightedActivitiIds);
    }

    @Override
    public ApplyProgressBpmnJsVO showProgressChartBpmnJs(Long id) {
        Apply apply = baseMapper.selectById(id);
        if (null == apply) {
            throw new RuntimeException("申请信息已删除");
        }
        ApplyProgressBpmnJsVO vo = new ApplyProgressBpmnJsVO();
        try {
            Result result = getResult(apply);
            if (result == null) {
                return null;
            }
            byte[] xmlBytes = (new BpmnXMLConverter()).convertToXML(result.bpmnModel, StandardCharsets.UTF_8.name());
            vo.setHighLightedFlowIds(result.highLightedFlowIds);
            vo.setHighLightedActivitiIds(result.highLightedActivitiIds);
            vo.setXml(new String(xmlBytes, StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("error", e);
        }
        return vo;
    }

    @Override
    public boolean revokeApply(Long id) {
        Apply apply = baseMapper.selectById(id);
        if (null == apply) {
            throw new RuntimeException("申请信息已删除");
        }
        Apply applyUp = new Apply();
        applyUp.setId(id);
        // 流程状态:0-进行中;1-已完成;2-已驳回;3-已撤销;
        applyUp.setStatus(BizConstant.StatusEnum.REVOKED.getStatus());
        // 删除流程信息
        String businessKey = apply.getDefKey() + ":" + apply.getId();
        deleteProcessInstance(apply.getDefKey(), businessKey);
        return baseMapper.updateById(applyUp) > 0;
    }

    @Override
    public boolean deleteById(Long id) {
        Apply apply = baseMapper.selectById(id);
        if (null == apply) {
            throw new RuntimeException("申请信息已删除");
        }
        int i = baseMapper.deleteById(id);
        // 删除关联的业务信息, 类型: 1-考勤管理; 2-行政管理; 3-财务管理; 4-人事管理; 5-...
        ApplyFactory.getInstance(apply.getType()).deleteById(id);
        // 定义businessKey,一般为流程实例key与实际业务数据的结合
        String businessKey = apply.getDefKey() + ":" + apply.getId();
        // 删除流程信息
        deleteProcessInstance(apply.getDefKey(), businessKey);
        // 删除历史数据
        HistoricProcessInstance history = historyService.createHistoricProcessInstanceQuery()
                .processDefinitionKey(apply.getDefKey())
                .processInstanceBusinessKey(businessKey)
                .singleResult();
        if (history != null) {
            historyService.deleteHistoricProcessInstance(history.getId());
        }
        return i > 0;
    }

    private void deleteProcessInstance(String processDefinitionKey, String businessKey) {
        // 删除关联的流程信息
        ProcessInstance process = runtimeService.createProcessInstanceQuery()
                .processDefinitionKey(processDefinitionKey)
                .processInstanceBusinessKey(businessKey)
                .singleResult();
        // 流程实例的当前任务act_ru_task会被删除
        // 流程历史act_hi_taskinst不会被删除，并且流程历史的状态置为finished完成。
        if (process != null) {
            runtimeService.deleteProcessInstance(process.getId(), "删除请假流程信息");
        }
    }

    private static class Result {
        public final BpmnModel bpmnModel;
        public final List<String> highLightedFlowIds;
        public final List<String> highLightedActivitiIds;

        public Result(BpmnModel bpmnModel, List<String> highLightedFlowIds, List<String> highLightedActivitiIds) {
            this.bpmnModel = bpmnModel;
            this.highLightedFlowIds = highLightedFlowIds;
            this.highLightedActivitiIds = highLightedActivitiIds;
        }
    }
}
