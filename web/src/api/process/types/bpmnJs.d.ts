// declare关键字用于告诉编译器某个标识符已经存在，但不需要进行类型检查
declare interface BpmnJsVO {
  id: string;
  name: string;
  xml: string;
}

declare interface BpmnJsDTO {
  xml: string;
  svg: string;
}