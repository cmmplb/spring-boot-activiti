<template>
  <div class='model-container'>
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
          icon="Plus"
          type="primary"
          plain
          @click="handlerAdd"
        >
          新增
        </el-button>
        <el-button
          icon="Upload"
          type="primary"
          plain
          @click="handlerImport"
        >
          导入
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
                v-if="scope.row.designType === 1"
                icon="Document"
                @click="handlerDesign(scope.row)"
                text
                style="color: #ff9100"
              >设计(activiti-modeler)
              </el-button
              >
              <el-button
                v-if="scope.row.designType === 2"
                icon="Document"
                @click="handlerDesignBpmnJs(scope.row)"
                text
                style="color: rgb(62,0,128)"
              >设计(bpmn-js)
              </el-button
              >
              <el-button
                icon="Edit"
                @click="handlerDeployment(scope.row)"
                text
                style="color: rgb(87,163,26)"
              >部署
              </el-button
              >
              <el-button
                icon="Download"
                @click="handlerExport(scope.row)"
                text
                style="color: rgb(221,0,255)"
              >导出
              </el-button
              >
              <el-button
                icon="Edit"
                @click="handlerEdit(scope.row)"
                text
                style="color: #409eff"
              >编辑
              </el-button
              >
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

    <!-- 表单组件 -->
    <FormModel
      ref="formModelRef"
      :visible="visible"
      @closeModel="closeModel"
      @refreshData="getData"
    >
    </FormModel>

    <!-- 文件上传弹窗, 在 vue3 中 el-dialog 把 visible 属性改为了 v-model -->
    <el-dialog title="上传流程文件" v-model="uploadVisible" style="max-width: 500px;padding: 50px"
               :before-close="handleClose">
      <div>
        <span>模型设计类型: </span>
        <el-radio-group v-model="uploadForm.designType" v-for="option in uploadForm.options">
          <el-radio :key="option.value" :value="option.value">{{ option.label }}</el-radio>
        </el-radio-group>
      </div>
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
import {deployment, exportModel, getByPaged, importModel, removeById} from '@/api/process/model';
import Table from '@/components/table/index.vue';
import {onMounted, reactive, ref} from 'vue';
import {ElMessage, ElMessageBox, UploadInstance, UploadUserFile} from 'element-plus';
import {QueryPageBean} from '@/utils/http/axios/axios';
import FormModel from '@/views/process/model/form-model.vue';
import {AxiosResponse} from 'axios';
import {downFile, getRoutingModeBase} from '@/utils';
import Form from '@/components/form/index.vue';
import {FormItem} from '@/components/form/form';

// reactive 一般定义响应式数据, 例如数组、对象等, ref 一般定义基础类型数据, 例如字符串、数字等

// 表格列配置
const columns = reactive<Column[]>([
  {
    prop: 'name',
    label: '模型名称',
    width: 240
  },
  {
    prop: 'key',
    label: '模型关键字',
    width: 240
  },
  {
    prop: 'category',
    label: '模型类型',
    width: 240
  },
  {
    prop: 'designType',
    label: '设计类型',
    option: [
      {
        value: 1,
        label: 'activiti-modeler',
        tagType: 'success'
      },
      {
        value: 2,
        label: 'bpmn-js',
        tagType: 'warning'
      }
    ],
    width: 140
  },
  {
    prop: 'version',
    label: '版本',
    width: 100
  },
  {
    prop: 'description',
    label: '模型描述',
    width: 180,
    showOverflowTooltip: true
  },
  {
    prop: 'createTime',
    label: '创建时间'
  },
  {
    prop: 'lastUpdateTime',
    label: '最后更新时间'
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

// Vue 3 写法, 获取 ref 定义的组件实例
const formModelRef = ref();
// 新增/编辑表单是否显示
const visible = ref(false);
// 上传部署列表, { name: '...', url: '....' }
const fileList = ref<UploadUserFile[]>([]);
// 上传流程文件弹窗是否显示
const uploadVisible = ref(false);
// 导入表单
const uploadForm = reactive({
  designType: 1,
  options: [
    {
      label: 'activiti-modeler',
      value: 1
    },
    {
      label: 'bpmn-js',
      value: 2
    }
  ]
});
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

// 点击添加按钮
const handlerAdd = () => {
  visible.value = true;
  if (formModelRef.value) {
    formModelRef.value.init(undefined);
  }
};

// 点击导入按钮
const handlerImport = () => {
  uploadVisible.value = true;
  fileList.value = [];
};

// 关闭上传流程文件弹窗
const handleClose = () => {
  uploadVisible.value = false;
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
  const res = await importModel(uploadForm.designType, formData);
  if (res.code === 200 && res.data) {
    ElMessage({type: 'success', message: '导入成功'});
    // 刷新列表
    await getData();
    // 关闭弹窗
    handleClose();
  } else {
    ElMessage({type: 'error', message: res.msg});
  }
};

// 点击设计按钮
const handlerDesign = (row: ModelVO) => {
  // 跳转到模型设计页面
  window.open(getRoutingModeBase() + '/activiti-modeler?modelId=' + row.id, '_blank');
};

// 点击设计(bpmn-js)按钮
const handlerDesignBpmnJs = (row: ModelVO) => {
  // 跳转到模型设计页面
  window.open(getRoutingModeBase() + '/bpmn-js?modelId=' + row.id, '_blank');
};

// 点击部署按钮
const handlerDeployment = (row: ModelVO) => {
  ElMessageBox.confirm('是否确认部署名称为"' + row.name + '"的模型？', '部署', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success'
    }
  ).then(async () => {
    const res = await deployment(row.id);
    if (res.code === 200 && res.data) {
      await getData();
      ElMessage({type: 'success', message: '部署成功'});
    } else {
      ElMessage({type: 'error', message: res.msg});
    }
  }).catch(() => {
    // 取消部署
  });
};

// 点击导出按钮
const handlerExport = (row: ModelVO) => {
  exportModel(row.id).then((response: AxiosResponse) => {
    downFile(response, row.key + '.bpmn20.xml');
  });
};

// 点击编辑按钮
const handlerEdit = (row: ModelVO) => {
  visible.value = true;
  if (formModelRef.value) {
    formModelRef.value.init(row.id);
  }
};

// 点击删除按钮
const handlerDelete = (row: ModelVO) => {
  ElMessageBox.confirm('是否确认删除"' + row.name + '"的数据项？', '删除', {
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

// 关闭新增/编辑弹出层
const closeModel = () => {
  visible.value = false;
};
</script>

<style scoped lang='scss'>
.model-container {
  .content-card {
    margin-top: 20px;
  }

  .upload {
    margin-top: 20px;

    .el-upload__tip {
      margin-top: 15px;
      color: red;
    }
  }

}
</style>