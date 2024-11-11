<template>
  <div class='deployment-container'>
    <!-- 通过shadow属性设置卡片阴影出现的时机：always、hover或never -->
    <el-card shadow="always" class="search-card">
      <!-- 搜索表单区域 -->
      <Form
        inline
        :form="searchForm"
        :formItems="searchFormItems"
      >
        <template #buttons>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="handlerSearch">查 询</el-button>
          </el-form-item>
        </template>
      </Form>
    </el-card>

    <el-card shadow="always" class="content-card">
      <!-- 新增按钮区域 -->
      <el-row class="content-card-row">
        <el-button
          icon="Upload"
          type="primary"
          plain
          @click="handlerUpload"
        >
          上传流程文件
        </el-button>
      </el-row>

      <!-- 表格区域 -->
      <Table
        :columns="columns"
        :data="data"
        showPagination
        @pagination="paginationChange"
        :paginationData="paginationData"
      >
        <template #buttons>
          <el-table-column fixed="right" align="center" label="操作" min-width="120" width="240">
            <template #default="scope">
              <el-button
                icon="Delete"
                @click="handlerDelete(scope.row)"
                text
                style="color: red"
              >删除
              </el-button
              >
            </template>
          </el-table-column>
        </template>
      </Table>
    </el-card>

    <!-- 文件上传弹窗, 在 vue3 中 el-dialog 把 visible 属性改为了 v-model -->
    <el-dialog title="上传流程文件" v-model="visible" style="max-width: 500px;padding: 50px"
               :before-close="handleClose">
      <!--
        上传组件: https://element-plus.org/zh-CN/component/upload.html
        drag-是否启用拖拽上传-boolean-false; multiple-是否支持多选文件-boolean-false; auto-upload-是否在文件选择后立即进行上传-boolean-true;
        :http-request-覆盖默认的 Xhr 行为，自定义实现上传文件的请求事件, 添加处理点击确认才调用上传
        vue3 更换了 :file-list 为 v-model:file-list
       -->
      <el-upload ref="uploadRef" class="upload" drag multiple :auto-upload="false" v-model:file-list="fileList">
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将流程文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip" slot="tip">提示: 仅允许导入 "bpmn", "xml" 或 "zip" 格式文件！</div>
      </el-upload>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleClose">取 消</el-button>
          <el-button type="primary" @click="handlerConfirm">确 定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">

import Table from '@/components/table/index.vue';
import {onMounted, reactive, ref} from 'vue';
import {QueryPageBean} from '@/utils/http/axios/axios';
import {getByPaged, removeById, upload} from '@/api/process/deployment.ts';
import {ElMessage, ElMessageBox, UploadInstance, UploadUserFile} from 'element-plus';
import Form from '@/components/form/index.vue';
import {FormItem} from '@/components/form/form';

// reactive 一般定义响应式数据, 例如数组、对象等, ref 一般定义基础类型数据, 例如字符串、数字等

// 表格列配置
const columns = reactive<Column[]>([
  {
    prop: 'name',
    label: '部署名称',
    width: 300
  },
  {
    prop: 'key',
    label: '关键字',
    width: 500
  },
  {
    prop: 'category',
    label: '类型',
    width: 300
  },
  {
    prop: 'version',
    label: '版本',
    width: 140
  },
  {
    prop: 'deploymentTime',
    label: '部署时间'
  }

]);
// 表格数据集
const data = ref([]);
// 分页参数
const paginationData = reactive<Pagination>({
  size: 10,
  current: 1,
  total: 0
});
// 搜索表单
const searchForm = reactive<QueryPageBean>({
  keywords: ''
});
// 搜索表单项
const searchFormItems = reactive<FormItem[]>([
  {
    prop: 'keywords',
    label: '关键词',
    placeholder: '关键词'
  }
]);

// 上传部署列表, { name: '...', url: '....' }
const fileList = ref<UploadUserFile[]>([]);
// 上传流程文件弹窗是否显示
const visible = ref(false);
// Vue 3 写法, 获取 ref 定义的组件实例
const uploadRef = ref<UploadInstance>();

// 挂载完毕后执行的回调函数
onMounted(() => {
  getData();
});

// 点击搜索按钮
const handlerSearch = () => {
  getData();
};

// 获取表格数据
const getData = async () => {
  const res = await getByPaged({...searchForm, ...paginationData});
  if (res.code === 200 && res.data) {
    data.value = res.data.rows;
    paginationData.total = res.data.total;
  } else {
    ElMessage({message: res.msg, type: 'error'});
  }
};

// 分页改变调用的事件
const paginationChange = (data: any) => {
  // 把原来的值覆盖
  Object.assign(paginationData, paginationData, data);
  getData();
};

// 点击上传流程文件按钮
const handlerUpload = () => {
  visible.value = true;
  fileList.value = [];
};

// 点击删除按钮
const handlerDelete = (row: ModelVO) => {
  ElMessageBox.confirm('是否确认删除"' + row.id + '"的数据项？', '删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    const res = await removeById(row.id);
    if (res.code === 200 && res.data) {
      await getData();
      ElMessage({type: 'success', message: '删除成功'});
    } else {
      ElMessage({type: 'error', message: res.msg});
    }
  }).catch(() => {
    // 取消删除
  });
};

// 关闭上传流程文件弹窗
const handleClose = () => {
  visible.value = false;
};

// 确定上传
const handlerConfirm = async () => {
  // 手动提交上传，会调用http-request事件
  // uploadRef.value!.submit();
  let formData = new FormData();
  // 这里上传文件我之前写的是 formData.append('files', ele); 控制台查看参数是 files: [object Object]
  // 需要把上传的文件转换成二进制对象, Blob/file.raw(.raw 就是上传的 binary), 参数: files: （二进制）或者 files: (binary) 才是正确的
  fileList.value.forEach(ele => {
    // 注意要添加 .raw
    formData.append('files', ele.raw!);
    // 或者使用 blob
    // formData.append('files', new Blob([ele.raw!]));
  });
  const res = await upload(formData);
  if (res.code === 200 && res.data) {
    ElMessage({type: 'success', message: '上传成功'});
    // 刷新列表
    await getData();
    // 关闭弹窗
    handleClose();
  } else {
    ElMessage({type: 'error', message: res.msg});
  }
};
</script>

<style scoped lang='scss'>
.deployment-container {
  .upload {
    .el-upload__tip {
      margin-top: 15px;
      color: red;
    }
  }
}

</style>