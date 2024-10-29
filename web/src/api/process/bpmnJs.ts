import request from '@/utils/http/axios';
import {Result} from '@/utils/http/axios/axios';

/**
 * 获取模型流程设计
 */
export const getBpmnInfoById = (id: string) => {
  return request.get<Result<BpmnJsVO>>({
    url: '/bpmn-js/' + id
  });
};

/**
 * 保存流程设计
 */
export const save = (id: string, data: BpmnJsDTO) => {
  return request.put<Result<boolean>>({
    url: '/bpmn-js/save/' + id,
    data
  });
};