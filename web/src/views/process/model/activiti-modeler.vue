<template>
  <div class='activiti-modeler-container'>
<!--    <iframe width="100%" height="100%" :src="src"></iframe>-->
    <iframe width="100%" height="100%" :src="src"></iframe>
  </div>
</template>

<script setup lang="ts">
import {onMounted} from 'vue';
import {useRouter} from 'vue-router';
import constant from '@/const/constant.ts';
import {getBase} from '@/utils';

const router = useRouter();

// 从路由参数中获取 modelId
const modelId = router.currentRoute.value.query.modelId;

// 模型编辑器地址
const src = getBase() + '/activiti-explorer/modeler.html?modelId=' + modelId;

onMounted(() => {
  // 通过 window 对象全局挂载全局对象或者属性, 存放在内存刷新会清空, 这并不是推荐的做法, 因为直接修改全局对象可能会导致命名冲突, 难以进行模块化管理以及不利于应用的封装与维护
  window.$contextRoot = import.meta.env.VITE_APP_BASE_API;
  window.$authorization = localStorage.getItem(constant.authorizationPrefix) + '';

});

</script>

<style scoped lang='scss'>
.activiti-modeler-container {
  width: 100vw;
  // 设置成 100 右侧会有一个滚动条
  height: 99vh;
}
</style>