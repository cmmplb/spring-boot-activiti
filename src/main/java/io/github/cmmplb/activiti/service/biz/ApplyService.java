package io.github.cmmplb.activiti.service.biz;

import io.github.cmmplb.activiti.beans.PageResult;
import io.github.cmmplb.activiti.domain.dto.biz.ApplyDTO;
import io.github.cmmplb.activiti.domain.dto.biz.ApplyQueryDTO;
import io.github.cmmplb.activiti.domain.entity.biz.Apply;
import com.baomidou.mybatisplus.extension.service.IService;
import io.github.cmmplb.activiti.domain.vo.biz.ApplyDetailsVO;
import io.github.cmmplb.activiti.domain.vo.biz.ApplyProgressBpmnJsVO;
import io.github.cmmplb.activiti.domain.vo.biz.ApplyVO;

/**
 * @author penglibo
 * @date 2024-11-08 10:48:05
 * @since jdk 1.8
 */
public interface ApplyService extends IService<Apply> {

    boolean save(ApplyDTO dto);

    PageResult<ApplyVO> getByPaged(ApplyQueryDTO dto);

    ApplyDetailsVO getApplyDetailsById(Long id);

    void showProgressChart(Long id);

    ApplyProgressBpmnJsVO showProgressChartBpmnJs(Long id);

    boolean revokeApply(Long id);

    boolean deleteById(Long id);
}
