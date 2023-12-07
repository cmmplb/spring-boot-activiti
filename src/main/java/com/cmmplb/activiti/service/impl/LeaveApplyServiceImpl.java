package com.cmmplb.activiti.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmmplb.activiti.constant.KeyConstant;
import com.cmmplb.activiti.dao.LeaveApplyMapper;
import com.cmmplb.activiti.dto.LeaveApplyDTO;
import com.cmmplb.activiti.entity.Apply;
import com.cmmplb.activiti.entity.LeaveApply;
import com.cmmplb.activiti.entity.LeaveApplyDate;
import com.cmmplb.activiti.service.ApplyService;
import com.cmmplb.activiti.service.LeaveApplyDateService;
import com.cmmplb.activiti.service.LeaveApplyService;
import com.cmmplb.activiti.util.SecurityUtil;
import com.cmmplb.activiti.vo.LeaveApplyDetailsVO;
import com.cmmplb.activiti.vo.LeaveApplyVO;
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
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author penglibo
 * @date 2023-11-15 11:04:20
 * @since jdk 1.8
 */

@Slf4j
@Service
@Transactional
public class LeaveApplyServiceImpl extends ServiceImpl<LeaveApplyMapper, LeaveApply> implements LeaveApplyService {

    @Autowired
    private TaskService taskService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ApplyService applyService;

    @Autowired
    private LeaveApplyDateService leaveApplyDateService;

    @Override
    public boolean save(LeaveApplyDTO dto) {
        LeaveApply leaveApply = new LeaveApply();
        // 这里不整合认证授权，从前端切换用户用来快速测试
        leaveApply.setUserId(SecurityUtil.getUserId());
        leaveApply.setStatus((byte) 0);
        leaveApply.setReason(dto.getReason());
        leaveApply.setCreateTime(new Date());
        leaveApply.setUpdateTime(new Date());
        baseMapper.insert(leaveApply);

        // 添加关联的日期信息
        if (!CollectionUtils.isEmpty(dto.getLeaveDateList())) {
            LeaveApplyDate leaveApplyDate;
            List<LeaveApplyDate> list = new ArrayList<>();
            for (LeaveApplyDTO.LeaveDate leaveDate : dto.getLeaveDateList()) {
                leaveApplyDate = new LeaveApplyDate();
                leaveApplyDate.setLeaveApplyId(leaveApply.getId());
                leaveApplyDate.setType(leaveDate.getType());
                leaveApplyDate.setStartTime(leaveDate.getStartTime());
                leaveApplyDate.setEndTime(leaveDate.getEndTime());
                leaveApplyDate.setCreateTime(new Date());
                leaveApplyDate.setUpdateTime(new Date());
                list.add(leaveApplyDate);
            }
            leaveApplyDateService.saveBatch(list);
        }

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(KeyConstant.LEAVE_APPLY_PROCESS_DEFINITION_KEY).singleResult();
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
        apply.setType((byte) 1);
        apply.setBusinessId(leaveApply.getId());
        apply.setStatus((byte) 0);
        apply.setDefKey(processDefinition.getKey());
        apply.setApplyTime(dto.getApplyTime());
        apply.setCreateTime(new Date());
        apply.setUpdateTime(new Date());
        applyService.save(apply);

        // 设置流程启动人
        Authentication.setAuthenticatedUserId(SecurityUtil.getUserId().toString());

        // 发起请假流程
        HashMap<String, Object> variables = new HashMap<>();
        // 这里直接指定流程的负责人，其他情况也可以在完成任务的时候设置或者转让负责人
        // 这里存的用户id，第一个申请的任务由本人完成
        variables.put("assignee0", 2);
        variables.put("assignee1", 3);
        variables.put("assignee2", 1);

        // 定义businessKey  businessKey一般为流程实例key与实际业务数据的结合
        String businessKey = processDefinition.getKey() + ":" + apply.getId();
        // 启动流程
        runtimeService.startProcessInstanceByKey(processDefinition.getKey(), businessKey, variables);
        // 查询节点任务
        Task task = taskService.createTaskQuery()
                .processDefinitionKey(processDefinition.getKey())
                .processInstanceBusinessKey(businessKey)
                .singleResult();
        taskService.complete(task.getId());
        // 第一个申请的任务由本人完成
        return true;
    }

    @Override
    public LeaveApplyDetailsVO getDetailsById(Long id) {
        LeaveApplyVO leaveApply = baseMapper.selectDetailsById(id);
        if (null == leaveApply) {
            throw new RuntimeException("请假信息已删除");
        }
        LeaveApplyDetailsVO vo = new LeaveApplyDetailsVO();
        vo.setUserName(leaveApply.getUserName());
        vo.setReason(leaveApply.getReason());
        List<LeaveApplyDate> list = leaveApplyDateService.list(new LambdaQueryWrapper<LeaveApplyDate>().eq(LeaveApplyDate::getLeaveApplyId, id));
        if (!CollectionUtils.isEmpty(list)) {
            List<LeaveApplyDetailsVO.LeaveDate> leaveDateList = new ArrayList<>();
            LeaveApplyDetailsVO.LeaveDate date;
            for (LeaveApplyDate leaveApplyDate : list) {
                date = new LeaveApplyDetailsVO.LeaveDate();
                date.setType(leaveApplyDate.getType());
                date.setStartTime(leaveApplyDate.getStartTime());
                date.setEndTime(leaveApplyDate.getEndTime());
                leaveDateList.add(date);
            }
            vo.setLeaveDateList(leaveDateList);
        }
        return vo;
    }
}
