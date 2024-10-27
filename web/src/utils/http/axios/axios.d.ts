import {AxiosRequestConfig, AxiosResponse} from 'axios';

declare interface RequestInterceptors {
  requestInterceptor: (config: any) => any;
  requestInterceptorCatch?: (error: any) => any;
  responseInterceptor?: (res: AxiosResponse) => any;
  responseInterceptorCatch?: (error: any) => any;
}

declare interface RequestConfig extends AxiosRequestConfig {
  interceptors?: RequestInterceptors;
}

declare interface Result<T = any> {
  code: number;
  msg: string;
  data: T;
  timestamp: number;
}

declare interface PageResult<T = any> {
  total: number;
  rows: [];
}

declare interface QueryPageBean {
  size?: number | undefined;
  current?: number | undefined;
  start?: number | undefined;
  keywords?: string;
}
