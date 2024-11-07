import request from '@/utils/http/axios';
import {Result} from '@/utils/http/axios/axios';

enum Api {
  BASE = '/user',
  INFO = BASE + '/info',
}

/**
 * 根据id获取详情信息
 */
export const getInfo = () => {
  return request.get<Result<UserDetails>>({
    url: Api.INFO
  });
};