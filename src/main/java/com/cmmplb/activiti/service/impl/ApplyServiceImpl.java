package com.cmmplb.activiti.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmmplb.activiti.beans.PageResult;
import com.cmmplb.activiti.dao.ApplyMapper;
import com.cmmplb.activiti.dto.ApplyDTO;
import com.cmmplb.activiti.dto.ApplyStatisticsTimeDTO;
import com.cmmplb.activiti.entity.Apply;
import com.cmmplb.activiti.entity.EvectionApply;
import com.cmmplb.activiti.entity.LeaveApply;
import com.cmmplb.activiti.entity.LeaveApplyDate;
import com.cmmplb.activiti.image.ProcessDiagramCanvas;
import com.cmmplb.activiti.image.ProcessDiagramGeneratorImpl;
import com.cmmplb.activiti.service.ApplyService;
import com.cmmplb.activiti.service.EvectionApplyService;
import com.cmmplb.activiti.service.LeaveApplyDateService;
import com.cmmplb.activiti.service.LeaveApplyService;
import com.cmmplb.activiti.util.ActivitiUtil;
import com.cmmplb.activiti.util.ServletUtil;
import com.cmmplb.activiti.vo.ApplyDetailsVO;
import com.cmmplb.activiti.vo.ApplyVO;
import com.cmmplb.activiti.vo.EvectionApplyDetailsVO;
import com.cmmplb.activiti.vo.LeaveApplyDetailsVO;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author penglibo
 * @date 2023-11-24 09:14:05
 * @since jdk 1.8
 */

@Slf4j
@Service
@Transactional
public class ApplyServiceImpl extends ServiceImpl<ApplyMapper, Apply> implements ApplyService {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private LeaveApplyService leaveApplyService;

    @Autowired
    private LeaveApplyDateService leaveApplyDateService;

    @Autowired
    private EvectionApplyService evectionApplyService;


    @Override
    public PageResult<ApplyVO> getByPaged(ApplyDTO dto) {
        Page<ApplyVO> page = baseMapper.selectByPaged(new Page<>(dto.getCurrent(), dto.getSize()), dto);
        return new PageResult<>(page.getTotal(), page.getRecords());
    }

    @Override
    public boolean cancelApply(Long id) {
        Apply apply = baseMapper.selectById(id);
        if (null == apply) {
            throw new RuntimeException("申请信息已删除");
        }
        Apply applyUp = new Apply();
        applyUp.setId(id);
        // 流程状态:0-进行中;1-已完成;2-已驳回;3-已撤销;
        applyUp.setStatus((byte) 3);
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
        // 类型:1-请假;2-出差;3...
        if (apply.getType().equals((byte) 1)) {
            // 删除关联的请假申请信息
            LeaveApply leaveApply = leaveApplyService.getById(apply.getBusinessId());
            if (null == leaveApply) {
                throw new RuntimeException("请假信息已删除");
            }
            leaveApplyService.removeById(apply.getBusinessId());

            // 删除请假关联的日期信息
            leaveApplyDateService.remove(new LambdaQueryWrapper<LeaveApplyDate>().eq(LeaveApplyDate::getLeaveApplyId, apply.getBusinessId()));
        } else if (apply.getType().equals((byte) 2)) {
            // 删除关联的请假申请信息
            EvectionApply evectionApply = evectionApplyService.getById(apply.getBusinessId());
            if (null == evectionApply) {
                throw new RuntimeException("出差信息已删除");
            }
            evectionApplyService.removeById(apply.getBusinessId());
        }
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

    @Override
    public ApplyDetailsVO getApplyDetailsById(Long id) {
        ApplyDetailsVO vo = baseMapper.selectDetailsById(id);
        // 类型:1-请假;2-出差;3...
        if (vo.getType().equals((byte) 1)) {
            LeaveApplyDetailsVO leaveApply = leaveApplyService.getDetailsById(vo.getBusinessId());
            vo.setLeaveApplyDetails(leaveApply);
        } else if (vo.getType().equals((byte) 2)) {
            EvectionApplyDetailsVO evectionApply = evectionApplyService.getDetailsById(vo.getBusinessId());
            vo.setEvectionApplyDetails(evectionApply);
        }
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
            // 定义businessKey,一般为流程实例key与实际业务数据的结合
            String businessKey = apply.getDefKey() + ":" + apply.getId();
            // 如果流程结束(驳回)，当前流程实例为空
            ProcessInstance process = runtimeService.createProcessInstanceQuery()
                    .processDefinitionKey(apply.getDefKey())
                    .processInstanceBusinessKey(businessKey)
                    .singleResult();

            // 获取历史流程实例来查询流程进度
            HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery().processInstanceBusinessKey(businessKey).singleResult();
            // historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(process.getId()).singleResult();
            if (null == processInstance) {
                log.error("流程信息不存在");
                return;
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

            Set<String> currIds = runtimeService.createExecutionQuery().processInstanceId(processInstance.getId()).list()
                    .stream().map(org.activiti.engine.runtime.Execution::getActivityId).collect(Collectors.toSet());
            // activiti7移除了静态方法创建，需要DefaultProcessDiagramGenerator实例
            // ProcessDiagramGenerator diagramGenerator = new DefaultProcessDiagramGenerator();
            // 由于是创建的新实例，这里的DiagramGenerator就不用注入到配置类里面了，当然ActivitiConfiguration配置类也移除了set的方法。
            ProcessDiagramGeneratorImpl diagramGenerator = new ProcessDiagramGeneratorImpl();
            // 使用默认配置获得流程图表生成器，并生成追踪图片字符流
            InputStream is = diagramGenerator.generateDiagram(bpmnModel,
                    highLightedActivitiIds,
                    highLightedFlowIds,
                    "宋体",
                    "微软雅黑",
                    "黑体",
                    new Color[]{ProcessDiagramCanvas.COLOR_NORMAL, ProcessDiagramCanvas.COLOR_CURRENT},
                    currIds
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

    @Override
    public List<ApplyStatisticsTimeDTO> getStatisticsList(Integer type) {
        return baseMapper.selectStatisticsList(type);
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
}
