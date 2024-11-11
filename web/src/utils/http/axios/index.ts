import {Request} from './request';
import {RequestConfig, RequestInterceptors} from '@/utils/http/axios/axios';
import constant from '@/const/constant.ts';
import {ElMessage} from 'element-plus';
import {router} from '@/router/index.ts';

const interceptors: RequestInterceptors = {

  /**
   * 请求拦截器处理
   * @param config
   */
  requestInterceptor: (config: RequestConfig) => {
    const authorization = localStorage.getItem(constant.authorizationPrefix);
    if (authorization) {
      config.headers = {
        ...config.headers,
        Authorization: authorization
      };
    }
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
    console.log('响应错误处理 err:', err);
    // HTTP 状态码
    const status = err.response?.status;
    if (status === 401) {
      // 记录当前页面地址，用于登录成功回调
      console.log('router.currentRoute.value.path:', router.currentRoute.value.path);
      let refer = router.currentRoute.value.path;
      if (router.currentRoute.value.path !== '/login') {
        // 存储当前页面，用于登录成功后重定向到当前页
        localStorage.setItem(constant.redirectToPrefix, refer);
        // 跳转到首页
        router.push({path: '/login'}).then();
        // 防止页面重定向失败, 刷新页面
        setTimeout(() => {
          location.reload();
        }, 2000);
      }
      ElMessage({message: '登陆失效', type: 'error'});
      return Promise.reject(new Error('登陆失效'));
    }
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