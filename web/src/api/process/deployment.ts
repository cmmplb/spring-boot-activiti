import request from '@/utils/http/axios';
import {PageResult, QueryPageBean, Result} from '@/utils/http/axios/axios';

enum Api {
  BASE = '/deployment',
  PAGED = BASE + '/paged',
  UPLOAD = BASE + '/upload',
  REMOVE = BASE + '/',
}

/**
 * 分页条件查询列表
 */
export const getByPaged = (data: QueryPageBean) => {
  return request.post<Result<PageResult<DeploymentVO>>>({
    url: Api.PAGED,
    data
  });
};

/**
 * 上传部署流程文件
 */
export const upload = (data: FormData) => {
  return request.post<Result<boolean>>({
    url: Api.UPLOAD,
    data
  });
};

/**
 * 根据id删除
 */
export const removeById = (id: string) => {
  return request.delete<Result<boolean>>({
    url: Api.REMOVE + id
  });
};