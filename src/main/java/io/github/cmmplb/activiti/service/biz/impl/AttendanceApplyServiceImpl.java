package io.github.cmmplb.activiti.service.biz.impl;

import io.github.cmmplb.activiti.constants.BizConstant;
import io.github.cmmplb.activiti.factory.ApplyAbstractHandler;
import io.github.cmmplb.activiti.factory.ApplyFactory;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.cmmplb.activiti.dao.biz.AttendanceApplyMapper;
import io.github.cmmplb.activiti.domain.entity.biz.AttendanceApply;
import io.github.cmmplb.activiti.service.biz.AttendanceApplyService;

/**
 * @author penglibo
 * @date 2024-11-08 10:48:05
 * @since jdk 1.8
 */
@Service
public class AttendanceApplyServiceImpl extends ApplyAbstractHandler<AttendanceApplyMapper, AttendanceApply> implements AttendanceApplyService {

    @Override
    public Long invoke(String jsonParams) throws Exception {
        // 添加考勤申请


        return super.invoke(jsonParams);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ApplyFactory.register(BizConstant.ApplyTypeEnum.ATTENDANCE.getType(), this);
    }
}
