<template>
  <div class='model-container'>
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
      <!-- 新增按钮区域 -->
      <el-form inline>
        <el-form-item>
          <el-button
            icon="Plus"
            type="primary"
            plain
            @click="handlerAdd"
          >
            新增
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 表格区域 -->
      <Table
        :columns="columns"
        :data="data"
        showPagination
        @pagination="paginationChange"
        :paginationData="paginationData"
      >
        <template #buttons>
          <el-table-column fixed="right" align="center" label="操作" min-width="120" width="220">
            <template #default="scope">
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
  </div>
</template>

<script setup lang="ts">
import {getByPaged, removeById} from '@/api/process/model';
import Table from '@/components/table/index.vue';
import {onMounted, reactive, ref} from 'vue';
import {ElMessage, ElMessageBox} from 'element-plus';
import {QueryPageBean} from '@/utils/http/axios/axios';
import FormModel from '@/views/process/model/form-model.vue';

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
    prop: 'version',
    label: '版本',
    width: 180
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
// 新增/编辑表单是否显示
const visible = ref(false);

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
  Object.assign(paginationData, {...paginationData, ...data});
  getData();
};

// Vue 3 写法, 获取 ref 定义的组件实例
const formModelRef = ref();

// 点击添加按钮
const handlerAdd = () => {
  visible.value = true;
  if (formModelRef.value) {
    formModelRef.value.init(undefined);
  }
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
  ElMessageBox.confirm('是否确认删除"' + row.id + '"的数据项？', '删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    return removeById(row.id);
  }).then(() => {
    getData();
    ElMessage({type: 'success', message: '删除成功'});
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

}


</style>