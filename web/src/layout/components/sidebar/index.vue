<template>
  <div class='sidebar-container'>
    <!--
      default-active: 页面加载时默认激活菜单的 index, string, 默认为 ''
      unique-opened: 是否只保持一个子菜单的展开, boolean 类型, 默认为 false
      mode: 菜单展示模式, enum, 默认为 vertical, 可选值 vertical / horizontal
      show-timeout: 菜单出现前的延迟, 单位为毫秒, 默认为 300
     -->
    <el-menu
      :default-active="activeMenu"
      unique-opened
      mode="vertical"
      :show-timeout="200"
    >
      <SidebarItem :menuList="menuList"/>
    </el-menu>
  </div>
</template>

<script setup lang="ts">

import SidebarItem from '@/layout/components/sidebar/sidebar-item.vue';
import {computed} from 'vue';
import {useRouter} from 'vue-router';
import {menuList} from '@/const/constant.ts';

const router = useRouter();

// 当前激活的菜单项
const activeMenu = computed(() => {
  return router.currentRoute.value.path;
});

</script>