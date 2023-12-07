package com.cmmplb.activiti.service.impl;

import com.cmmplb.activiti.beans.PageResult;
import com.cmmplb.activiti.constant.KeyConstant;
import com.cmmplb.activiti.dto.HandleTaskDTO;
import com.cmmplb.activiti.dto.TaskQueryDTO;
import com.cmmplb.activiti.entity.Apply;
import com.cmmplb.activiti.entity.EvectionApply;
import com.cmmplb.activiti.entity.LeaveApply;
import com.cmmplb.activiti.entity.User;
import com.cmmplb.activiti.service.*;
import com.cmmplb.activiti.util.ListUtil;
import com.cmmplb.activiti.vo.CompletedTaskVO;
import com.cmmplb.activiti.vo.IncompleteTaskVO;
import com.cmmplb.activiti.vo.UserInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author penglibo
 * @date 2023-11-16 10:42:11
 * @since jdk 1.8
 */

@Slf4j
@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private UserService userService;

    @Autowired
    private ApplyService applyService;

    @Autowired
    private LeaveApplyService leaveApplyService;

    @Autowired
    private EvectionApplyService evectionApplyService;

    @Autowired
    private org.activiti.engine.TaskService taskService;

    @Override
    public PageResult<IncompleteTaskVO> getByPaged(TaskQueryDTO dto) {
        // 这里根据业务拼接条件
        TaskQuery taskQuery = taskService.createTaskQuery();
        if (null != dto.getUserId()) {
            taskQuery.taskAssignee(dto.getUserId().toString());
        }
        if (null != dto.getType()) {
            // 类型:1-请假;2-出差;3...
            if (dto.getType().equals((byte) 1)) {
                taskQuery.processDefinitionKey(KeyConstant.LEAVE_APPLY_PROCESS_DEFINITION_KEY);
            } else if (dto.getType().equals((byte) 2)) {
                taskQuery.processDefinitionKey(KeyConstant.EVECTION_APPLY_PROCESS_DEFINITION_KEY);
            }
        }
        long count = taskQuery.count();
        if (count == 0) {
            return new PageResult<>(count, new ArrayList<>());
        }
        // 获取任务列表
        List<Task> tasks = taskQuery.orderByTaskCreateTime().asc().listPage(dto.getStart(), dto.getSize());
        Set<String> processInstanceIds = new HashSet<>();
        Set<String> userIds = new HashSet<>();
        for (Task task : tasks) {
            processInstanceIds.add(task.getProcessInstanceId());
            userIds.add(task.getAssignee());
        }
        List<ProcessInstance> processInstanceList = runtimeService.createProcessInstanceQuery().processInstanceIds(processInstanceIds).list();
        processInstanceList.stream().map(ProcessInstance::getStartUserId).forEach(userIds::add);
        // 这里把办理人id转换成名称
        Map<Long, User> userMap = null;
        if (!CollectionUtils.isEmpty(userIds)) {
            List<User> userList = userService.getListByIds(userIds);
            userMap = userList.stream().collect(Collectors.toMap(User::getId, Function.identity()));
        }
        IncompleteTaskVO vo;
        List<IncompleteTaskVO> res = new ArrayList<>();
        for (Task task : tasks) {
            for (ProcessInstance processInstance : processInstanceList) {
                if (task.getProcessInstanceId().equals(processInstance.getId())) {
                    vo = new IncompleteTaskVO();
                    vo.setId(task.getId());
                    String id = "";
                    if (task.getBusinessKey().contains(KeyConstant.LEAVE_APPLY_PROCESS_DEFINITION_KEY)) {
                        // 请假
                        id = task.getBusinessKey().replace(KeyConstant.LEAVE_APPLY_PROCESS_DEFINITION_KEY + ":", "");
                    } else if (task.getBusinessKey().contains(KeyConstant.EVECTION_APPLY_PROCESS_DEFINITION_KEY)) {
                        // 出差
                        id = task.getBusinessKey().replace(KeyConstant.EVECTION_APPLY_PROCESS_DEFINITION_KEY + ":", "");
                    }
                    vo.setApplyId(Long.parseLong(id));
                    vo.setTaskName(task.getName());
                    vo.setProcessInstanceId(task.getProcessInstanceId());
                    vo.setExecutionId(task.getExecutionId());
                    vo.setProcessDefinitionId(task.getProcessDefinitionId());
                    vo.setCreateTime(task.getCreateTime());
                    vo.setAssigneeId(task.getAssignee());
                    vo.setBusinessKey(processInstance.getBusinessKey());
                    // 类型多的话要抽取
                    vo.setType(processInstance.getBusinessKey().contains(KeyConstant.LEAVE_APPLY_PROCESS_DEFINITION_KEY) ? (byte) 1 : (byte) 2);
                    vo.setProcessDefinitionName(processInstance.getProcessDefinitionName());
                    vo.setStartUserId(processInstance.getStartUserId());
                    if (!CollectionUtils.isEmpty(userMap)) {
                        // 负责人姓名
                        User user = userMap.get(Long.parseLong(task.getAssignee()));
                        if (null != user) {
                            vo.setAssigneeName(user.getName());
                        }
                        // 流程启动人姓名
                        user = userMap.get(Long.parseLong(processInstance.getStartUserId()));
                        if (null != user) {
                            vo.setStartUserName(user.getName());
                        }
                    }
                    vo.setStartTime(processInstance.getStartTime());
                    res.add(vo);
                    break;
                }
            }
        }
        return new PageResult<>(count, res);
    }

    @SuppressWarnings("deprecation")
    @Override
    public PageResult<CompletedTaskVO> getCompletedByPaged(TaskQueryDTO dto) {
        // 获取历史流程实例
        HistoricProcessInstanceQuery processInstanceQuery = historyService.createHistoricProcessInstanceQuery();
        if (null != dto.getType()) {
            // 类型:1-请假;2-出差;3...
            if (dto.getType().equals((byte) 1)) {
                processInstanceQuery.processDefinitionKey(KeyConstant.LEAVE_APPLY_PROCESS_DEFINITION_KEY);
            } else if (dto.getType().equals((byte) 2)) {
                processInstanceQuery.processDefinitionKey(KeyConstant.EVECTION_APPLY_PROCESS_DEFINITION_KEY);
            }
        }
        long count = processInstanceQuery.count();
        if (count == 0) {
            return new PageResult<>(count, new ArrayList<>());
        }
        List<UserInfoVO> userList = userService.getList();
        Map<Long, UserInfoVO> userMap = userList.stream().collect(Collectors.toMap(UserInfoVO::getId, Function.identity()));
        List<CompletedTaskVO> res = new ArrayList<>();
        CompletedTaskVO vo;
        List<Comment> commentList = new ArrayList<>();
        List<HistoricProcessInstance> processList = processInstanceQuery.list();
        for (HistoricProcessInstance processInstance : processList) {
            HistoricActivityInstanceQuery activityInstanceQuery = historyService.createHistoricActivityInstanceQuery()
                    .processInstanceId(processInstance.getId()).activityType("userTask");
            // 根据流程实例获取审批意见（流程已经结束的审批意见）
            List<Comment> commentDbList = taskService.getProcessInstanceComments(processInstance.getId());
            commentList.addAll(commentDbList);
            Map<String, String> taskIdCommentMap = commentList.stream().collect(Collectors.toMap(Comment::getTaskId, Comment::getFullMessage));
            if (null != dto.getUserId()) {
                activityInstanceQuery.taskAssignee(dto.getUserId().toString());
            }
            // 根据历史流程实例id获取已办理的节点信息
            List<HistoricActivityInstance> list = activityInstanceQuery.list();
            if (!CollectionUtils.isEmpty(list)) {
                for (HistoricActivityInstance instance : list) {
                    vo = new CompletedTaskVO();
                    vo.setInstanceId(instance.getId());
                    String id = "";
                    if (processInstance.getBusinessKey().contains(KeyConstant.LEAVE_APPLY_PROCESS_DEFINITION_KEY)) {
                        // 请假
                        id = processInstance.getBusinessKey().replace(KeyConstant.LEAVE_APPLY_PROCESS_DEFINITION_KEY + ":", "");
                    } else if (processInstance.getBusinessKey().contains(KeyConstant.EVECTION_APPLY_PROCESS_DEFINITION_KEY)) {
                        // 出差
                        id = processInstance.getBusinessKey().replace(KeyConstant.EVECTION_APPLY_PROCESS_DEFINITION_KEY + ":", "");
                    }
                    vo.setApplyId(Long.parseLong(id));
                    vo.setBusinessKey(processInstance.getBusinessKey());
                    // 类型多的话要抽取
                    vo.setType(processInstance.getBusinessKey().contains(KeyConstant.LEAVE_APPLY_PROCESS_DEFINITION_KEY) ? (byte) 1 : (byte) 2);
                    vo.setProcessDefinitionName(processInstance.getProcessDefinitionName());
                    vo.setStartTime(instance.getStartTime());
                    vo.setStartUserId(processInstance.getStartUserId());
                    vo.setStartUserName(userMap.get(Long.parseLong(processInstance.getStartUserId())).getName());
                    vo.setTaskName(instance.getActivityName());
                    vo.setAssigneeName(userMap.get(Long.parseLong(instance.getAssignee())).getName());
                    vo.setComment(taskIdCommentMap.get(instance.getTaskId()));
                    res.add(vo);
                }
            }
        }
        count = ListUtil.getTotalPage(res, dto.getSize());
        return new PageResult<>(count, ListUtil.startPage(res, dto.getCurrent(), dto.getSize()));
    }

    @Override
    public boolean handleTask(HandleTaskDTO dto) {
        Task task = taskService.createTaskQuery().taskId(dto.getId()).singleResult();

        // 如果当前任务没有指派人，需要先使用 claim() 方法领取任务
        // taskService.claim(id, "李四");

        // 如果设置为null，归还组任务,该 任务没有负责人
        // taskService.setAssignee(taskId, null);

        // 将此任务交给其它候选人办理该 任务
        // taskService.setAssignee(taskId, "李四");

        // 添加评论
        try {
            taskService.addComment(dto.getId(), task.getProcessInstanceId(), dto.getComment());
        } catch (ActivitiException e) {
            if (e.getMessage().contains("Cannot add a comment to a suspended task")) {
                throw new RuntimeException("流程已挂起，不可操作。");
            }
            throw new RuntimeException(e.getMessage());
        }
        // 查看评论表act_hi_comment,查看act_ru_task表，可以看到任务流转：
        // 传入通过和驳回的状态参数
        HashMap<String, Object> variables = new HashMap<>();
        variables.put("status", dto.getStatus());
        // 如果有指派人，直接完成任务
        taskService.complete(dto.getId(), variables);
        // 流程处理
        processEnded(dto.getStatus(), task.getBusinessKey(), task.getProcessInstanceId());
        return true;
    }

    @Override
    public boolean entrustTask(String taskId, String userId) {
        // 将此任务交给其它候选人办理该任务
        taskService.setAssignee(taskId, userId);
        return true;
    }

    /**
     * 流程结束处理
     */
    private void processEnded(Byte status, String businessKey, String processInstanceId) {
        // 这个可以用来判断流程是否结束
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (null == processInstance) {
            log.info("processInstance流程结束");
            String id = "";
            if (businessKey.contains(KeyConstant.LEAVE_APPLY_PROCESS_DEFINITION_KEY)) {
                // 请假
                id = businessKey.replace(KeyConstant.LEAVE_APPLY_PROCESS_DEFINITION_KEY + ":", "");
            } else if (businessKey.contains(KeyConstant.EVECTION_APPLY_PROCESS_DEFINITION_KEY)) {
                // 出差
                id = businessKey.replace(KeyConstant.EVECTION_APPLY_PROCESS_DEFINITION_KEY + ":", "");
            }
            // 更新申请信息
            Apply apply = applyService.getById(Long.parseLong(id));
            if (null == apply) {
                throw new RuntimeException("申请信息不存在");
            }
            Apply applyUp = new Apply();
            applyUp.setId(apply.getId());
            applyUp.setStatus(status);
            applyService.updateById(applyUp);

            // 更新业务表状态
            if (apply.getType().equals((byte) 1)) {
                // 请假
                upLeaveStatus(apply.getBusinessId(), status);
            } else if (apply.getType().equals((byte) 2)) {
                // 出差
                upEvectionStatus(apply.getBusinessId(), status);
            }
            // more
        }
        // 完成最后一个任务的审批后，会更新历史流程实例的完成时间act_hi_procinst，DURATION_字段
        // ru相关表数据都会清空，流程结束
    }

    private void upEvectionStatus(Long evectionId, Byte status) {
        EvectionApply evectionApply = evectionApplyService.getById(evectionId);
        if (null == evectionApply) {
            throw new RuntimeException("出差申请信息不存在");
        }
        EvectionApply leaveApplyUp = new EvectionApply();
        leaveApplyUp.setId(evectionApply.getId());
        leaveApplyUp.setStatus(status);
        evectionApplyService.updateById(leaveApplyUp);
    }

    private void upLeaveStatus(Long leaveId, Byte status) {
        LeaveApply leaveApply = leaveApplyService.getById(leaveId);
        if (null == leaveApply) {
            throw new RuntimeException("请假申请信息不存在");
        }
        LeaveApply leaveApplyUp = new LeaveApply();
        leaveApplyUp.setId(leaveApply.getId());
        leaveApplyUp.setStatus(status);
        leaveApplyService.updateById(leaveApplyUp);
    }
}
