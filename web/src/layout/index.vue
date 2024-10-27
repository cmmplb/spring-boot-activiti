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

  // 使用视口宽高
  width: 100vw;
  height: 100vh;

  // 左侧菜单样式
  .app-wrapper-aside {
    width: 200px;
    border: 1px solid #ddd;
  }

  // 主体样式
  .main-container {

    // 头部样式
    .main-container-header {
      --el-header-padding: 10px
    }

    // 内容样式
    .main-container-content {
      --el-main-padding: 10px
    }

  }
}
</style>