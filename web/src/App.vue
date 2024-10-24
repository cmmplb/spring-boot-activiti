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
    <iframe src="/modeler.html?modelId='dasdada'" width="100%" height="100%" style="border:none"></iframe>
  </div>
</template>