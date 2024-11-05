// declare关键字用于告诉编译器某个标识符已经存在，但不需要进行类型检查
declare interface ProcessDefinitionVO {
  id: string;
  category: string;
  name: string;
  key: string;
  description: string;
  version: number;
  resourceName: string;
  deploymentId: string;
  diagramResourceName: string;
  suspended: boolean;
  appVersion: number;
}

declare interface SuspendDefinitionDTO {
  id: string;
  activateProcessInstances: boolean;
  activationDate: string;
}