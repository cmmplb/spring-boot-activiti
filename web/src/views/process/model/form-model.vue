<template>
  <div class='form-container'>
    <el-dialog :title="(form.id? '编辑': '新增')+'模型'" v-model="visibleValue" style="max-width: 500px;padding: 50px"
               :before-close="handleClose">
      <!-- Form 表单: https://element-plus.org/zh-CN/component/form.html -->
      <el-form
        label-width="auto"
        ref="dataFormRef"
        :model="form"
        :rules="rules"
      >
        <el-form-item label="模型关键字" prop="key">
          <el-input v-model="form.key" placeholder="请输入模型关键字"></el-input>
        </el-form-item>
        <el-form-item label="模型名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入模型名称"></el-input>
        </el-form-item>
        <el-form-item label="模型作者" prop="name">
          <el-input v-model="form.author" placeholder="请输入模型作者"></el-input>
        </el-form-item>
        <el-form-item label="模型类型" prop="category">
          <el-input v-model="form.category" placeholder="请输入模型类型"></el-input>
        </el-form-item>
        <el-form-item label="模型描述" prop="description">
          <el-input v-model="form.description" placeholder="请输入模型描述"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handlerCancel">取 消</el-button>
          <el-button type="primary" @click="handlerConfirm(dataFormRef)">确 定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import {getInfoById, save, updateById} from '@/api/process/model';
import {nextTick, onMounted, reactive, ref, watch} from 'vue';
import {ElMessage, FormInstance, FormRules} from 'element-plus';

// 父组件传值
const props = defineProps({
  // 新增/编辑表单是否显示
  visible: {
    type: Boolean
  }
});
// 表单参数
const form = reactive<ModelDTO>({
  id: '',
  key: '',
  name: '',
  author: '',
  category: '',
  description: ''
});
// 表单校验规则
const rules = reactive<FormRules<ModelDTO>>({
  key: [
    {required: true, message: '请输入模型关键字', trigger: 'blur'}
  ],
  name: [
    {required: true, message: '请输入模型名称', trigger: 'blur'}
  ]
});
// Vue 3 写法, 获取 ref 定义的组件实例
const dataFormRef = ref<FormInstance>();
// 利用 defineEmits 方法返回函数触发自定义事件, 不需要引入直接使用, 用于子组件与父组件通信
const emit = defineEmits(['refreshData', 'closeModel']);
// 在 vue3 中 el-dialog 把 visible 属性改为了 v-model, 这里子组件不能更改 props 里面的数据, 也不能使用 v-model 绑定
// 所以这里定义一个变量来和 v-model 绑定, 把接收到的 props 赋值
const visibleValue = ref(false);
// 挂载完毕后执行的回调函数
onMounted(() => {
  // 初始化 visible
  visibleValue.value = props.visible;
});
// 使用 watch 监听父组件传过来的值, 保证数据的同步更新
watch(() => props.visible, (newValue) => {
  visibleValue.value = newValue;
});

// 取消事件
const handlerCancel = () => {
  handleClose();
};

// 确认事件
const handlerConfirm = async (formEl: FormInstance | undefined) => {
  if (!formEl) return;
  await formEl.validate(async (valid, fields) => {
      if (valid) {
        let res;
        if (form.id) {
          res = await updateById(form.id, form);
        } else {
          res = await save(form);
        }
        if (res.code === 200 && res.data) {
          ElMessage({message: form.id ? '修改成功' : '新增成功', type: 'success'});
          // 搜索刷新列表
          emit('refreshData');
          // 关闭弹窗
          handleClose();
        } else {
          ElMessage({message: res.msg, type: 'error'});
        }
      } else {
        console.log('未通过字段校验:', fields);
      }
    }
  );
};

// 关闭弹窗
const handleClose = () => {
  emit('closeModel');
};

// 初始化表单
const init = (id?: string) => {
  // 在 DOM 更新后执行回调
  nextTick(async () => {
    if (!dataFormRef.value) return;
    // 重置数据
    dataFormRef.value.resetFields();
    // 传了 id 参数则为修改操作
    if (id) {
      const res = await getInfoById(id);
      if (res.code === 200 && res.data) {
        // 把原来的值覆盖
        Object.assign(form, {...res.data});
      }
    } else {
      form.id = undefined;
    }
  });
};

// 在 <script setup> 中, 所有定义的变量和函数默认是私有的, 不能从组件外部访问, 通过 defineExpose 显式指定暴露方法
defineExpose({
  init
});
</script>