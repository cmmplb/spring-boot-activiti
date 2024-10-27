// declare关键字用于告诉编译器某个标识符已经存在，但不需要进行类型检查
declare interface Menu {
  // 菜单id
  id: number;
  // 菜单图标
  icon: string;
  // 菜单名称
  name: string;
// 菜单路径
  path: string;
// 父级菜单 id
  parentId: number;
// 子菜单集合
  children?: Array<Menu>;
}