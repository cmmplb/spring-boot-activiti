package io.github.cmmplb.activiti.service.act;

import io.github.cmmplb.activiti.beans.PageResult;
import io.github.cmmplb.activiti.beans.QueryPageBean;
import io.github.cmmplb.activiti.domain.dto.act.SuspendDefinitionDTO;
import io.github.cmmplb.activiti.domain.vo.act.ProcessDefinitionVO;

/**
 * @author penglibo
 * @date 2024-11-02 18:57:48
 * @since jdk 1.8
 */

public interface ProcessDefinitionService {

    PageResult<ProcessDefinitionVO> getByPaged(QueryPageBean queryPageBean);

    String show(String deploymentId, String resourceName);

    void showChart(String id);

    String showChartBpmnJs(String id);

    boolean exchangeToModel(String id, Integer designType);

    boolean suspend(SuspendDefinitionDTO dto);

    boolean activate(SuspendDefinitionDTO dto);
}
