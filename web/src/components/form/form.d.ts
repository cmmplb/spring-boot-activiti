// declare关键字用于告诉编译器某个标识符已经存在，但不需要进行类型检查
// 表格列配置
import {FormItemRule} from 'element-plus';
import {Arrayable} from 'element-plus/es/utils';

declare interface FormItem {
  // 属性名
  prop: string,
  // 显示名称
  label?: string,
  // 自定义前缀图标
  prefixIcon?: string,
  // 自定义后缀图标
  suffixIcon?: string,
  // 表单项类型
  type?: InputType,
  // 表单项显示条件
  conditions?: Condition[],
  // 选项配置
  options?: Option[],
  // 提示语
  placeholder?: string,
  // 表单校验规则
  rules?: Arrayable<FormItemRule>,
}

// 定义字面量类型的联合类型, 给变量赋值, 只能赋联合类型中定义的字面量值
type InputType = undefined | 'text' | 'textarea' | 'password' | 'radio' | 'switch' | 'datetime';

// 选项配置
declare interface Option {
  // 选项值
  value: string | number | boolean,
  // 选项名称
  label: string,
}

// 显示条件
declare interface Condition {
  // 属性名
  prop: string,
  // 属性值
  value: string | number | boolean,
}
