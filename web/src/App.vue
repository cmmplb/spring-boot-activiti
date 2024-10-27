<template>
  <div id="app">
    <!-- 设置挂载点-当 url 的 hash 值路径切换, 显示规则里对应的组件到这 -->
    <el-config-provider :locale="locale">
      <router-view />
    </el-config-provider>
  </div>
</template>

<script lang="ts" setup>
import { ElConfigProvider } from 'element-plus'
// 引入中文包, 2.3.7 版本及之前引入方式：
// import locale from "element-plus/lib/locale/lang/zh-cn"
// 2.3.8+ 版本引入方式：
import locale from 'element-plus/es/locale/lang/zh-cn'
</script>