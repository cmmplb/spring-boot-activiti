# 学习springboot整合activiti

这里为了学习，快速集成，页面用的thymeleaf加载html
配合使用element-ui+vue

版本：

| 依赖           | 版本       |
|--------------|----------|
| springboot   | 2.5.3    |
| activiti     | 7.1.0.M6 |
| mybatis-plus | 3.4.3.1  |

在v7.1.0.M6.sql脚本里面加上了各个表的字段注释，项目运行后可以执行一遍脚本刷一下表注释。

# 项目图片

方便学习，没有集成认证，点击右上角直接切换用户。
![img_3.png](doc%2Fimage%2Fimg_3.png)

流程进度
![img_4.png](doc%2Fimage%2Fimg_4.png)

代办任务
![img_5.png](doc%2Fimage%2Fimg_5.png)

# 整合Activiti Modeler

官网：https://www.activiti.org/get-started

下载5.x Download包

解压wars/activiti-explorer.war，把diagram-viewer、editor-app、modeler.html文件拷贝到resource/static目录下，将stencilset.json拷贝到resource/目录下

修改editor-app/app-cfg.js，演示版不需要路径

````
ACTIVITI.CONFIG = {
    // 这个是默认的项目路径
	//'contextRoot' : '/activiti-explorer/service',
	// 改成自己项目路径
	'contextRoot' : '',
};
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