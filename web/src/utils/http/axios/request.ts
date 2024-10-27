import axios, {AxiosInstance, AxiosResponse} from 'axios';
import {RequestConfig, Result} from '@/utils/http/axios/axios';

export class Request {

  private instance: AxiosInstance;

  constructor(config: RequestConfig) {
    this.instance = axios.create(config);
    this.instance.interceptors.request.use(config.interceptors?.requestInterceptor, config.interceptors?.requestInterceptorCatch);
    this.instance.interceptors.response.use(config.interceptors?.responseInterceptor, config.interceptors?.responseInterceptorCatch);
  }

  request<T>(config: RequestConfig): Promise<T> {
    return new Promise((resolve, reject) => {
      if (config.interceptors?.requestInterceptor) {
        config = config.interceptors.requestInterceptor(config);
      }
      this.instance.request<any, AxiosResponse<Result>>(config)
        .then((res: AxiosResponse<Result>) => {
          if (config.interceptors?.responseInterceptor) {
            res = config.interceptors.responseInterceptor(res);
          }
          resolve(res as unknown as Promise<T>);
        }).catch((err) => {
        reject(err);
      });
    });
  }

  get<T>(config: RequestConfig): Promise<T> {
    return this.request<T>({...config, method: 'GET'});
  }

  post<T>(config: RequestConfig): Promise<T> {
    return this.request<T>({...config, method: 'POST'});
  }

  put<T>(config: RequestConfig): Promise<T> {
    return this.request<T>({...config, method: 'PUT'});
  }

  delete<T>(config: RequestConfig): Promise<T> {
    return this.request<T>({...config, method: 'DELETE'});
  }

  patch<T>(config: RequestConfig): Promise<T> {
    return this.request<T>({...config, method: 'PATCH'});
  }
}