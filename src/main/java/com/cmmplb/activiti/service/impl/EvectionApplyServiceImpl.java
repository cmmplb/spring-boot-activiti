package com.cmmplb.activiti.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmmplb.activiti.constant.KeyConstant;
import com.cmmplb.activiti.dao.EvectionApplyMapper;
import com.cmmplb.activiti.dto.EvectionApplyDTO;
import com.cmmplb.activiti.entity.Apply;
import com.cmmplb.activiti.entity.EvectionApply;
import com.cmmplb.activiti.service.ApplyService;
import com.cmmplb.activiti.service.EvectionApplyService;
import com.cmmplb.activiti.util.SecurityUtil;
import com.cmmplb.activiti.vo.EvectionApplyDetailsVO;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;

/**
 * @author penglibo
 * @date 2023-11-27 09:17:52
 * @since jdk 1.8
 */

@Slf4j
@Service
@Transactional
public class EvectionApplyServiceImpl extends ServiceImpl<EvectionApplyMapper, EvectionApply> implements EvectionApplyService {

    @Autowired
    private TaskService taskService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ApplyService applyService;

    @Override
    public boolean save(EvectionApplyDTO dto) {
        EvectionApply evectionApply = new EvectionApply();
        // 这里不整合认证授权，从前端切换用户用来快速测试
        evectionApply.setUserId(SecurityUtil.getUserId());
        evectionApply.setStatus((byte) 0);
        evectionApply.setReason(dto.getReason());
        evectionApply.setStartTime(dto.getStartTime());
        evectionApply.setEndTime(dto.getEndTime());
        evectionApply.setCreateTime(new Date());
        evectionApply.setUpdateTime(new Date());
        baseMapper.insert(evectionApply);

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(KeyConstant.EVECTION_APPLY_PROCESS_DEFINITION_KEY).singleResult();
        if (null == processDefinition) {
            throw new RuntimeException("流程定义信息不存在");
        }
        if (processDefinition.isSuspended()) {
            throw new RuntimeException("流程已挂起，暂时无法申请");
        }
        // 添加事项申请信息
        Apply apply = new Apply();
        apply.setTitle(dto.getTitle());
        apply.setUserId(SecurityUtil.getUserId());
        apply.setType((byte) 2);
        apply.setBusinessId(evectionApply.getId());
        apply.setStatus((byte) 0);
        apply.setApplyTime(dto.getApplyTime());
        apply.setDefKey(processDefinition.getKey());
        apply.setCreateTime(new Date());
        apply.setUpdateTime(new Date());
        applyService.save(apply);

        // 设置流程启动人
        Authentication.setAuthenticatedUserId(SecurityUtil.getUserId().toString());

        // 发起请假流程
        HashMap<String, Object> variables = new HashMap<>();
        // 这里直接指定流程的负责人，其他情况也可以在完成任务的时候设置或者转让负责人
        // 这里存的用户id，第一个申请的任务由本人完成
        variables.put("assignee0", SecurityUtil.getUserId().toString());
        variables.put("assignee1", 3);
        variables.put("assignee2", 1);

        // 定义businessKey一般为流程实例key与实际业务数据的结合
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
        return true;
    }

    @Override
    public EvectionApplyDetailsVO getDetailsById(Long id) {
        return baseMapper.selectDetailsById(id);
    }
}
