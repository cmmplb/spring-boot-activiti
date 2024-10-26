import {reactive} from 'vue';

/**
 * 菜单列表, 后续可以从后台获取
 */
export const menuList = reactive<Array<Menu>>([
  {
    'id': 1,
    'name': '首页',
    'icon': 'House',
    'path': '/',
    'parentId': 0
  },
  {
    'id': 2,
    'name': '流程管理',
    'icon': 'Platform',
    'path': '/process',
    'parentId': 0,
    'children': [
      {
        'id': 3,
        'name': '模型管理',
        'icon': 'Suitcase',
        'path': '/process/model',
        'parentId': 2
      }
    ]
  }
]);