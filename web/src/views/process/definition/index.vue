<template>
  <div class='definition-container'>
    <!-- 通过shadow属性设置卡片阴影出现的时机：always、hover或never -->
    <el-card shadow="always" class="search-card">
      <!-- 搜索表单区域 -->
      <!-- inline 属性可以让表单域变为行内的表单域 -->
      <el-form inline :model="searchForm" class="search-form">
        <el-row>
          <el-col :span="4">
            <el-form-item label="关键词">
              <el-input class="search-type-options" v-model="searchForm.keywords" clearable
                        placeholder="关键词"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="3">
            <el-form-item>
              <el-button type="primary" icon="Search" @click="handlerSearch">查 询</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <el-card shadow="always" class="content-card">
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
                icon="Outline"
                @click="handlerShow(scope.row)"
                text
                style="color: #ff5f00"
              >查看流程文件
              </el-button
              >
              <el-button
                icon="View"
                @click="handlerShowChart(scope.row)"
                text
                style="color: rgba(6,190,213,0.98)"
              >查看流程图
              </el-button
              >
              <el-button
                icon="View"
                @click="handlerShowChartBpmnJs(scope.row)"
                text
                style="color: rgb(128,105,105)"
              >查看流程图(bpmn-js)
              </el-button
              >
              <el-button
                icon="Flag"
                @click="handlerExchangeModel(scope.row)"
                text
                style="color: #b700ff"
              >转为模型
              </el-button
              >
              <el-button
                :icon="scope.row.suspended ? 'Play' : 'Pause'"
                @click="handlerSuspended(scope.row)"
                text
                :style="'color: ' + (scope.row.suspended ? 'rgb(68,179,3)' : 'blue')"
              >{{ scope.row.suspended ? '激活' : '挂起' }}
              </el-button
              >
            </template>
          </el-table-column>
        </template>
      </Table>
    </el-card>

    <!-- 流程文件预览弹窗 -->
    <el-dialog title="流程文件预览" v-model="processDefineVisible" width="85%" top="3vh" style="padding: 10px"
               :before-close="handlerCloseProcessDefineVisible">
      <pre>
        <code class="hljs" v-html="highlightedCode()"></code>
      </pre>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="handlerCloseProcessDefineVisible">关 闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 流程图预览 -->
    <el-dialog title="流程图预览" v-model="processChartVisible"
               :before-close="handlerCloseProcessChart">
      <template v-if="showBpmnProcess">
        <!-- 画布 -->
        <div id="canvas" ref="canvasRef"></div>
      </template>
      <template v-else>
        <img :src="img">
      </template>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="handlerCloseProcessChart">关 闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 挂起/激活弹窗 -->
    <el-dialog :title="suspended ? '激活' : '挂起'" v-model="suspendedVisible" style="max-width: 500px;padding: 50px"
               :before-close="handlerCloseSuspended">
      <el-form
        label-width="auto"
        ref="dataFormRef"
        :model="form"
        :rules="rules"
      >
        <el-form-item :label="(suspended ? '激活' : '挂起') + '关联流程实例'" prop="activateProcessInstances">
          <el-switch v-model="form.activateProcessInstances"/>
        </el-form-item>
        <el-form-item :label="'定时' + (suspended ? '激活' : '挂起') + '时间'" prop="activationDate">
          <el-date-picker v-model="form.activationDate" type="datetime"
                          :placeholder="'请选择定时'+ (suspended ? '激活' : '挂起') + '时间'"
                          value-format="yyyy-MM-DD HH:mm:ss"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handlerCloseSuspended">取 消</el-button>
          <el-button type="primary" @click="handlerConfirmSuspended(dataFormRef)">确 定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">

import Table from '@/components/table/index.vue';
import {markRaw, onMounted, reactive, ref} from 'vue';
import {QueryPageBean} from '@/utils/http/axios/axios';
import {
  activate,
  exchangeToModel,
  getByPaged,
  show,
  showChart,
  showChartBpmnJs,
  suspend
} from '@/api/process/definition.ts';
// 设计器, bpmn-js有两种模式: Modeler 模式和 Viewer 模式, 在 Modeler 模式下可以对流程图进行编辑, 而 Viewer 模式则不能, 仅作为展示用
import ViewerModeler from 'bpmn-js/lib/Viewer';

import {ElMessage, ElMessageBox, FormInstance, FormRules} from 'element-plus';
import hljs from 'highlight.js';
// 加载默认主题
// import 'highlight.js/styles/default.css';
// dark
// import 'highlight.js/styles/github-dark.css';
// 高亮主题, 还有一些其他主题都在 highlight.js/styles 目录下, 可以自己切换
import 'highlight.js/styles/intellij-light.css';

// reactive 一般定义响应式数据, 例如数组、对象等, ref 一般定义基础类型数据, 例如字符串、数字等

// 表格列配置
const columns = reactive<Column[]>([
  {
    prop: 'name',
    label: '部署名称',
    width: 140
  },
  {
    prop: 'key',
    label: '关键字'
  },
  {
    prop: 'category',
    label: '类型'
  },
  {
    prop: 'version',
    label: '版本',
    width: 100
  },
  {
    prop: 'resourceName',
    label: '资源路径'
  },
  {
    prop: 'diagramResourceName',
    label: '图片资源文件名称',
    width: 180
  },
  {
    prop: 'suspended',
    label: '状态',
    width: 180,
    option: [
      {
        value: false,
        label: '激活',
        tagType: 'success'
      },
      {
        value: true,
        label: '挂起',
        tagType: 'warning'
      }
    ]
  },
  {
    prop: 'appVersion',
    label: '自定义应用版本号',
    width: 120
  }
]);
// Vue 3 写法, 获取 ref 定义的组件实例
const dataFormRef = ref<FormInstance>();
// 表单参数
const form = reactive<SuspendDefinitionDTO>({
  id: '',
  activateProcessInstances: true,
  activationDate: ''
});
// 表单校验规则
const rules = reactive<FormRules<SuspendDefinitionDTO>>({
  activateProcessInstances: [
    {required: true, message: '请选择挂起关联流程实例', trigger: 'blur'}
  ]
});
// 表格数据集
const data = ref([]);
// 搜索表单
const searchForm = reactive<QueryPageBean>({
  keywords: ''
});
// 分页参数
const paginationData = reactive<Pagination>({
  size: 10,
  current: 1,
  total: 0
});
// 流程文件预览弹窗是否显示
const processDefineVisible = ref(false);
// 流程图预览弹窗是否显示
const processChartVisible = ref(false);
// 挂起/激活弹窗是否显示
const suspendedVisible = ref(false);
// 是否显示 bpmn-js 流程图
const showBpmnProcess = ref(false);
// activiti-modeler 流程图片
const img = ref();
// 流程文件内容
const text = ref('');
// 挂起/激活状态
const suspended = ref(false);
// bpmn-js 流程图实例
const bpmnModeler = ref();

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
const paginationChange = (data: ProcessDefinitionVO) => {
  // 把原来的值覆盖
  Object.assign(paginationData, {...paginationData, ...data});
  getData();
};

// 点击查看流程文件按钮
const handlerShow = async (row: ProcessDefinitionVO) => {
  processDefineVisible.value = true;
  const res = await show(row.deploymentId, {resourceName: row.resourceName});
  if (res.code === 200 && res.data) {
    text.value = res.data;
  } else {
    ElMessage({message: res.msg, type: 'error'});
  }
};

/** 高亮显示 */
const highlightedCode = () => {
  const result = hljs.highlight(text.value, {language: 'xml', ignoreIllegals: true});
  return result.value || '&nbsp;';
};

// 点击查看流程图
const handlerShowChart = async (row: ProcessDefinitionVO) => {
  // 设置是否显示 bpmn-js 流程图为 false
  showBpmnProcess.value = false;
  // 显示流程图弹窗
  processChartVisible.value = true;
  // 转换流响应到弹窗
  const res = await showChart(row.id);
  if (typeof window !== 'undefined' && window.URL) {
    // 将 blob 对象 生成 BlobURL
    img.value = window.URL.createObjectURL(res);
  }
};

// Vue 3 写法, 获取 ref 定义的组件实例
const canvasRef = ref();

// 点击查看流程图(bpmn-js)
const handlerShowChartBpmnJs = async (row: ProcessDefinitionVO) => {
  // 设置是否显示 bpmn-js 流程图为 true
  showBpmnProcess.value = true;
  // 显示流程图弹窗
  processChartVisible.value = true;
  if (bpmnModeler.value) {
    // 如果存在就销毁重新创建，否则会不停添加流程
    bpmnModeler.value.destroy();
  }

  const res = await showChartBpmnJs(row.id);
  if (res.code === 200 && res.data) {
    // toRaw 方法可以将一个被 reactive 包裹的对象还原为其中的原始对象，从而使其不再具有任何响应式能力。
    // markRaw 方法可以将一个对象标记为非响应式，从而使其不会被 reactive 包裹，也就不会成为 Vue3 中的响应式对象。
    // BpmnViewer 预览、BpmnModeler 操作
    bpmnModeler.value = markRaw(new ViewerModeler({
      container: canvasRef.value
    }));
    await bpmnModeler.value.importXML(res.data);

    const canvas = bpmnModeler.value.get('canvas');
    // 使流程图自适应屏幕
    canvas.zoom('fit-viewport', 'auto');
    canvas.zoom(0.8); //缩写至0.8倍
  }
};

// 点击转为模型按钮
const handlerExchangeModel = async (row: ProcessDefinitionVO) => {
  // 给个默认值 1
  let designType: number = 1;
  await ElMessageBox.confirm('是否确认转换名称为"' + row.name + '"的流程定义为模型？', '转换模型', {
      confirmButtonText: '转换为 bpmn-js',
      cancelButtonText: '转换为 activiti-modeler',
      type: 'success'
    }
  ).then(async () => {
    // 转换为 bpmn-js
    designType = 2;
  }).catch(() => {
    // 转换为 activiti-modeler
    designType = 1;
  });
  console.log('designType:', designType);
  const res = await exchangeToModel(row.id, designType);
  if (res.code === 200 && res.data) {
    await getData();
    ElMessage({type: 'success', message: '转换成功'});
  } else {
    ElMessage({type: 'error', message: res.msg});
  }
};

// 点击激活/挂起按钮
const handlerSuspended = async (row: ProcessDefinitionVO) => {
  form.id = row.id;
  suspended.value = row.suspended;
  suspendedVisible.value = true;
};

// 关闭流程文件预览弹窗
const handlerCloseProcessDefineVisible = () => {
  processDefineVisible.value = false;
};

// 关闭流程图预览弹窗
const handlerCloseProcessChart = () => {
  processChartVisible.value = false;
};

// 关闭挂起/激活弹窗
const handlerCloseSuspended = () => {
  suspendedVisible.value = false;
};

// 确定挂起/激活
const handlerConfirmSuspended = async (formEl: FormInstance | undefined) => {
  if (!formEl) return;
  await formEl.validate(async (valid, fields) => {
      if (valid) {
        let res;
        console.log('suspended:', suspended.value);
        if (suspended.value) {
          // 设置是否挂起为 false
          suspended.value = false;
          res = await activate(form.id, form);
        } else {
          // 设置是否挂起为 false
          suspended.value = true;
          res = await suspend(form.id, form);
        }
        if (res.code === 200 && res.data) {
          ElMessage({message: suspended.value ? '挂起成功' : '激活成功', type: 'success'});
          // 搜索刷新列表
          await getData();
          // 关闭弹窗
          handlerCloseSuspended();
        } else {
          ElMessage({message: res.msg, type: 'error'});
        }
      } else {
        console.log('未通过字段校验:', fields);
      }
    }
  );
};


</script>

<style scoped lang='scss'>

</style>