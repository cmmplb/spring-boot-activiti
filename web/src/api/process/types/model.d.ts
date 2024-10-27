// declare关键字用于告诉编译器某个标识符已经存在，但不需要进行类型检查
declare interface ModelVO {
  id: string;
  name: string;
  key: string;
  description: string;
  category: string;
  createTime: string;
  lastUpdateTime: string;
  version: number;
  metaInfo: string;
  deploymentId: string;
  model: Object;
}

declare interface ModelDTO {
  id?: string;
  key: string;
  name: string;
  author: string;
  category: string;
  description: string;
  jsonXml?: string;
  svgXml?: string;
}