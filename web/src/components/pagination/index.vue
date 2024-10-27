<template>
  <!--
    https://element-plus.org/zh-CN/component/pagination.html
    常用属性：
    参数	                      说明	                            类型	                可选值	                            默认值
    background	      是否为分页按钮添加背景色	                 boolean                 —	                            false
    current-page	    当前页数，支持 .sync 修饰符	              number	                —	                              1
    page-size	        每页显示条目个数，支持 .sync 修饰符	        number	                —	                             10
    layout	          组件布局，子组件名用逗号分隔                	String	      sizes, prev, pager,           'prev, pager, next, jumper, ->, total'
                                                                           next, jumper, ->, total, slot
    page-sizes	      每页显示个数选择器的选项设置	                number[]	              —	                    [10, 20, 30, 40, 50, 100]
    total	            总条目数	                                number	                —	                              —

    事件：
    事件名称	                  说明	                                回调参数
    size-change	          size 改变时会触发	                      每页条数
    current-change	      current 改变时会触发	                  当前页
    prev-click	          用户点击上一页按钮改变当前页后触发	            当前页
    next-click	          用户点击下一页按钮改变当前页后触发	            当前页
   -->
  <div :class="{ hidden: hidden }" class='pagination-container'>
    <el-pagination
      :background="background"
      :current-page.sync="props.current"
      :page-size.sync="props.size"
      :layout="layout"
      :page-sizes="props.pageSizes!"
      :total="total"
      v-bind="$attrs"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    >
    </el-pagination>
  </div>
</template>

<script setup lang="ts">

const emit = defineEmits(['update:current', 'update:size', 'pagination']);

const props = defineProps({
  // 总条目数
  total: {
    required: true,
    type: Number
  },
  // 当前页码
  current: {
    type: Number,
    default: 1
  },
  // 每页条数
  size: {
    type: Number,
    default: 20
  },
  // 是否隐藏
  hidden: {
    type: Boolean,
    default: false
  },
  // 是否为分页按钮添加背景色
  background: {
    type: Boolean,
    default: () => {
      return false;
    }
  },
  // 组件布局，子组件名用逗号分隔
  layout: {
    type: String,
    default: 'total, sizes, prev, pager, next, jumper'
  },
  // 每页显示个数选择器的选项设置
  pageSizes: {
    type: Array,
    default() {
      return [1, 5, 10, 20, 30, 50];
    }
  },
  // 是否自动滚动
  autoScroll: {
    type: Boolean,
    default: true
  }
});

/**
 * size 改变时会触发
 * @param size 每页条数
 */
const handleSizeChange = (size: number) => {
  emit('pagination', {current: props.current, size: size});
  if (props.autoScroll) {
    scrollTo(0, 800);
  }
};

/**
 * currentPage 改变时会触发
 * @param current 当前页
 */
const handleCurrentChange = (current: number) => {
  emit('pagination', {current: current, size: props.size});
  if (props.autoScroll) {
    scrollTo(0, 800);
  }
};
</script>

<style scoped lang='scss'>
.pagination-container {
  position: relative;
  height: 25px;
  margin-bottom: 10px;
  margin-top: 15px;
  padding: 10px 20px !important;

  .el-pagination {
    position: absolute;
    right: 10px;
  }
}

.pagination-container.hidden {
  display: none;
}

</style>