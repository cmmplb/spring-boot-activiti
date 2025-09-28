package io.github.cmmplb.activiti.service.biz.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.cmmplb.activiti.constants.BizConstant;
import io.github.cmmplb.activiti.dao.biz.AttendanceApplyMapper;
import io.github.cmmplb.activiti.domain.dto.biz.AttendanceApplyDTO;
import io.github.cmmplb.activiti.domain.entity.biz.AttendanceApply;
import io.github.cmmplb.activiti.domain.entity.biz.AttendanceApplyLeave;
import io.github.cmmplb.activiti.domain.vo.biz.AttendanceApplyDetailsVO;
import io.github.cmmplb.activiti.factory.ApplyAbstractHandler;
import io.github.cmmplb.activiti.factory.ApplyFactory;
import io.github.cmmplb.activiti.service.biz.AttendanceApplyLeaveService;
import io.github.cmmplb.activiti.service.biz.AttendanceApplyService;
import io.github.cmmplb.activiti.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author penglibo
 * @date 2024-11-08 10:48:05
 * @since jdk 1.8
 */
@Service
public class AttendanceApplyServiceImpl extends ApplyAbstractHandler<AttendanceApplyMapper, AttendanceApply> implements AttendanceApplyService {

    @Autowired
    private AttendanceApplyLeaveService attendanceApplyLeaveService;

    @Override
    public Long save(String jsonParams) throws Exception {
        // 注意, 我这个是为了演示集成才这样写, 添加考勤的入口都在这一个地方, 实际开发中可能是对应不同的 controller
        AttendanceApplyDTO dto = JSON.parseObject(jsonParams, new TypeReference<AttendanceApplyDTO>() {
        });
        // 添加考勤申请
        AttendanceApply attendanceApply = new AttendanceApply();
        attendanceApply.setUserId(SecurityUtil.getUser().getId());
        attendanceApply.setType(dto.getType());
        attendanceApply.setStatus(BizConstant.StatusEnum.IN_PROGRESS.getStatus());
        attendanceApply.setRemark(dto.getRemark());
        attendanceApply.setCreateTime(new Date());
        attendanceApply.setUpdateTime(new Date());
        baseMapper.insert(attendanceApply);

        // 如果类型是请假, 添加请假时间
        if (dto.getType().equals(BizConstant.AttendanceApply.TypeEnum.LEAVE.getType())) {
            List<AttendanceApplyDTO.LeaveDate> leaveDateList = dto.getLeaveDateList();
            if (!CollectionUtils.isEmpty(leaveDateList)) {
                List<AttendanceApplyLeave> list = new ArrayList<>();
                AttendanceApplyLeave attendanceApplyLeave;
                for (AttendanceApplyDTO.LeaveDate leaveDate : leaveDateList) {
                    attendanceApplyLeave = new AttendanceApplyLeave();
                    attendanceApplyLeave.setAttendanceApplyId(attendanceApply.getId());
                    attendanceApplyLeave.setType(leaveDate.getType());
                    attendanceApplyLeave.setStartTime(leaveDate.getStartTime());
                    attendanceApplyLeave.setEndTime(leaveDate.getEndTime());
                    attendanceApplyLeave.setCreateTime(new Date());
                    attendanceApplyLeave.setUpdateTime(new Date());
                    list.add(attendanceApplyLeave);
                }
                attendanceApplyLeaveService.saveBatch(list);
            }
        }
        return attendanceApply.getId();
    }

    @Override
    public String getDetailsById(Long id) {
        AttendanceApplyDetailsVO attendanceApplyDetails = baseMapper.selectDetailsById(id);
        if (null == attendanceApplyDetails) {
            throw new RuntimeException("考勤信息已删除");
        }
        // 获取关联的请假时间
        List<AttendanceApplyLeave> list = attendanceApplyLeaveService.list(
                new LambdaQueryWrapper<AttendanceApplyLeave>().eq(AttendanceApplyLeave::getAttendanceApplyId, id)
        );
        if (!CollectionUtils.isEmpty(list)) {
            List<AttendanceApplyDetailsVO.AttendanceApplyLeaveVO> leaveDateList = new ArrayList<>();
            AttendanceApplyDetailsVO.AttendanceApplyLeaveVO leave;
            for (AttendanceApplyLeave applyLeave : list) {
                leave = new AttendanceApplyDetailsVO.AttendanceApplyLeaveVO();
                leave.setType(applyLeave.getType());
                leave.setStartTime(applyLeave.getStartTime());
                leave.setEndTime(applyLeave.getEndTime());
                leaveDateList.add(leave);
            }
            attendanceApplyDetails.setAttendanceApplyLeaveList(leaveDateList);
        }
        return JSON.toJSONString(attendanceApplyDetails);
    }

    @Override
    public void deleteById(Long id) {
        // 删除关联的请假申请信息
        AttendanceApply attendanceApply = baseMapper.selectById(id);
        if (null == attendanceApply) {
            throw new RuntimeException("考勤信息已删除");
        }
        // 删除考勤申请
        baseMapper.deleteById(id);

        if (attendanceApply.getType().equals(BizConstant.AttendanceApply.TypeEnum.LEAVE.getType())) {
            // 删除关联的请假时间
            attendanceApplyLeaveService.remove(
                    new LambdaQueryWrapper<AttendanceApplyLeave>().eq(AttendanceApplyLeave::getAttendanceApplyId, id)
            );
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ApplyFactory.register(BizConstant.ApplyTypeEnum.ATTENDANCE.getType(), this);
    }
}
