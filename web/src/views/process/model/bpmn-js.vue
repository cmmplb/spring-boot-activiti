<template>
  <div class='bpmn-js-container'>
    <div class="bpmn-js-wrapper">
      <!-- 画布 -->
      <div class="canvas" ref="canvasRef" id="canvas"/>

      <!-- 属性工具栏 -->
      <div id="properties-panel"></div>

      <!-- 操作 -->
      <div class="operate">
        <el-button class="operate-item" size="small" icon="DocumentAdd" @click="handlerSave(false)">
          保存
        </el-button>
        <el-button class="operate-item" size="small" icon="DocumentAdd" @click="handlerSave(true)">
          保存并关闭
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {markRaw, onMounted, ref} from 'vue';
// 设计器, bpmn-js有两种模式: Modeler 模式和 Viewer 模式, 在 Modeler 模式下可以对流程图进行编辑, 而 Viewer 模式则不能, 仅作为展示用
import BpmnModeler from 'bpmn-js/lib/Modeler';
// 左侧工具栏以及选中节点的样式,
import 'bpmn-js/dist/assets/diagram-js.css';
// 这个不知道啥效果, 看样式是类名有 .bjs-container .bjs-breadcrumbs, 内容和面包屑样式? 后面看看会不会找到用的地方
import 'bpmn-js/dist/assets/bpmn-js.css';
// 左侧工具栏字体图标样式
import 'bpmn-js/dist/assets/bpmn-font/css/bpmn.css';
// 这个是一些 unicode 码, 生僻字不知道干啥的 0.0
import 'bpmn-js/dist/assets/bpmn-font/css/bpmn-codes.css';
// 这个比 bpmn.css  多了两个 src base64
import 'bpmn-js/dist/assets/bpmn-font/css/bpmn-embedded.css';

import {
  BpmnPropertiesPanelModule,
  BpmnPropertiesProviderModule,
  CamundaPlatformPropertiesProviderModule
} from 'bpmn-js-properties-panel';
// 扩展属性
import CamundaModdleDescriptor from 'camunda-bpmn-moddle/resources/camunda';

// 网上的教程都是引入这个, 不过版本都是 0.+ 的, 我这里安装的版本是 "bpmn-js-properties-panel": "^5.25.0", 对应的目录没有这个文件
// import 'bpmn-js-properties-panel/dist/assets/bpmn-js-properties-panel.css';
// 看他源码的 package.json 里面导入了 "@bpmn-io/properties-panel": "^3.24.1", 我就找到了下面这个 css, 发现可以渲染属性栏样式
import '@bpmn-io/properties-panel/dist/assets/properties-panel.css';

// 小地图
import minimapModule from 'diagram-js-minimap';
import 'diagram-js-minimap/assets/diagram-js-minimap.css';
import {translate} from '@/utils/translate.ts';
import {useRouter} from 'vue-router';
import {getBpmnInfoById, save} from '@/api/process/bpmnJs.ts';
import {ElMessage} from 'element-plus';

// 挂载画布
const canvasRef = ref();
// 设计器实例
const bpmnModeler = ref();

const router = useRouter();

onMounted(() => {
  // 初始化
  init();
  // 预览
  preview();
});

// 初始化实例
const init = () => {
  // toRaw 方法可以将一个被 reactive 包裹的对象还原为其中的原始对象，从而使其不再具有任何响应式能力。
  // markRaw 方法可以将一个对象标记为非响应式，从而使其不会被 reactive 包裹，也就不会成为 Vue3 中的响应式对象。
  // BpmnViewer 预览、BpmnModeler 操作
  bpmnModeler.value = markRaw(new BpmnModeler({
    // 画布
    container: canvasRef.value,

    // 属性工具栏
    propertiesPanel: {
      parent: '#properties-panel'
    },

    // 添加插件
    additionalModules: [
      // 右侧属性栏属性标题
      BpmnPropertiesPanelModule,
      // 右侧属性栏基础信息, id/name/version
      BpmnPropertiesProviderModule,
      // 右侧属性栏扩展信息
      CamundaPlatformPropertiesProviderModule,
      minimapModule,
      {
        // 汉化
        translate: ['value', translate]
        // 禁用滚轮滚动
        // zoomScroll: ["value", ""],
        // 禁止拖动线
        // bendpoints: ["value", ""],
        // 禁用左侧面板
        // paletteProvider: ["value", ""],
        // 禁止点击节点出现contextPad
        // contextPadProvider: ["value", ""],
        // 禁止双击节点出现label编辑框
        // labelEditingProvider: ["value", ""]
      }
    ],
    // 扩展属性
    moddleExtensions: {
      camunda: CamundaModdleDescriptor
    }
  }));
  const canvas = bpmnModeler.value.get('canvas');
  // 设置画布背景色
  canvas._container.style.backgroundColor = '#f2f2f2';
  // 打开 minimap, 默认不打开
  bpmnModeler.value.get('minimap').open();
};

// 预览
const preview = async () => {
  // 先写死一个 xml 来渲染测试, 这个 xml 是之前用 idea 插件画的 resources/processes/test.bpmn20.xml
  // const xmlString = `<?xml version="1.0" encoding="UTF-8"?><definitions xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:omgdc="http://www.omg.org/spec/DD/20100524/DC" xmlns:omgdi="http://www.omg.org/spec/DD/20100524/DI" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:activiti="http://activiti.org/bpmn" targetNamespace="http://www.activiti.org/processdef"><process id="test" name="test" isExecutable="true"><startEvent id="sid-e8a69d3a-e0fe-4728-b1aa-cc6e967ad749" /><userTask id="sid-9002d505-244f-420a-bd96-39f96f40c49a" name="提交申请" /><userTask id="sid-cb3c722b-808f-4932-98ca-aab433c4da25" name="领导审批" /><endEvent id="sid-1918ce19-1394-40d8-b0a6-a661e89ef42d" /><sequenceFlow id="sid-1104d862-1837-474b-bc68-77ae330abca9" sourceRef="sid-e8a69d3a-e0fe-4728-b1aa-cc6e967ad749" targetRef="sid-9002d505-244f-420a-bd96-39f96f40c49a" /><sequenceFlow id="sid-5ddc1d3b-f32b-4eb8-99bd-3fb170ad495f" sourceRef="sid-9002d505-244f-420a-bd96-39f96f40c49a" targetRef="sid-cb3c722b-808f-4932-98ca-aab433c4da25" /><sequenceFlow id="sid-bb10ac4b-83ec-48df-9dd4-2b5edc7f5507" sourceRef="sid-cb3c722b-808f-4932-98ca-aab433c4da25" targetRef="sid-1918ce19-1394-40d8-b0a6-a661e89ef42d" /></process><bpmndi:BPMNDiagram id="BPMNDiagram_test"><bpmndi:BPMNPlane id="BPMNPlane_test" bpmnElement="test"><bpmndi:BPMNShape id="shape-31f6eace-2180-4508-8c59-8134d4ba2305" bpmnElement="sid-9002d505-244f-420a-bd96-39f96f40c49a"><omgdc:Bounds x="880" y="357" width="100" height="80" /></bpmndi:BPMNShape><bpmndi:BPMNShape id="shape-19af1961-8fd8-45bf-a7ff-f0c5078d9277" bpmnElement="sid-cb3c722b-808f-4932-98ca-aab433c4da25"><omgdc:Bounds x="1080" y="357" width="100" height="80" /></bpmndi:BPMNShape><bpmndi:BPMNShape id="shape-dde69d06-7654-4b35-b2c3-ffbb12f2a5a8" bpmnElement="sid-1918ce19-1394-40d8-b0a6-a661e89ef42d"><omgdc:Bounds x="1280" y="382" width="30" height="30" /></bpmndi:BPMNShape><bpmndi:BPMNShape id="shape-6b082d8d-d467-4359-8834-114f9362d628" bpmnElement="sid-e8a69d3a-e0fe-4728-b1aa-cc6e967ad749"><omgdc:Bounds x="725" y="382" width="30" height="30" /></bpmndi:BPMNShape><bpmndi:BPMNEdge id="edge-f1ae40a2-95e4-4006-b001-83376f57baa9" bpmnElement="sid-1104d862-1837-474b-bc68-77ae330abca9"><omgdi:waypoint x="755" y="397" /><omgdi:waypoint x="880" y="397" /></bpmndi:BPMNEdge><bpmndi:BPMNEdge id="edge-67f619bc-02a6-4323-8eb8-60df8ee1497a" bpmnElement="sid-5ddc1d3b-f32b-4eb8-99bd-3fb170ad495f"><omgdi:waypoint x="980" y="397" /><omgdi:waypoint x="1080" y="397" /></bpmndi:BPMNEdge><bpmndi:BPMNEdge id="edge-ba818e30-4fff-42b6-8520-a52fc90ec7c5" bpmnElement="sid-bb10ac4b-83ec-48df-9dd4-2b5edc7f5507"><omgdi:waypoint x="1180" y="397" /><omgdi:waypoint x="1280" y="397" /></bpmndi:BPMNEdge></bpmndi:BPMNPlane></bpmndi:BPMNDiagram></definitions>`;
  // 从路由参数中获取 modelId
  const modelId = router.currentRoute.value.query.modelId;
  if (modelId) {
    const res = await getBpmnInfoById(String(modelId));
    if (res.code === 200 && res.data) {
      // 渲染流程图
      try {
        await bpmnModeler.value.importXML(res.data.xml);
      } catch (e) {
        ElMessage({
          message: '渲染流程失败, 可能是 activiti-modeler 设计的流程 xml 和 bpmn-js 节点不对应',
          type: 'error'
        });
        // 渲染流程失败的话, 创建一个有开始节点的画板
        await bpmnModeler.value.createDiagram();
      }
    } else {
      ElMessage({message: res.msg, type: 'error'});
    }
  }
};

// 保存按钮事件
const handlerSave = async (close?: boolean) => {
  const modelId = router.currentRoute.value.query.modelId;
  if (modelId) {
    // 可以点保存查看下控制台 xml
    const {xml} = await bpmnModeler.value.saveXML();
    const {svg} = await bpmnModeler.value.saveSVG();
    console.log('xml:', xml);
    console.log('svg:', svg);
    const res = await save(String(modelId), {xml, svg});
    if (res.code === 200 && res.data) {
      ElMessage({message: res.msg, type: 'success'});
      // 获取模型信息, 刷新预览
      await preview();
      if (close) {
        // 1 秒后关闭当前窗口
        setTimeout(() => {
          window.close();
        }, 1000);
      }
    } else {
      ElMessage({message: res.msg, type: 'error'});
    }
  }
};
</script>

<style scoped lang='scss'>
.bpmn-js-container {

  width: 100vw;
  height: 100vh;
  // 画布加个空白边距
  padding: 10px;
  // 将 border 和 padding 数值包含在 width 和 height 之内
  box-sizing: border-box;

  .bpmn-js-wrapper {

    height: 100%;
    position: relative;

    #canvas {
      height: 100%;
      border: 1px solid #c0c0c0;
    }

    /* 右侧面板样式 */
    #properties-panel {
      position: absolute;
      top: 5px;
      right: 5px;
    }

    // 覆盖默认样式, 把小地图往左边挪点, 不然会被右侧属性栏挡住
    ::v-deep(.djs-minimap) {
      right: 580px !important;
    }

    /* 操作 */
    .operate {
      position: absolute;
      right: 50px;
      bottom: 100px;

      .operate-item {
        padding: 15px;
        width: 110px;
        margin: 10px;
        background: rgb(255, 255, 255);
        color: black;
        box-shadow: 0 1px 4px rgba(0, 0, 0, 0.3);
        border-radius: 6px;

        &:hover {
          color: deepskyblue;
        }
      }
    }
  }
}

</style>