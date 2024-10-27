<template>
  <div class='sidebar-item-container'>
    <!-- 循环菜单列表 -->
    <template v-for="menu in menuList">
      <!-- 没有子菜单直接显示, index 是唯一标志 -->
      <el-menu-item
        :index="menu.path"
        v-if="!hasChildren(menu)"
        @click="open(menu)"
      >
        <el-icon>
          <!-- 使用 component 组件, 通过 is 动态绑定图标, 值为组件名称 -->
          <component :is="menu.icon"></component>
        </el-icon>
        <span class="menu_name">{{ menu.name }}</span>
      </el-menu-item>
      <!-- 多级菜单 -->
      <el-sub-menu v-else :index="menu.path">
        <template #title>
          <el-icon>
            <component :is="menu.icon"></component>
          </el-icon>
          <span class="menu_name">{{ menu.name }}</span>
        </template>
        <!-- 引入自身, 来实现递归多级菜单 -->
        <SidebarItem :menuList="menu.children"/>
      </el-sub-menu>
    </template>
  </div>
</template>

<script setup lang="ts">
import {useRouter} from 'vue-router';

const router = useRouter();

defineProps({
  menuList: {
    type: Array<Menu>
  }
});
/**
 * 点击事件
 * @param menu
 */
const open = (menu: Menu) => {
  // 点击菜单, 跳转到对应的路由地址
  router.push({
    path: menu.path
  });
};

/**
 * 判断是否有子菜单
 * @param menu
 */
const hasChildren = (menu: Menu) => {
  // 如果有子菜单字段并且长度大于 0
  return menu.children && menu.children.length > 0;
};
</script>

<style scoped lang='scss'>

</style>