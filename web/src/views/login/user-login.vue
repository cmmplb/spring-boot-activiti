<template>
  <div class='user-login-container'>
    <!-- 登录表单 -->
    <Form
      ref="dataFormRef"
      :form="dataForm"
      :formItems="dataFormItems"
    >
      <template #buttons>
        <el-form-item>
          <el-button class="login-button" type="primary" :loading="loading" @click="doLogin">登录</el-button>
        </el-form-item>
      </template>
    </Form>
  </div>
</template>

<script setup lang="ts">

import {reactive, ref} from 'vue';
import Form from '@/components/form/index.vue';
import {FormItem} from '@/components/form/form';
import {useRouter} from 'vue-router';
import {ElMessage} from 'element-plus';
import {login} from '@/api/login/authentication.ts';
import {md5} from '@/utils';
import constant from '@/const/constant.ts';

// 登陆按钮加载
const loading = ref(false);

// Vue 3 写法, 获取 ref 定义的组件实例
const dataFormRef = ref();
// 表单参数
const dataForm = reactive<LoginDTO>({
  username: 'admin',
  password: '123456'
});
// 表单项
const dataFormItems = reactive<FormItem[]>([
  {
    prop: 'username',
    prefixIcon: 'User',
    placeholder: '请输入用户名',
    rules: [
      {required: true, message: '请输入用户名', trigger: 'blur'}
    ]
  },
  {
    prop: 'password',
    prefixIcon: 'Lock',
    type: 'password',
    placeholder: '请输入密码',
    rules: [
      {required: true, message: '请输入密码', trigger: 'blur'}
    ]
  }
]);

const router = useRouter();

/**
 * 点击登录按钮
 */
const doLogin = async () => {
  if (dataFormRef.value) {
    const valid = await dataFormRef.value.validate();
    if (valid) {
      // 密码加密
      dataForm.password = md5(dataForm.password);
      const res = await login(dataForm);
      if (res.code === 200 && res.data) {
        // 保存令牌
        localStorage.setItem(constant.authorizationPrefix, res.data);
        ElMessage({message: '登录成功', type: 'success'});
        const redirectTo = localStorage.getItem(constant.redirectToPrefix);
        if (redirectTo) {
          // 跳转到回调地址
          window.location.href = redirectTo;
        } else {
          // 跳转到首页
          await router.push({path: '/'});
        }
      } else {
        ElMessage({message: res.msg, type: 'error'});
      }
    }
  }
};

</script>

<style lang="scss" scoped>
.user-login-container {

  // 登录按钮
  .login-button {
    margin-top: 10px;
    margin-left: 5%;
    width: 90%;
    height: 40px;
    border: 1px solid #409EFF;
    border-radius: 15px;
    background: none;
    font-size: 16px;
    // 字符之间的间距
    letter-spacing: 2px;
    font-weight: 300;
    color: #409EFF;
    cursor: pointer;
    transition: 0.25s;
  }

  .login-button:hover {
    background: #409EFF11;
    color: #409EFF;
  }

  // 覆盖默认样式, 设置边框圆角
  ::v-deep(.el-input__wrapper) {
    border-radius: 10px;
  }

  ::v-deep(.el-input) {
    input {
      padding: 20px 0 20px 0;
      text-indent: 5px;
    }
  }
}
</style>