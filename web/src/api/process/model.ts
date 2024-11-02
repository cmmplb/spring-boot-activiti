import request from '@/utils/http/axios';
import {PageResult, QueryPageBean, Result} from '@/utils/http/axios/axios';
import {AxiosResponse} from 'axios';

/**
 * 新增
 */
export const save = (data: ModelDTO) => {
  return request.post<Result<boolean>>({
    url: '/model/save',
    data
  });
};

/**
 * 修改
 */
export const updateById = (id: string, data: ModelDTO) => {
  return request.put<Result<boolean>>({
    url: '/model/' + id,
    data
  });
};

/**
 * 根据id删除
 */
export const removeById = (id: string) => {
  return request.delete<Result<boolean>>({
    url: '/model/' + id
  });
};

/**
 * 分页条件查询列表
 */
export const getByPaged = (data: QueryPageBean) => {
  return request.post<Result<PageResult<ModelVO>>>({
    url: '/model/paged',
    data
  });
};

/**
 * 根据id获取详情信息
 */
export const getInfoById = (id: string) => {
  return request.get<Result<PageResult<ModelVO>>>({
    url: '/model/' + id
  });
};

/**
 * 导出流程模型文件
 */
export const exportModel = (id: string) => {
  return request.get<AxiosResponse>({
    url: '/model/export/' + id,
    // 重要! 设置响应类型为 blob 或 arraybuffer
    responseType: 'blob'
  });
};

/**
 * 部署模型
 */
export const deployment = (id: string) => {
  return request.post<Result<boolean>>({
    url: '/model/deploy/' + id
  });
};