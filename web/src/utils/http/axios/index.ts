import {Request} from './request';
import {RequestConfig, RequestInterceptors} from '@/utils/http/axios/axios';

const interceptors: RequestInterceptors = {

  /**
   * 请求拦截器处理
   * @param config
   */
  requestInterceptor: (config: RequestConfig) => {
    console.log('请求成功的拦截');
    return config;
  },

  /**
   * 请求错误拦截器处理
   * @param err
   */
  requestInterceptorCatch: (err) => {
    console.log('请求失败的拦截');
    return err;
  },

  /**
   * 响应拦截器处理
   * @param res
   */
  responseInterceptor: (res) => {
    console.log('响应成功的拦截');
    if (res.headers) {
      // 注意这里的是小写 content-type
      const contentType = res.headers['content-type'];
      if (contentType === 'application/force-download' || contentType === 'application/xml' || contentType === 'application/octet-stream') {
        // 这里不解构
        return res;
      }
    }
    // 解构一层 data
    return res.data;
  },

  /**
   * 响应错误处理
   * @param err
   */
  responseInterceptorCatch: (err) => {
    if (err.response?.data) {
      // 解构到 data 属性
      return err.response?.data;
    }
    return err;
  }
};
// 创建一个新的请求,并传入参数
const request: Request = new Request({
  baseURL: import.meta.env.VITE_APP_BASE_API,
  timeout: 60000,
  interceptors
});

export default request;