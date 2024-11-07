import request from '@/utils/http/axios';
import {Result} from '@/utils/http/axios/axios';

enum Api {
  BASE = '/authentication',
  LOGIN = BASE + '/login',
}

/**
 * 登录
 */
export const login = (data: LoginDTO) => {
  return request.post<Result<string>>({
    url: Api.LOGIN,
    data
  });
};