<template>
  <div class='form-container'>
    <el-dialog :title="(dataForm.id? '编辑': '新增')+'模型'" v-model="visibleValue"
               style="max-width: 500px;padding: 50px"
               :before-close="handleClose">
      <Form
        ref="dataFormRef"
        :form="dataForm"
        :formItems="dataFormItems"
        :rules="rules"
      >
      </Form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handlerCancel">取 消</el-button>
          <el-button type="primary" @click="handlerConfirm">确 定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import {getInfoById, save, updateById} from '@/api/process/model';
import {nextTick, onMounted, reactive, ref, watch} from 'vue';
import {ElMessage, FormRules} from 'element-plus';
import Form from '@/components/form/index.vue';
import {FormItem} from '@/components/form/form';

// 父组件传值
const props = defineProps({
  // 新增/编辑表单是否显示
  visible: {
    type: Boolean
  }
});
// Vue 3 写法, 获取 ref 定义的组件实例
const dataFormRef = ref();
// 表单参数
const dataForm = reactive<ModelDTO>({
  id: '',
  key: '',
  name: '',
  author: '',
  category: '',
  description: '',
  designType: 1
});
// 表单项
const dataFormItems = reactive<FormItem[]>([
  {
    prop: 'name',
    label: '模型名称',
    placeholder: '请输入模型名称'
  },
  {
    prop: 'key',
    label: '模型关键字',
    placeholder: '请输入模型关键字'
  },
  {
    prop: 'category',
    label: '模型类型',
    placeholder: '请输入模型类型'
  },
  {
    prop: 'designType',
    label: '模型设计类型',
    type: 'radio',
    options: [
      {
        label: 'activiti-modeler',
        value: 1
      },
      {
        label: 'bpmn-js',
        value: 2
      }
    ],
    placeholder: 'designType'
  },
  {
    prop: 'author',
    label: '模型作者',
    // 只有 activiti-modeler 才需要填写作者，bpmn-js 没有保存作者的属性
    conditions: [
      {
        prop: 'designType',
        value: 1
      }
    ],
    placeholder: '请输入模型作者'
  },
  {
    prop: 'description',
    label: '模型描述',
    type: 'textarea',
    placeholder: '请输入模型描述'
  }
]);
// 表单校验规则
const rules = reactive<FormRules<ModelDTO>>({
  key: [
    {required: true, message: '请输入模型关键字', trigger: 'blur'},
    {min: 1, max: 255, message: '模型关键字长度限制 255', trigger: 'blur'}
  ],
  name: [
    {required: true, message: '请输入模型名称', trigger: 'blur'},
    {min: 1, max: 255, message: '模型名称长度限制 255', trigger: 'blur'}
  ],
  category: [
    {min: 1, max: 255, message: '模型类型长度限制 255', trigger: 'blur'}
  ],
  author: [
    {min: 1, max: 255, message: '模型作者长度限制 64', trigger: 'blur'}
  ],
  // META_INFO_ 长度 4000
  description: [
    {min: 1, max: 3000, message: '模型类型长度限制 3000', trigger: 'blur'}
  ]
});
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
const handlerConfirm = async () => {
  if (dataFormRef.value) {
    const valid = await dataFormRef.value.validate();
    if (valid) {
      let res;
      if (dataForm.id) {
        res = await updateById(dataForm.id, dataForm);
      } else {
        res = await save(dataForm);
      }
      if (res.code === 200 && res.data) {
        ElMessage({message: dataForm.id ? '修改成功' : '新增成功', type: 'success'});
        // 搜索刷新列表
        emit('refreshData');
        // 关闭弹窗
        handleClose();
      } else {
        ElMessage({message: res.msg, type: 'error'});
      }
    }
  }
};

// 关闭弹窗
const handleClose = () => {
  emit('closeModel');
};

// 初始化表单
const init = (id?: string) => {
  nextTick(async () => {
    if (dataFormRef.value) {
      await dataFormRef.value.init();
      // 传了 id 参数则为修改操作
      if (id) {
        const res = await getInfoById(id);
        if (res.code === 200 && res.data) {
          // 把原来的值覆盖
          Object.assign(dataForm, {...res.data});
        }
      } else {
        dataForm.id = undefined;
      }
    }
  });
};

// 在 <script setup> 中, 所有定义的变量和函数默认是私有的, 不能从组件外部访问, 通过 defineExpose 显式指定暴露方法
defineExpose({
  init
});
</script>