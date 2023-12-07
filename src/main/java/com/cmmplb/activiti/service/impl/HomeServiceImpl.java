package com.cmmplb.activiti.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cmmplb.activiti.dto.ApplyStatisticsTimeDTO;
import com.cmmplb.activiti.entity.Apply;
import com.cmmplb.activiti.service.ApplyService;
import com.cmmplb.activiti.service.EvectionApplyService;
import com.cmmplb.activiti.service.HomeService;
import com.cmmplb.activiti.service.LeaveApplyService;
import com.cmmplb.activiti.util.DateUtil;
import com.cmmplb.activiti.vo.ApplyStatisticsVO;
import com.cmmplb.activiti.vo.ItemCountVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author penglibo
 * @date 2023-12-06 13:47:24
 * @since jdk 1.8
 */


@Slf4j
@Service
@Transactional
public class HomeServiceImpl implements HomeService {

    @Autowired
    private ApplyService applyService;

    @Autowired
    private LeaveApplyService leaveApplyService;

    @Autowired
    private EvectionApplyService evectionApplyService;

    @Override
    public ItemCountVO getItemCount() {
        ItemCountVO vo = new ItemCountVO();
        // 流程状态:0-进行中;1-已完成;2-已驳回;3-已撤销;
        int incompleteCount = applyService.count(new LambdaQueryWrapper<Apply>().eq(Apply::getStatus, (byte) 0));
        int completedCount = applyService.count(new LambdaQueryWrapper<Apply>().eq(Apply::getStatus, (byte) 1));
        int leaveCount = leaveApplyService.count();
        int evectionCount = evectionApplyService.count();
        vo.setIncompleteCount(incompleteCount);
        vo.setCompletedCount(completedCount);
        vo.setLeaveCount(leaveCount);
        vo.setEvectionCount(evectionCount);
        return vo;
    }

    @Override
    public ApplyStatisticsVO getApplyStatistics(Integer type) {
        List<String> timeList = new ArrayList<>();
        List<String> nameList = new ArrayList<>();
        if (type == 1) {
            // 生成一个前24小时的集合
            List<Integer> last24Hour = DateUtil.getLast24Hour();
            for (Integer i : last24Hour) {
                timeList.add(i + ":00");
            }
        } else {
            // 生成前一个月的集合
            timeList = DateUtil.getLastMonthDate();
        }
        List<ApplyStatisticsTimeDTO> applyList = applyService.getStatisticsList(type);
        List<ApplyStatisticsVO.ApplyStatisticsData> dataList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(applyList)) {
            ApplyStatisticsVO.ApplyStatisticsData data;
            // 按类型分组
            Map<Byte, List<ApplyStatisticsTimeDTO>> typeMap = applyList.stream().collect(Collectors.groupingBy(ApplyStatisticsTimeDTO::getType));
            for (Map.Entry<Byte, List<ApplyStatisticsTimeDTO>> entry : typeMap.entrySet()) {
                Byte key = entry.getKey();
                data = new ApplyStatisticsVO.ApplyStatisticsData();
                // 类型:1-请假;2-出差;3...
                String name = key.equals((byte)1) ? "请假申请" : "出差申请";
                if (key.equals((byte)1)) {
                    nameList.add(name);
                    data.setName(name);
                } else {
                    nameList.add(name);
                    data.setName(name);
                }
                List<ApplyStatisticsTimeDTO> value = entry.getValue();
                // 再按时间分组
                Map<String, List<ApplyStatisticsTimeDTO>> timeMap = value.stream().collect(Collectors.groupingBy(ApplyStatisticsTimeDTO::getTime));
                List<Integer> count = new ArrayList<>();
                for (String s : timeList) {
                    if (timeMap.containsKey(s)) {
                        count.add(timeMap.get(s).size());
                    } else {
                        count.add(0);
                    }
                }
                data.setData(count);
                dataList.add(data);
            }
        }
        ApplyStatisticsVO vo = new ApplyStatisticsVO();
        vo.setTimeList(timeList);
        vo.setNameList(nameList);
        vo.setDataList(dataList);
        return vo;
    }
}
