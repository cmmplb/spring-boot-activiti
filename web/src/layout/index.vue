<template>
  <el-container class="app-wrapper">
    <el-aside class="app-wrapper-aside">
      <Sidebar/>
    </el-aside>
    <el-container class="main-container">
      <!-- header -->
      <el-header class="main-container-header">
        <Navbar/>
      </el-header>
      <el-main class="main-container-content">
        <!-- 挂载嵌套路由组件 -->
        <router-view/>
      </el-main>
    </el-container>
  </el-container>
</template>
<script setup lang="ts">
import Sidebar from './components/sidebar/index.vue';
import Navbar from '@/layout/components/navbar.vue';
</script>
<style scoped lang='scss'>
// sass 1.80 不再支持 @import 需要使用 `@use’
@use "@/assets/styles/common.scss";

.app-wrapper {
  position: relative;
  height: 100%;
  width: 100%;

  // 左侧菜单样式
  .app-wrapper-aside {
    width: 200px;
  }

  // 主体样式
  .main-container {

    // 头部样式
    .main-container-header {
    }

    // 内容样式
    .main-container-content {
      // 加个边框效果查看布局
      border: 1px solid #000;
    }

  }
}
</style>