// declare关键字用于告诉编译器某个标识符已经存在，但不需要进行类型检查
declare module '*.vue' {
  import type {DefineComponent} from 'vue';
  const component: DefineComponent<{}, {}, any>;
  const _default: DefineComponent<{}, {}, {}, any, any, any, any, {}, any, any, {}>;
  export default component;
}

// 声明全局变量
interface Window {
  $contextRoot: string;
  $authorization: string;
}

// 属性栏模块包类型
declare module 'bpmn-js-properties-panel';
// 属性栏扩展属性模块包类型
declare module 'camunda-bpmn-moddle/resources/camunda';

// 小地图模块包类型
declare module 'diagram-js-minimap';

// 加密
declare module 'crypto-js' {
  export function MD5(message: string): string;
}