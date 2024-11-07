<template>
  <!--
      https://element-plus.org/zh-CN/component/table.html
      table常用的属性：
      参数	                  说明	                   类型	              可选值	              默认值
      data	            显示的数据	                array	                —	                  —
      stripe	          是否为斑马纹table	          boolean	              —	                false
      border	          是否带有纵向边框	            boolean	              —	                false
      size	            Table 的尺寸	              string    	  medium / small / mini	      —

      Table Events
      事件名	                          说明	                                            参数
      select	        当用户手动勾选数据行的 Checkbox 时触发的事件	                      selection, row
      select-all	    当用户手动勾选全选 Checkbox 时触发的事件	                          selection
      selection-change	    当选择项发生变化时会触发该事件	                              selection


      Table-column Attributes:
      resizable               对应列是否可以通过拖动改变宽度              boolean            —	                  true
      show-overflow-tooltip   当内容过长被隐藏时显示 tooltip            boolean	           —	                  false
  -->
  <div class='table-container'>
    <el-table
      :data="data"
      :stripe="stripe"
      :border="border"
      v-loading="loading"
      :size="size"
      :row-key="rowKey"
      :tree-props="treeProps"
      :default-expand-all="defaultExpandAll"
      @select="handleSelect"
      @select-all="handleSelectAll"
      @cell-click="handleCellClick"
      style="width: 100%"
    >
      <el-table-column
        v-if="showSelection"
        type="selection"
        :resizable="false"
        width="55"
      ></el-table-column>
      <el-table-column
        label="序号"
        v-if="showIndex"
        type="index"
        width="50"
        :resizable="false"
        :align="center ? 'center' : 'left'"
      ></el-table-column>
      <template v-for="column in columns">
        <el-table-column
          :column-key="column.prop"
          :prop="column.prop"
          :label="column.label"
          :width="column.width"
          :show-overflow-tooltip="column.showOverflowTooltip"
          :align="center ? 'center' : 'left'"
        >
          <template #default="scope">
            <!--
              https://www.jb51.net/article/259221.htm
              这里做了一个自定义枚举转换
            -->
            <div v-if="column.switch">
              <!-- :disabled="scope.row[column.switch.prop]===column.switch.activeValue" -->
              <el-switch v-model="scope.row[column.prop]"
                         :disabled="column.switch.disabled && scope.row[column.switch.disabled] === column.switch.disabledValue"
                         :active-value="column.switch.activeValue"
                         :inactive-value="column.switch.inactiveValue"
                         @change="handleSwitchChange(scope.row)"/>
            </div>
            <div v-else-if="column.icon">
              <i :class="scope.row.icon"/>
            </div>
            <div v-else-if="column.option">
              <template v-for="ele in column.option">
                <template v-if="scope.row[scope.column.property] === ele.value">
                  <!-- 是否启用tag -->
                  <template v-if="ele.tagType">
                    <el-tag :type="ele.tagType" size="small">
                      <span v-html="formatter(scope.row, scope.column, column.option)"/>
                    </el-tag>
                  </template>
                  <template v-else>
                    <span v-html="formatter(scope.row, scope.column, column.option)"/>
                  </template>
                </template>
              </template>
            </div>
            <span v-else>
              {{ scope.row[scope.column.property] }}
            </span>
          </template>
        </el-table-column>
      </template>

      <!-- **注意**: Vue3 中使用具名插槽需要使用 template 进行包裹起来 -->
      <!-- 操作插槽, 具名插槽: 即 <slot> 元素上使用 name 属性用来标识插槽, , 还有默认插槽和作用域插槽 -->
      <slot name="buttons"></slot>
    </el-table>

    <Pagination
      v-if="showPagination && paginationData.total > 0"
      :total="paginationData.total"
      :size="paginationData.size"
      :current="paginationData.current"
      @pagination="handlePagination"
    />
  </div>
</template>

<script setup lang="ts">

import Pagination from '@/components/pagination/index.vue';
import {PropType} from 'vue';

const emit = defineEmits(['handleCellClick', 'handleSwitchChange', 'pagination']);

const props = defineProps({
  // table名称
  tableName: {
    type: String,
    default: () => {
      return 'table';
    }
  },
  // 表格列配置
  columns: {
    type: Array<Column>,
    default: () => {
      return [];
    }
  },
  // 表格列表
  data: {
    type: Array,
    default: () => {
      return [];
    }
  },
  // 行数据的Key
  rowKey: {
    type: String,
    default: () => {
      return '';
    }
  },
  // 渲染嵌套数据的配置选项
  treeProps: {
    type: Object,
    default: () => {
      return {
        children: 'children',
        hasChildren: 'hasChildren'
      };
    }
  },
  // 是否默认展开所有行, 当Table包含展开行存在或者为树形表格时有效
  defaultExpandAll: {
    type: Boolean,
    default: () => {
      return false;
    }
  },
  // 是否为斑马纹table
  stripe: {
    type: Boolean,
    default: () => {
      return true;
    }
  },
  // 是否带有纵向边框
  border: {
    type: Boolean,
    default: () => {
      return true;
    }
  },
  // 是否显示序号
  showIndex: {
    type: Boolean,
    default: () => {
      return true;
    }
  },
  // 是否居中
  center: {
    type: Boolean,
    default: () => true
  },
  // 是否加载中
  loading: {
    type: Boolean,
    default: () => false
  },
  // 表格大小=>medium / small / mini
  size: {
    type: String,
    default: () => 'small'
  },
  // 是否显示多选框
  showSelection: {
    type: Boolean,
    default: () => false
  },
  // 当前多选的行数据
  checkedData: {
    type: Array,
    default: () => {
      return [];
    }
  },
  // 是否显示分页
  showPagination: {
    type: Boolean,
    default: () => {
      return false;
    }
  },
  // 分页配置
  paginationData: {
    type: Object as PropType<Pagination>,
    default: () => {
      return {
        total: 0,
        current: 1,
        size: 10
      };
    }
  }
});
// 当用户手动勾选数据行的 Checkbox 时触发的事件
const handleSelect = (selection: any, row?: any) => {
  console.log('selection:', selection);
  console.log('row:', row);
  props.checkedData.splice(0, props.checkedData.length);
  for (let key in selection) {
    props.checkedData.push(selection[key]);
  }
};
// 当用户手动勾选全选 Checkbox 时触发的事件
const handleSelectAll = (selection: any) => {
  handleSelect(selection);
};

// 当某个单元格被点击时会触发该事件
const handleCellClick = (row: any, column: any, cell: any, event: PointerEvent) => {
  emit('handleCellClick', row, column, cell, event);
};

// switch 状态发生变化时的回调函数
const handleSwitchChange = (row: any) => {
  // this.$set(row, prop, row[prop] === 0 ? 1 : 0);
  emit('handleSwitchChange', row);
};

/**
 * 转换枚举值
 * @param row 当前行数据
 * @param column 列配置
 * @param option 枚举字典
 * @returns 转换枚举的结果
 */
const formatter = (row: any, column: any, option: any) => {
  let value = row[column.property];
  if (value == null || value === '' || value === undefined) {
    return value;
  } else {
    let result = option.filter((ele: any) => ele.value === value);
    return (result && result.length > 0) ? result[0].label : value;
  }
};
// 页码切换
const handlePagination = (data: Pagination) => {
  emit('pagination', data);
};
</script>