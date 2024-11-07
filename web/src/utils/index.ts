// 下载文件
import {AxiosResponse} from 'axios';
import CryptoJS from 'crypto-js';
import {ElMessage} from 'element-plus';

export const downFile = (response: AxiosResponse<any, any>, fileName: string) => {
  // 创建一个新的 Blob 对象，我这里在 interceptors.responseInterceptor 中判断没有解构, 所以文件流信息在 data 里面
  const blob = new Blob([response.data]);
  const reader = new FileReader();
  reader.readAsText(blob, 'UTF-8');
  // 监听文件读取结束后事件
  reader.onloadend = () => {
    // 读取文件内容
    const content = String(reader.result);
    // 出现了错误
    if ('{' === content.substring(0, 1)) {
      // {"msg":error}
      const parse = JSON.parse(content);
      ElMessage({type: 'error', message: parse.msg});
    } else {
      // 获取响应头
      let headers = response.headers;
      const blob = new Blob([response.data], {
        // 注意这里的是小写 content-type
        type: headers['content-type']
      });
      // 如果后端设置了文件名称
      const nameKey = (Object.keys(response.headers) || []).find((ele) => {
        return ele.toLowerCase() == 'content-disposition';
      });
      if (nameKey) {
        // content-disposition: "attachment;filename=leave-bpmn-js.bpmn20.xml"
        fileName = response.headers[nameKey].split('=')[1];
        fileName = decodeURIComponent(fileName);
      }
      // 创建一个指向新 Blob 对象的 URL
      let url = URL.createObjectURL(blob);
      // 创建一个 a 标签用于下载，隐藏，设置 href 属性，触发点击方法
      let link = document.createElement('a');
      link.style.display = 'none';
      link.href = url;
      // 设置下载文件名
      link.setAttribute('download', fileName);
      document.body.appendChild(link);
      // 触发下载
      link.click();
      // 清理并移除元素和对象URL
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
    }
  };
};

export const getBase = () => {
  return import.meta.env.VITE_APP_BASE_PATH;
};

export const getRoutingModeBase = () => {
  if (getRoutingMode() === 'hash') {
    // hash 模式需要添加 /# 前缀
    return getBase() + '/#';
  }
  return getBase();
};

export const getRoutingMode = () => {
  // 检查当前URL中的hash部分
  const hash = window.location.hash;

  if (hash.length > 1) {
    // 如果hash长度大于1，则表示是hash模式
    return 'hash';
  } else {
    // 否则，可能是 history 模式
    const historyApiSupported = 'pushState' in history;
    return historyApiSupported ? 'history' : 'hash';
  }
};

// 静态方法，用于对给定字符串进行MD5加密
export const md5 = (str: string): string => {
  return CryptoJS.MD5(str).toString();
};