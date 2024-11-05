import request from '@/utils/http/axios';
import {PageResult, QueryPageBean, Result} from '@/utils/http/axios/axios';

enum Api {
  BASE = '/process/definition',
  PAGED = BASE + '/paged',
  REMOVE = BASE + '/',
  SHOW = BASE + '/show/',
  SHOW_CHART = BASE + '/show/chart/',
  SHOW_CHART_BPMN_JS = BASE + '/show/chart/bpmn-js/',
  EXCHANGE = BASE + '/exchange/',
  SUSPEND = BASE + '/suspend/',
  ACTIVATE = BASE + '/activate/',
}

/**
 * 分页条件查询列表
 */
export const getByPaged = (data: QueryPageBean) => {
  return request.post<Result<PageResult<ProcessDefinitionVO>>>({
    url: Api.PAGED,
    data
  });
};

/**
 * 查看流程文件
 */
export const show = (deploymentId: string, params: any) => {
  return request.get<Result<string>>({
    url: Api.SHOW + deploymentId,
    params
  });
};

/**
 * 查看流程图
 */
export const showChart = (id: string) => {
  return request.get<Blob>({
    url: Api.SHOW_CHART + id,
    // 重要! 设置响应类型为 blob 或 arraybuffer
    responseType: 'blob'
  });
};

/**
 * 查看流程图-bpmn-js
 */
export const showChartBpmnJs = (id: string) => {
  return request.get<Result<string>>({
    url: Api.SHOW_CHART_BPMN_JS + id
  });
};

/**
 * 将流程定义转为模型
 */
export const exchangeToModel = (id: string, designType: number) => {
  return request.post<Result<boolean>>({
    url: Api.EXCHANGE + id + '/' + designType
  });
};

/**
 * 挂起流程定义
 */
export const suspend = (id: string, data: SuspendDefinitionDTO) => {
  return request.post<Result<boolean>>({
    url: Api.SUSPEND + id,
    data
  });
};

/**
 * 激活流程定义
 */
export const activate = (id: string, data: SuspendDefinitionDTO) => {
  return request.post<Result<boolean>>({
    url: Api.ACTIVATE + id,
    data
  });
};