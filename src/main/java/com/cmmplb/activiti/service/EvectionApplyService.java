package com.cmmplb.activiti.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cmmplb.activiti.dto.EvectionApplyDTO;
import com.cmmplb.activiti.dto.LeaveApplyDTO;
import com.cmmplb.activiti.entity.EvectionApply;
import com.cmmplb.activiti.entity.LeaveApply;
import com.cmmplb.activiti.vo.EvectionApplyDetailsVO;

/**
 * @author penglibo
 * @date 2023-11-27 09:17:28
 * @since jdk 1.8
 */
public interface EvectionApplyService extends IService<EvectionApply> {

    boolean save(EvectionApplyDTO dto);

    EvectionApplyDetailsVO getDetailsById(Long id);
}
