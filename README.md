# 学习springboot整合activiti

仓库地址：https://gitee.com/cmmplb/spring-boot-activiti

- doc/db/v7.1.0.M6.sql 脚本是 activiti 初始化时自动生成的相关表，这里加上了各个表的字段注释，可以执行一遍脚本刷一下表注释（初始化生成的表名是大写的，脚本里面的是小写的）。

- 项目 sql 脚本为 spring_boot_activiti.sql，添加了基本元数据。

**相关依赖版本**

| 依赖                             | 版本       |
|--------------------------------|----------|
| spring-boot                    | 2.7.18   |
| knife4j-spring-boot-starter    | 3.0.3    |
| pagehelper-spring-boot-starter | 1.3.0    |
| mybatis-plus-boot-starter      | 3.4.3.1  |
| activiti                       | 7.1.0.M6 |

**其他版本查看pom中配置的properties**

feature/2.5.3 分支是前段时间学习 activiti 的时候敲的，使用的 thymeleaf 加载 html 快速集成，配合使用 Element-UI +
Vue，前后端未分离。

---

master 分支为最新版本 feature/vue3，doc 目录下写了项目模块搭建过程以及 activiti 每个功能的实现步骤，配合使用 Element
Plus + Vue3（idea开发，需要安装lombok插件）

---

## 项目结构

````
spring-boot-activiti
├── doc                                                 文档目录
├── src                                                 后端模块 [20000]
├── web                                                 前端模块 [30000]
└── pom.xml                                             工程依赖
````

---- 

防止每个功能代码迭代替换了前面步骤的代码，所以每个功能模块的代码都放在了单独的分支上，按照doc目录下的序号打的分支。

````
feature
├── 1.x         模块搭建
├── 2.x         基础
├── 3.1-2       项目起步、模型管理
├── TODO：
└── 

- 首页数据统计

- 流程管理
    - 模型管理
    - 部署管理

- 事项申请
    - 发起申请
    - 申请历史

- 办理事项
    - 代办任务
    - 已办任务

- Spring Security 新版配置
- 用户、租户
````

### 文档目录

1. 模块搭建

[1.后端模块.md](doc%2F1.%E6%A8%A1%E5%9D%97%E6%90%AD%E5%BB%BA%2F1.%E5%90%8E%E7%AB%AF%E6%A8%A1%E5%9D%97.md)

[2.前端模块.md](doc%2F1.%E6%A8%A1%E5%9D%97%E6%90%AD%E5%BB%BA%2F2.%E5%89%8D%E7%AB%AF%E6%A8%A1%E5%9D%97.md)

2. 基础

[2.1.流程模型.md](doc%2F2.%E5%9F%BA%E7%A1%80%2F2.1.%E6%B5%81%E7%A8%8B%E6%A8%A1%E5%9E%8B.md)

[2.2.部署流程.md](doc%2F2.%E5%9F%BA%E7%A1%80%2F2.2.%E9%83%A8%E7%BD%B2%E6%B5%81%E7%A8%8B.md)

[2.3.启动流程.md](doc%2F2.%E5%9F%BA%E7%A1%80%2F2.3.%E5%90%AF%E5%8A%A8%E6%B5%81%E7%A8%8B.md)

[2.4.处理任务.md](doc%2F2.%E5%9F%BA%E7%A1%80%2F2.4.%E5%A4%84%E7%90%86%E4%BB%BB%E5%8A%A1.md)

3. 进阶

[1.项目起步.md](doc%2F3.%E8%BF%9B%E9%98%B6%2F1.%E9%A1%B9%E7%9B%AE%E8%B5%B7%E6%AD%A5.md)

[2.模型管理.md](doc%2F3.%E8%BF%9B%E9%98%B6%2F2.%E6%A8%A1%E5%9E%8B%E7%AE%A1%E7%90%86.md)

---- 

# 整合 Activiti Modeler

官网：https://www.activiti.org/get-started

- 下载5.x Download包

- 解压 activiti-5.22.0.zip，找到 wars/activiti-explorer.war
  解压，把 diagram-viewer 文件夹、editor-app 文件夹和 modeler.html
  页面文件拷贝到前端模块 `spring-boot-activiti/web/public` 目录下；

resource/static目录下，将 stencilset.json 拷贝到resource/目录下

修改editor-app/app-cfg.js，演示版不需要路径

````
ACTIVITI.CONFIG = {
    // 这个是默认的项目路径
	//'contextRoot' : '/activiti-explorer/service',
	// 改成自己项目路径
	'contextRoot' : '',
};
````

# 整合bpmn-js

扩展:

https://github.com/LinDaiDai/bpmn-chinese-document/blob/master/directory.md

- 官网：

https://bpmn.io/

- 仓库:

https://github.com/bpmn-io/bpmn-js

在线引用资源，或者下载到项目

搜索包的网站：

- https://www.jsdelivr.com/
- https://unpkg.com/
- https://www.npmjs.com/


- diagram-js左侧设计栏

````
https://unpkg.com/bpmn-js@7.3.0/dist/assets/diagram-js.css
https://unpkg.com/bpmn-js@7.3.0/dist/assets/bpmn-font/css/bpmn.css
https://unpkg.com/bpmn-js@7.3.0/dist/assets/bpmn-font/css/bpmn-codes.css
https://unpkg.com/bpmn-js@7.3.0/dist/assets/bpmn-font/css/bpmn-embedded.css
https://unpkg.com/bpmn-js@7.3.0/dist/assets/bpmn-font/font/bpmn.eot
https://unpkg.com/bpmn-js@7.3.0/dist/assets/bpmn-font/font/bpmn.woff2
https://unpkg.com/bpmn-js@7.3.0/dist/assets/bpmn-font/font/bpmn.woff
https://unpkg.com/bpmn-js@7.3.0/dist/assets/bpmn-font/font/bpmn.ttf
https://unpkg.com/bpmn-js@7.3.0/dist/assets/bpmn-font/font/bpmn.svg
https://unpkg.com/bpmn-js@7.3.0/dist/bpmn-modeler.development.js
````

- properties-panel 右侧树形栏

````
https://unpkg.com/bpmn-js-properties-panel@5.6.0/dist/assets/properties-panel.css
https://unpkg.com/bpmn-js-properties-panel@5.6.0/dist/bpmn-js-properties-panel.umd.js
````

- 小地图

````
https://unpkg.com/diagram-js-minimap@2.1.1/dist/diagram-minimap.umd.js
````

# todo

- 多人审批

# 项目图片

#### 方便学习，没有集成认证，点击右上角直接切换用户。

![img_3.png](doc%2Fimage%2Fimg_3.png)

#### 流程设计-modeler和bpmn-js

![img_6.png](doc%2Fimage%2Fimg_6.png)
![img_7.png](doc%2Fimage%2Fimg_7.png)

#### 流程进度-modeler和bpmn-js

![img_4.png](doc%2Fimage%2Fimg_4.png)
![img_8.png](doc%2Fimage%2Fimg_8.png)

代办任务
![img_5.png](doc%2Fimage%2Fimg_5.png)

# 整合Activiti Modeler

官网：https://www.activiti.org/get-started

下载5.x Download包

解压wars/activiti-explorer.war，把diagram-viewer、editor-app、modeler.html文件拷贝到resource/static目录下

将/wars/activiti-explorer/WEB-INF/classes/stencilset.json拷贝到resource/目录下，这个是设计界面上的语言文件，可以备份一份，更名为
stencilset-en.json，复制一份 stencilset-zh.json，对照着翻译，也可以直接用项目里面的。

修改editor-app/app-cfg.js，演示版不需要路径

````
ACTIVITI.CONFIG = {
    // 这个是默认的项目路径
	//'contextRoot' : '/activiti-explorer/service',
	// 改成自己项目路径
	'contextRoot' : '',
};
````

# 整合bpmn-js

扩展:

https://github.com/LinDaiDai/bpmn-chinese-document/blob/master/directory.md

- 官网：

https://bpmn.io/

- 仓库:

https://github.com/bpmn-io/bpmn-js

在线引用资源，或者下载到项目

搜索包的网站：

- https://www.jsdelivr.com/
- https://unpkg.com/
- https://www.npmjs.com/


- diagram-js左侧设计栏

````
https://unpkg.com/bpmn-js@7.3.0/dist/assets/diagram-js.css
https://unpkg.com/bpmn-js@7.3.0/dist/assets/bpmn-font/css/bpmn.css
https://unpkg.com/bpmn-js@7.3.0/dist/assets/bpmn-font/css/bpmn-codes.css
https://unpkg.com/bpmn-js@7.3.0/dist/assets/bpmn-font/css/bpmn-embedded.css
https://unpkg.com/bpmn-js@7.3.0/dist/assets/bpmn-font/font/bpmn.eot
https://unpkg.com/bpmn-js@7.3.0/dist/assets/bpmn-font/font/bpmn.woff2
https://unpkg.com/bpmn-js@7.3.0/dist/assets/bpmn-font/font/bpmn.woff
https://unpkg.com/bpmn-js@7.3.0/dist/assets/bpmn-font/font/bpmn.ttf
https://unpkg.com/bpmn-js@7.3.0/dist/assets/bpmn-font/font/bpmn.svg
https://unpkg.com/bpmn-js@7.3.0/dist/bpmn-modeler.development.js
````

- properties-panel 右侧树形栏

````
https://unpkg.com/bpmn-js-properties-panel@5.6.0/dist/assets/properties-panel.css
https://unpkg.com/bpmn-js-properties-panel@5.6.0/dist/bpmn-js-properties-panel.umd.js
````

- 小地图

````
https://unpkg.com/diagram-js-minimap@2.1.1/dist/diagram-minimap.umd.js
````

# 一些设计操作

双击事件可以编辑事件名称

从互斥网关上的连线设置条件，在这个流转条件里面可以填写表达式。
![img.png](doc%2Fimage%2Fimg.png)

然后还有一个要把连线调整方向选择这个连线加号图标，添加分支拖动。
![img_1.png](doc%2Fimage%2Fimg_1.png)

idea设计安装的插件：
Activiti BPMN visualizer

# 遇到的一些问题

### activiti7移除了静态方法创建ProcessDiagramGenerator，需要创建DefaultProcessDiagramGenerator实例

参数移除了imageType、customClassLoader，生成的文件格式为svg，在响应给客户端流程图的时候，可以设置响应类型

````
response.setContentType("image/svg+xml");
IOUtils.copy(is, response.getOutputStream());
````

或者把svg转换为png

````
new PNGTranscoder().transcode(new TranscoderInput(is), new TranscoderOutput(response.getOutputStream()));
````

maven本地仓库有jar，但是项目引用失败=》把对应jar的_remote.repositories文件删除

````shell
#!/usr/bin/env bash
# 遍历删除当前目录下指定名称的文件（-type f 来指定是删除文件） *.lastUpdated 名称文件
find . -name '*.lastUpdated' -type f -print -exec rm -rf {} \;
find . -name '*_remote.repositories' -type f -print -exec rm -rf {} \;
````

### 流程图节点高亮

这里参照DefaultProcessDiagramGenerator，重写了生成逻辑

![img_2.png](doc%2Fimage%2Fimg_2.png)

学习参考的源码仓库:

https://gitee.com/leafseelight/Spring-activiti

https://gitee.com/shenzhanwang/RuoYi-activiti