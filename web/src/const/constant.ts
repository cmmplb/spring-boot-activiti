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
      },
      {
        'id': 4,
        'name': '部署管理',
        'icon': 'VideoPlay',
        'path': '/process/deployment',
        'parentId': 2
      },
      {
        'id': 5,
        'name': '定义管理',
        'icon': 'Tickets',
        'path': '/process/definition',
        'parentId': 2
      }
    ]
  }
]);

export default {
  // 凭证
  authorizationPrefix: 'Authorization',
  // 重定向 url
  redirectToPrefix: 'redirectTo',
};