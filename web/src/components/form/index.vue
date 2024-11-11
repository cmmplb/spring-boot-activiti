<template>
  <div class='form-container'>
    <!-- inline 属性可以让表单域变为行内的表单域 -->
    <el-form
      ref="dataFormRef"
      label-width="auto"
      :inline="inline"
      :model="form"
      :rules="rules"
    >
      <template v-for="formItem in formItems">
        <el-form-item
          :label="formItem.label"
          :prop="formItem.prop"
          :rules=formItem.rules
          v-if="condition(form, formItem)"
        >
          <!-- 没有定义或者定义为 text -->
          <template v-if="formItem.type === undefined || formItem.type === 'text'">
            <el-input v-model="form[formItem.prop]" clearable :placeholder="formItem.placeholder"
                      :suffix-icon="formItem.suffixIcon" :prefix-icon="formItem.prefixIcon"></el-input>
          </template>
          <template v-if="formItem.type === 'textarea'">
            <el-input v-model="form[formItem.prop]" type="textarea" :placeholder="formItem.placeholder"
                      :suffix-icon="formItem.suffixIcon" :prefix-icon="formItem.prefixIcon"></el-input>
          </template>
          <template v-if="formItem.type === 'password'">
            <el-input v-model="form[formItem.prop]" show-password clearable
                      :placeholder="formItem.placeholder" :suffix-icon="formItem.suffixIcon"
                      :prefix-icon="formItem.prefixIcon"></el-input>
          </template>
          <template v-if="formItem.type === 'switch'">
            <el-switch v-model="form[formItem.prop]" :active-icon="formItem.prefixIcon"
                       :inactive-icon="formItem.suffixIcon"></el-switch>
          </template>
          <template v-if="formItem.type === 'radio'">
            <el-radio-group v-model="form[formItem.prop]" v-for="option in formItem.options">
              <el-radio :key="option.value" :value="option.value">{{ option.label }}</el-radio>
            </el-radio-group>
          </template>
          <template v-if="formItem.type === 'datetime'">
            <el-date-picker v-model="form[formItem.prop]" type="datetime" :placeholder="formItem.placeholder"
                            value-format="yyyy-MM-DD HH:mm:ss"/>
          </template>
        </el-form-item>
      </template>
      <!-- **注意**: Vue3 中使用具名插槽需要使用 template 进行包裹起来 -->
      <!-- 操作插槽, 具名插槽: 即 <slot> 元素上使用 name 属性用来标识插槽, , 还有默认插槽和作用域插槽 -->
      <slot name="buttons"></slot>
    </el-form>
  </div>
</template>

<script setup lang="ts">

import {FormItem} from '@/components/form/form';
import {FormInstance} from 'element-plus';
import {nextTick, onMounted, ref} from 'vue';

defineProps({
  // 表单对象
  form: {
    type: Object,
    default: () => {
      return {};
    }
  },
  // 表单校验规则
  rules: {
    type: Object,
    default: () => {
      return {};
    }
  },
  // 表单项配置
  formItems: {
    type: Array<FormItem>,
    default: () => {
      return [];
    }
  },
  // 是否为行内表单
  inline: {
    type: Boolean,
    default: false
  }
});

// Vue 3 写法, 获取 ref 定义的组件实例
const dataFormRef = ref<FormInstance>();

// 挂载完毕后执行的回调函数
onMounted(() => {
  init();
});

// 初始化表单
const init = () => {
  // 在 DOM 更新后执行回调
  nextTick(async () => {
    if (!dataFormRef.value) return;
    // 重置数据
    dataFormRef.value.resetFields();
  });
};

// 确认事件
const validate = () => {
  if (!dataFormRef.value) return;
  return new Promise((resolve, reject) => {
    dataFormRef.value!.validate(async (valid, fields) => {
        if (valid) {
          resolve(true);
        } else {
          reject('未通过字段校验:' + fields);
        }
      }
    );
  });
};

// 表单项显示条件
const condition = (form: any, formItem: FormItem) => {
  if (formItem.conditions) {
    for (let i = 0; i < formItem.conditions.length; i++) {
      const ele = formItem.conditions[i];
      if (form[ele.prop] !== ele.value) {
        return false;
      }
    }
  }
  return true;
};

// 在 <script setup> 中, 所有定义的变量和函数默认是私有的, 不能从组件外部访问, 通过 defineExpose 显式指定暴露方法
defineExpose({
  init, validate
});
</script>

<style scoped lang='scss'>

</style>