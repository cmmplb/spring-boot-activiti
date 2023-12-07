package com.cmmplb.activiti.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmmplb.activiti.dao.LeaveApplyDateMapper;
import com.cmmplb.activiti.entity.LeaveApplyDate;
import com.cmmplb.activiti.service.LeaveApplyDateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author penglibo
 * @date 2023-11-23 16:46:26
 * @since jdk 1.8
 */

@Slf4j
@Service
@Transactional
public class LeaveApplyDateServiceImpl extends ServiceImpl<LeaveApplyDateMapper, LeaveApplyDate> implements LeaveApplyDateService {
}
