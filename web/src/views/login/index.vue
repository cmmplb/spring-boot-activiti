<template>
  <div class='login-container'>
    <!-- 卡片组件 -->
    <!-- 通过shadow属性设置卡片阴影出现的时机：always、hover或never -->
    <el-card class="login-card" shadow="always">
      <div class="login-menu">
        账号密码登录
      </div>
      <div class="login-body">
        <UserLogin/>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import UserLogin from '@/views/login/user-login.vue';
</script>

<style scoped lang='scss'>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 210px;
  width: 100%;
  height: 100%;

  .login-card {
    width: 20%;
    //margin: 0 auto;
    padding: 10px;

    .login-menu {
      margin-bottom: 20px;
      width: 100%;
      text-align: center;
      color: #409EFF;
      font-size: 18px;
    }
  }
}
</style>