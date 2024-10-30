// declare关键字用于告诉编译器某个标识符已经存在，但不需要进行类型检查
declare interface ModelVO {
  id: string;
  name: string;
  author: string;
  key: string;
  category: string;
  description: string;
  createTime: string;
  lastUpdateTime: string;
  version: number;
  metaInfo: string;
  deploymentId: string;
}

declare interface ModelDTO {
  id?: string;
  key: string;
  name: string;
  author: string;
  category: string;
  description: string;
  designType: number;
}