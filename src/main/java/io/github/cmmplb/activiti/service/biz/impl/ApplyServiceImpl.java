package io.github.cmmplb.activiti.service.biz.impl;

import com.alibaba.fastjson.JSON;
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
import io.github.cmmplb.activiti.factory.ApplyFactory;
import io.github.cmmplb.activiti.handler.exection.BusinessException;
import io.github.cmmplb.activiti.service.biz.ApplyService;
import io.github.cmmplb.activiti.utils.SecurityUtil;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;

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
    private RepositoryService repositoryService;

    @Override
    public boolean save(ApplyDTO dto) {
        // 这里为了方便掩饰流程, 就在这一个发起申请里面处理不同类型的数据了，实际项目中是分开业务类型添加的数据
        Long businessId;
        // [ 策略模式 + 工厂模式 + 模板方法模式 ] 来处理不同类型, 实际就是 if else 调用不同的 Service 实现类
        // 类型: 1-考勤管理; 2-行政管理; 3-财务管理; 4-人事管理; 5-...
        try {
            String jsonParams = JSON.toJSONString(dto.getLeaveApply());
            businessId = ApplyFactory.getInstance(BizConstant.ApplyTypeEnum.ATTENDANCE.getType()).invoke(jsonParams);
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
        // 这里查询数据库版本最新的流程定义
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(BizConstant.ProcessEnum.LEAVE_APPLY.getKey())
                .orderByProcessDefinitionVersion()
                .desc()
                .singleResult();
        if (null == processDefinition) {
            throw new BusinessException("流程定义信息不存在, 请先部署流程");
        }
        if (processDefinition.isSuspended()) {
            throw new BusinessException("流程已挂起, 暂时无法申请");
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
        taskService.complete(task.getId());
        // 第一个申请的任务由本人完成
        return true;
    }

    @Override
    public PageResult<ApplyVO> getByPaged(ApplyQueryDTO dto) {
        return null;
    }

    @Override
    public ApplyDetailsVO getApplyDetailsById(Long id) {
        return null;
    }

    @Override
    public void showProgressChart(Long id) {

    }

    @Override
    public ApplyProgressBpmnJsVO showProgressChartBpmnJs(Long id) {
        return null;
    }

    @Override
    public boolean revokeApply(Long id) {
        return false;
    }

    @Override
    public boolean deleteById(Long id) {
        return false;
    }
}
