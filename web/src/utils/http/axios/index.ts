import {Request} from './request';
import {ElMessage} from 'element-plus';
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
    // 解构一层data
    return res.data;
  },

  /**
   * 响应错误处理
   * @param err
   */
  responseInterceptorCatch: (err) => {
    // // 处理 HTTP 网络错误
    let message = '';
    // // HTTP 状态码
    const status = err.response?.status;
    if (status === 400) {
      console.log('err.response:', err.response);
      if (err.response?.data) {
        // 返回包含data属性
        return err.response?.data;
      } else {
        return err.response?.statusText;
      }
    }
    if (status !== 200) {
      if (err.response?.data.msg) {
        ElMessage({message: err.response?.data.msg, type: 'error'});
      } else {
        ElMessage({message: '服务器繁忙', type: 'error'});
      }
    }
    return message;
  }
};
// 创建一个新的请求,并传入参数
const request: Request = new Request({
  baseURL: import.meta.env.VITE_APP_BASE_API,
  timeout: 5000,
  interceptors
});

export default request;