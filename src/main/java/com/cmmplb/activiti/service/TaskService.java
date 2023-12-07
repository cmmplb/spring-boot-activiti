package com.cmmplb.activiti.service;

import com.cmmplb.activiti.beans.PageResult;
import com.cmmplb.activiti.dto.HandleTaskDTO;
import com.cmmplb.activiti.dto.TaskQueryDTO;
import com.cmmplb.activiti.vo.CompletedTaskVO;
import com.cmmplb.activiti.vo.IncompleteTaskVO;

/**
 * @author penglibo
 * @date 2023-11-16 10:42:05
 * @since jdk 1.8
 */
public interface TaskService {

    PageResult<IncompleteTaskVO> getByPaged(TaskQueryDTO dto);

    PageResult<CompletedTaskVO> getCompletedByPaged(TaskQueryDTO dto);

    boolean handleTask(HandleTaskDTO dto);

    boolean entrustTask(String taskId, String userId);
}
