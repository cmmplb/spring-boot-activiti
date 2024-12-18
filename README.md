# 学习 springboot 整合 activiti

配合使用 Element Plus + Vue3（idea开发，需要安装lombok插件）

**相关依赖版本**

后台：

| 依赖                             | 版本          |
|--------------------------------|-------------|
| spring-boot                    | 2.7.18      |
| knife4j-spring-boot-starter    | 3.0.3       |
| pagehelper-spring-boot-starter | 1.3.0       |
| mybatis-plus-boot-starter      | 3.4.3.1     |
| activiti                       | 7.1.0.M6    |
| mapstruct                      | 1.5.2.Final |

**其他版本查看 pom 中配置的 properties**

前端：

| 依赖                       | 版本      |
|--------------------------|---------|
| vue                      | 3.5.10  |
| typescript               | 5.5.3   |
| element-plus             | 2.8.6   |
| sass                     | 1.80.4  |
| sass-loader              | 16.0.2  |
| activiti-modeler         | 5.22.0  |
| bpmn-js                  | 17.11.1 |
| bpmn-js-properties-panel | 5.25.0  |
| diagram-js-minimap       | 5.1.0   |
| crypto-js                | 4.2.0   |
| highlight.js             | 11.10.0 |

**其他版本查看 package.json**

- 仓库地址：https://gitee.com/cmmplb/spring-boot-activiti

- doc 目录下写了项目模块搭建过程以及 activiti 每个功能的实现步骤，尽量把所有功能以及表字段都使用上，防止以后再回头看花费时间，多写了些注释。

- doc/db/v7.1.0.M6.sql 脚本是 activiti 初始化时自动生成的相关表，这里加上了各个表的字段注释，可以执行一遍脚本刷一下表注释。
  初始化生成的表名和字段名是大写的，脚本里面的是小写的（阿里建表规约，2.【强制】表名、字段名必须使用小写字母或数字，禁止出现数字开头，禁止两个下划线中间
  只出现数字）。

- tag/2.5.3 分支是之前学习 activiti 的时候敲的，基于 springboot 2.5.3 + thymeleaf 快速集成，配合使用 Element-UI + Vue。

**在线访问**

vue3：http://www.cmmplb.top/vue3

tag/2.5.3：http://www.cmmplb.top (1m 带宽的拉跨服务器，卡的一批，前后端未分离的情况下切换页面会导致重新加载 axios、vue 和
element-ui 的 js、css 文件，造成页面响应慢的情况)

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

防止每个功能代码迭代替换了前面步骤的代码，所以每个功能模块的代码都放在了单独的分支上，按照doc目录下的序号打的分支，跟着学习的话可以按着顺序切换分支查看。

````
feature
├── 1.x         模块搭建
├──────           后端模块    
├──────           前端模块    
├── 2.x         基础
├──────           流程模型    
├──────           部署流程   
├──────           启动流程  
├──────           处理任务
├── 3.1         项目起步
├── 3.2         模型管理
├── 3.3         前端布局
├── 3.4         模型管理-前端实现
├── 3.5         整合activiti-modeler+bpmn-js
├── 3.6         部署管理
├── 3.7         定义管理
├── 3.8         整合spring-security登录
````

**feature/latest version todo**：

- vue3 整合 activiti-modeler ✅
- vue3 整合 bpmn-js ✅
- 首页数据统计 ❌
- 流程管理
    - 模型管理 ✅
    - 部署管理 ✅
    - 定义管理 ✅
- 事项管理
    - 发起申请 ❌
    - 申请记录 ❌
- 办理事项
    - 代办任务 ❌
    - 已办任务 ❌
- Spring Security 5.7+ 权限配置 ✅
- 流程关联用户、用户组、租户 ❌

**tag/2.5.3**：

- vue2 整合 activiti-modeler ✅
- vue2 整合 bpmn-js ✅
- 首页数据统计 ✅
- 流程管理
    - 模型管理 ✅
    - 部署管理 ✅
- 事项申请
    - 发起申请 ✅
    - 申请历史 ✅
- 办理事项
    - 代办任务 ✅
    - 已办任务 ✅

### 文档目录

1.模块搭建

[1.1.后端模块.md](doc%2F1.%E6%A8%A1%E5%9D%97%E6%90%AD%E5%BB%BA%2F1.1.%E5%90%8E%E7%AB%AF%E6%A8%A1%E5%9D%97.md)

[1.2.前端模块.md](doc%2F1.%E6%A8%A1%E5%9D%97%E6%90%AD%E5%BB%BA%2F1.2.%E5%89%8D%E7%AB%AF%E6%A8%A1%E5%9D%97.md)

2.基础

[2.1.流程模型.md](doc%2F2.%E5%9F%BA%E7%A1%80%2F2.1.%E6%B5%81%E7%A8%8B%E6%A8%A1%E5%9E%8B.md)

[2.2.部署流程.md](doc%2F2.%E5%9F%BA%E7%A1%80%2F2.2.%E9%83%A8%E7%BD%B2%E6%B5%81%E7%A8%8B.md)

[2.3.启动流程.md](doc%2F2.%E5%9F%BA%E7%A1%80%2F2.3.%E5%90%AF%E5%8A%A8%E6%B5%81%E7%A8%8B.md)

[2.4.处理任务.md](doc%2F2.%E5%9F%BA%E7%A1%80%2F2.4.%E5%A4%84%E7%90%86%E4%BB%BB%E5%8A%A1.md)

3.进阶

[3.1.项目起步.md](doc%2F3.%E8%BF%9B%E9%98%B6%2F3.1.%E9%A1%B9%E7%9B%AE%E8%B5%B7%E6%AD%A5.md)

[3.2.模型管理.md](doc%2F3.%E8%BF%9B%E9%98%B6%2F3.2.%E6%A8%A1%E5%9E%8B%E7%AE%A1%E7%90%86.md)

[3.3.前端布局.md](doc%2F3.%E8%BF%9B%E9%98%B6%2F3.3.%E5%89%8D%E7%AB%AF%E5%B8%83%E5%B1%80.md)

[3.4.模型管理-前端实现.md](doc%2F3.%E8%BF%9B%E9%98%B6%2F3.4.%E6%A8%A1%E5%9E%8B%E7%AE%A1%E7%90%86-%E5%89%8D%E7%AB%AF%E5%AE%9E%E7%8E%B0.md)

[3.5.整合activiti-modeler+bpmn-js.md](doc%2F3.%E8%BF%9B%E9%98%B6%2F3.5.%E6%95%B4%E5%90%88activiti-modeler%2Bbpmn-js.md)

[3.6.部署管理.md](doc%2F3.%E8%BF%9B%E9%98%B6%2F3.6.%E9%83%A8%E7%BD%B2%E7%AE%A1%E7%90%86.md)

[3.7.定义管理.md](doc%2F3.%E8%BF%9B%E9%98%B6%2F3.7.%E5%AE%9A%E4%B9%89%E7%AE%A1%E7%90%86.md)

[3.8.整合spring-security登录.md](doc%2F3.%E8%BF%9B%E9%98%B6%2F3.8.%E6%95%B4%E5%90%88spring-security%E7%99%BB%E5%BD%95.md)

### tag/2.5.3 相关功能：

Activiti Modeler 查看流程进度 ：

![activiti-modeler-process.png](doc%2Fimage%2Ftag%2F2.5.3%2Factiviti-modeler-process.png)

Bpmn-Js 查看流程进度：

![bpmn-js-process.png](doc%2Fimage%2Ftag%2F2.5.3%2Fbpmn-js-process.png)

学习参考的源码仓库:

https://gitee.com/leafseelight/Spring-activiti

https://gitee.com/shenzhanwang/RuoYi-activiti

阿里规约插件：

https://github.com/alibaba/p3c