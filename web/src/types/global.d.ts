// declare关键字用于告诉编译器某个标识符已经存在，但不需要进行类型检查
declare module '*.vue' {
  import type {DefineComponent} from 'vue'
  const component: DefineComponent<{}, {}, any>
  const _default: DefineComponent<{}, {}, {}, any, any, any, any, {}, any, any, {}>;
  export default component
}