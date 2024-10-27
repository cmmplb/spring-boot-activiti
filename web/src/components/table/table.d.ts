// declare关键字用于告诉编译器某个标识符已经存在，但不需要进行类型检查
// 表格列配置
declare interface Column {
  // 属性名
  prop: string,
  // 显示名称
  label: string,
  // 表格宽度
  width?: number,
  // 开关选项
  switch?: Switch,
  // 表格为字体图标
  icon?: boolean,
  // 列选项
  option?: Option[],
  // 是否隐藏额外内容并在单元格悬停时使用 Tooltip 显示
  showOverflowTooltip?: boolean
}

// 表格开关配置, 例如用户账号状态, 是否启用, 点击 switch 来切换状态, 有的数据禁止修改状态则对应 disabled
declare interface Switch {
  // 禁用的字段名
  disabled?: string,
  // 禁用的值
  disabledValue?: string,
  // 激活的值
  activeValue: boolean | string | number,
  // 未激活的值
  inactiveValue: boolean | string | number
}

// 表格列选项配置, 例如用户性别字段, 0:男, 1:女, 2:保密
declare interface Option {
  // 选项值
  value: string | number,
  // 选项名称
  label: string,
  // 启用 element plus 的 tag 标签, 对应 tag-type 属性
  tagType: string
}