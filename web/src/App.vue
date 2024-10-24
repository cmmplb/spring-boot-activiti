<script setup lang="ts">
import {onMounted} from 'vue';
import axios from 'axios';

onMounted(() => {
  axios.get('/api/test').then(res => {
    console.log('test:', res);
  });
});
</script>

<template>
  <div>
    <h1>Home</h1>
  </div>
</template>