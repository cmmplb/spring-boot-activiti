<template>
  <div class='home-container'>
    <h1>首页</h1>
    <div>
      登录人：{{ user.name }}
    </div>
  </div>
</template>
<script setup lang="ts">
import {getInfo} from '@/api/sys/user.ts';
import {onMounted, reactive} from 'vue';
import {ElMessage} from 'element-plus';

const user = reactive<UserDetails>({
  id: 0,
  username: '',
  name: '',
  avatar: ''
});

// 挂载完毕后执行的回调函数
onMounted(() => {
  getUserInfo();
});

const getUserInfo = async () => {
  const res = await getInfo();
  if (res.code === 200 && res.data) {
    // 把原来的值覆盖
    Object.assign(user, res.data);
  } else {
    ElMessage({message: res.msg, type: 'error'});
  }
};

</script>