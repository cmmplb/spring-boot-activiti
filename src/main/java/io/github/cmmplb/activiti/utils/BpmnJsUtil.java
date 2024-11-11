package io.github.cmmplb.activiti.utils;

/**
 * @author penglibo
 * @date 2024-11-09 16:14:23
 * @since jdk 1.8
 */
public class BpmnJsUtil {

    // 根标签就是 root, 不需要这个
    public static final String DEFINITIONS = "definitions";
    public static final String BPMN_2_DEFINITIONS = "bpmn2:definitions";
    public static final String XMLNS_CAMUNDA = "xmlns:camunda";
    public static final String HTTP_CAMUNDA_ORG = "http://camunda.org/schema/1.0/bpmn";
    public static final String PROCESS = "process";
    public static final String BPMN_PROCESS = "bpmn:process";
    public static final String BPMN2_PROCESS = "bpmn2:process";
    public static final String CAMUNDA_VERSION_TAG = "camunda:versionTag";
    public static final String DOCUMENTATION = "documentation";
    public static final String BPMN_DOCUMENTATION = "bpmn:documentation";
    public static final String BPMN2_DOCUMENTATION = "bpmn2:documentation";
    public static final String START_EVENT = "startEvent";
    public static final String EXTENSION_ELEMENTS = "extensionElements";
    public static final String BPMN_EXTENSION_ELEMENTS = "bpmn:extensionElements";
    public static final String BPMN2_EXTENSION_ELEMENTS = "bpmn2:extensionElements";
    public static final String CAMUNDA_PROPERTIES = "camunda:properties";
    public static final String CAMUNDA_PROPERTY = "camunda:property";
    public static final String BPMN_DI_BPMN_DIAGRAM = "bpmndi:BPMNDiagram";
    public static final String BPMN_DIAGRAM = "BPMNDiagram_";
    public static final String BPMN_DI_BPMN_PLANE = "bpmndi:BPMNPlane";
    public static final String BPMN_PLANE = "BPMNPlane_";
    public static final String USER_TASK = "userTask";
    public static final String BPMN_USER_TASK = "bpmn:userTask";
    public static final String BPMN2_USER_TASK = "bpmn2:userTask";
    public static final String ACTIVITI_ASSIGNEE = "activiti:assignee";
    public static final String CAMUNDA_ASSIGNEE = "camunda:assignee";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String VALUE = "value";
    public static final String AUTHOR = "author";
    public static final String CATEGORY = "category";

    public static boolean checkProcess(String xml) {
        return XmlUtil.checkExist(xml, PROCESS);
    }

    public static boolean checkBpmn2Process(String xml) {
        return XmlUtil.checkExist(xml, BPMN2_PROCESS);
    }

    public static String getId(String xml) {
        if (checkProcess(xml)) {
            return XmlUtil.getFirstTagAttribute(xml, PROCESS, ID);
        } else if (checkBpmn2Process(xml)) {
            return XmlUtil.getFirstTagAttribute(xml, BPMN2_PROCESS, ID);
        } else {
            return XmlUtil.getFirstTagAttribute(xml, BPMN_PROCESS, ID);
        }
    }

    public static String getName(String xml) {
        if (checkProcess(xml)) {
            return XmlUtil.getFirstTagAttribute(xml, PROCESS, NAME);
        } else if (checkBpmn2Process(xml)) {
            return XmlUtil.getFirstTagAttribute(xml, BPMN2_PROCESS, NAME);
        } else {
            return XmlUtil.getFirstTagAttribute(xml, BPMN_PROCESS, NAME);
        }
    }

    public static String getCategory(String xml) {
        return XmlUtil.getTagSecondAttribute(xml, CAMUNDA_PROPERTY, NAME, CATEGORY, VALUE);
    }

    public static String getAuthor(String xml) {
        return XmlUtil.getTagSecondAttribute(xml, CAMUNDA_PROPERTY, NAME, AUTHOR, VALUE);
    }

    public static String getDescription(String xml) {
        if (checkProcess(xml)) {
            return XmlUtil.getFirstTagContent(xml, DOCUMENTATION);
        } else if (checkBpmn2Process(xml)) {
            return XmlUtil.getFirstTagContent(xml, BPMN2_DOCUMENTATION);
        } else {
            return XmlUtil.getFirstTagContent(xml, BPMN_DOCUMENTATION);
        }

    }

    public static String setId(String xml, String id) {
        if (checkProcess(xml)) {
            return XmlUtil.setFirstTagAttribute(xml, PROCESS, ID, id);
        } else if (checkBpmn2Process(xml)) {
            return XmlUtil.setFirstTagAttribute(xml, BPMN2_PROCESS, ID, id);
        } else {
            return XmlUtil.setFirstTagAttribute(xml, BPMN_PROCESS, ID, id);
        }
    }

    public static String setName(String xml, String name) {
        if (checkProcess(xml)) {
            return XmlUtil.setFirstTagAttribute(xml, PROCESS, NAME, name);
        } else if (checkBpmn2Process(xml)) {
            return XmlUtil.setFirstTagAttribute(xml, BPMN2_PROCESS, NAME, name);
        } else {
            return XmlUtil.setFirstTagAttribute(xml, BPMN_PROCESS, NAME, name);
        }
    }

    public static String setVersion(String xml, int revision) {
        xml = setCamundaNamespace(xml);
        if (checkProcess(xml)) {
            return XmlUtil.setFirstTagAttribute(xml, PROCESS, CAMUNDA_VERSION_TAG, revision + "");
        } else if (checkBpmn2Process(xml)) {
            return XmlUtil.setFirstTagAttribute(xml, BPMN2_PROCESS, CAMUNDA_VERSION_TAG, revision + "");
        } else {
            return XmlUtil.setFirstTagAttribute(xml, BPMN_PROCESS, CAMUNDA_VERSION_TAG, revision + "");
        }
    }

    public static String setDescription(String xml, String description) {
        if (checkProcess(xml)) {
            return XmlUtil.setTagContent(xml, DOCUMENTATION, description);
        } else if (checkBpmn2Process(xml)) {
            return XmlUtil.setTagContent(xml, BPMN2_DOCUMENTATION, description);
        } else {
            return XmlUtil.setTagContent(xml, BPMN_DOCUMENTATION, description);
        }
    }

    public static String setBpmnDiagramKey(String xml, String key) {
        return XmlUtil.setFirstTagAttribute(xml, BPMN_DI_BPMN_DIAGRAM, ID, BPMN_DIAGRAM + key);
    }

    public static String setBpmnPlaneKey(String xml, String key) {
        return XmlUtil.setFirstTagAttribute(xml, BPMN_DI_BPMN_PLANE, ID, BPMN_PLANE + key);
    }

    public static String setAuthorCategory(String xml, String author, String category) {
        if (checkProcess(xml)) {
            // 判断是否存在 extensionElements 扩展标签, 不存在则添加
            xml = XmlUtil.checkExist(xml, PROCESS, EXTENSION_ELEMENTS, START_EVENT);
            // 判断是否存在 properties 标签, 不存在则添加
            xml = XmlUtil.checkExist(xml, EXTENSION_ELEMENTS, CAMUNDA_PROPERTIES, null);
        } else if (checkBpmn2Process(xml)) {
            // 判断是否存在 extensionElements 扩展标签, 不存在则添加
            xml = XmlUtil.checkExist(xml, BPMN2_PROCESS, BPMN2_EXTENSION_ELEMENTS, START_EVENT);
            // 判断是否存在 properties 标签, 不存在则添加
            xml = XmlUtil.checkExist(xml, BPMN2_EXTENSION_ELEMENTS, CAMUNDA_PROPERTIES, null);
        } else {
            // 判断是否存在 extensionElements 扩展标签, 不存在则添加
            xml = XmlUtil.checkExist(xml, BPMN_PROCESS, BPMN_EXTENSION_ELEMENTS, START_EVENT);
            // 判断是否存在 properties 标签, 不存在则添加
            xml = XmlUtil.checkExist(xml, BPMN_EXTENSION_ELEMENTS, CAMUNDA_PROPERTIES, null);
        }
        // 判断是否存在自定义属性作者, 不存在则添加
        xml = XmlUtil.setTagAttribute(xml, CAMUNDA_PROPERTIES, CAMUNDA_PROPERTY, NAME, AUTHOR, VALUE, author);
        // 判断是否存在自定义属性分类, 不存在则添加
        xml = XmlUtil.setTagAttribute(xml, CAMUNDA_PROPERTIES, CAMUNDA_PROPERTY, NAME, CATEGORY, VALUE, category);
        return xml;
    }

    public static String setCamundaAssignee(String xml) {
        xml = setCamundaNamespace(xml);
        // 把属性 activiti:assignee 改为 camunda:assignee
        if (checkProcess(xml)) {
            xml = XmlUtil.replaceTagAttribute(xml, USER_TASK, ACTIVITI_ASSIGNEE, CAMUNDA_ASSIGNEE);
        } else if (checkBpmn2Process(xml)) {
            xml = XmlUtil.replaceTagAttribute(xml, BPMN2_USER_TASK, ACTIVITI_ASSIGNEE, CAMUNDA_ASSIGNEE);
        } else {
            xml = XmlUtil.replaceTagAttribute(xml, BPMN_USER_TASK, ACTIVITI_ASSIGNEE, CAMUNDA_ASSIGNEE);
        }
        return xml;
    }

    public static String setActivitiAssignee(String xml) {
        // 把属性 camunda:assignee 改为 activiti:assignee
        if (checkProcess(xml)) {
            xml = XmlUtil.replaceTagAttribute(xml, USER_TASK, CAMUNDA_ASSIGNEE, ACTIVITI_ASSIGNEE);
        } else if (checkBpmn2Process(xml)) {
            xml = XmlUtil.replaceTagAttribute(xml, BPMN2_USER_TASK, CAMUNDA_ASSIGNEE, ACTIVITI_ASSIGNEE);
        } else {
            xml = XmlUtil.replaceTagAttribute(xml, BPMN_USER_TASK, CAMUNDA_ASSIGNEE, ACTIVITI_ASSIGNEE);
        }
        return xml;
    }

    public static String setCamundaNamespace(String xml) {
        return XmlUtil.setNamespace(xml, XMLNS_CAMUNDA, HTTP_CAMUNDA_ORG);
    }
}
