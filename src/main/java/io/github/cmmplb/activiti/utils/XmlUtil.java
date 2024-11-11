package io.github.cmmplb.activiti.utils;

import jdk.nashorn.internal.runtime.ParserException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author penglibo
 * @date 2024-10-31 15:46:34
 * @since jdk 1.8
 */

@Slf4j
public class XmlUtil {

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{(.*?)}");

    /**
     * 格式化输出 xml
     * @param xml xml 字符串
     * @return 格式化后的 xml 字符串
     */
    public static String formatXml(String xml) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setCoalescing(true);
        StringWriter stringWriter = new StringWriter();
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.transform(new DOMSource(documentBuilderFactory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)))), new StreamResult(stringWriter));
        return stringWriter.toString();
    }

    /**
     * 提取字符串中 ${} 包裹的变量，并根据提供的变量值映射进行替换
     * @param template  包含变量的模板字符串，如 "Hello, ${name}! Today is ${day}."
     * @param variables 变量值映射，如 {"name": "John", "day": "Monday"}
     * @return 替换后的字符串
     */
    public static String replaceXml(String template, Map<String, Object> variables) {
        Matcher matcher = VARIABLE_PATTERN.matcher(template);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String variableName = matcher.group(1);
            matcher.appendReplacement(result, variables.getOrDefault(variableName, "").toString());
        }
        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * 向指定的XML文件添加xmlns属性
     * @param xml          xml
     * @param namespaceUri 命名空间的URI
     */
    public static String setNamespace(String xml, String attributeName, String namespaceUri) {
        try {
            Document document = getDocument(xml);
            // 获取根元素
            Element rootElement = document.getDocumentElement();
            rootElement.setAttribute(attributeName, namespaceUri);
            return getXmlString(document);
        } catch (ParserException | IOException | TransformerException | SAXException | ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 使用正则获取标签内容
     * @param xml     xml
     * @param tagName 查找的标签名
     * @return 标签内容
     */
    public static String getTagContent4Regex(String xml, String tagName) {
        // 构建查找指定XML标签的正则表达式
        String regex = "<" + tagName + ">(.*?)</" + tagName + ">";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(xml);
        String result = "";
        while (matcher.find()) {
            result = matcher.group(1);
        }
        return result;
    }

    /**
     * 使用正则设置标签内容
     * @param xmlString   xml
     * @param tagName     查找的标签名
     * @param textContent 要替换成的内容
     * @return 替换后的 xml 内容
     */
    public static String setTagContent4Regex(String xmlString, String tagName, String textContent) {
        // 构建查找指定XML标签的正则表达式
        String regex = "<" + tagName + ">(.*?)</" + tagName + ">";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(xmlString);
        // 进行替换操作
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(result, "<" + tagName + ">" + textContent + "</" + tagName + ">");
        }
        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * 获取标签内容
     * @param xmlString xml
     * @param tagName   标签名
     * @return 匹配的第一个标签内容
     */
    public static String getFirstTagContent(String xmlString,
                                            String tagName) {
        ElementDTO dto = getElement(xmlString, tagName);
        Node node = dto.getElement();
        if (node == null) {
            return null;
        }
        return node.getTextContent();
    }

    /**
     * 设置标签内容
     * @param xml     xml
     * @param tagName 标签名
     * @return 替换匹配的第一个标签内容后的 xml
     */
    public static String setFirstTagContent(String xml,
                                            String tagName,
                                            String textContent) {
        ElementDTO dto = getElement(xml, tagName);
        Node element = dto.getElement();
        if (element == null) {
            return xml;
        }
        element.setTextContent(textContent);
        try {
            return getXmlString(dto.getDocument());
        } catch (TransformerException e) {
            log.error("dom 转换 xml 失败", e);
        }
        return xml;
    }

    /**
     * 设置匹配标签内容
     * @param xml     xml
     * @param tagName 标签名
     * @return 替换匹配标签内容后的 xml
     */
    public static String setTagContent(String xml,
                                       String tagName,
                                       String textContent) {
        ElementDTO dto = getElement(xml, tagName);
        NodeList nodeList = dto.getNodeList();
        if (null == nodeList || nodeList.getLength() == 0) {
            return xml;
        }
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            element.setTextContent(textContent);
        }
        try {
            return getXmlString(dto.getDocument());
        } catch (TransformerException e) {
            log.error("dom 转换 xml 失败", e);
        }
        return xml;
    }

    /**
     * 获取属性值
     * @param xml           xml
     * @param tagName       标签名称
     * @param attributeName 属性名称
     * @return 属性内容
     */
    public static String getFirstTagAttribute(String xml,
                                              String tagName,
                                              String attributeName) {
        ElementDTO dto = getElement(xml, tagName);
        Element element = dto.getElement();
        if (null == element) {
            return null;
        }
        // 查找属性
        if (element.hasAttribute(attributeName)) {
            return element.getAttribute(attributeName);
        }
        return null;
    }

    /**
     * 设置属性值
     * @param xml            原始 xml
     * @param tagName        标签名称
     * @param attributeName  属性名称
     * @param attributeValue 替换的属性值
     * @return 替换后的 xml
     */
    public static String setFirstTagAttribute(String xml,
                                              String tagName,
                                              String attributeName,
                                              String attributeValue) {
        ElementDTO dto = getElement(xml, tagName);
        Element element = dto.getElement();
        if (element == null) {
            // 没有找到返回原 xml
            return xml;
        }
        element.setAttribute(attributeName, attributeValue);
        try {
            // 将修改后的 DOM 对象转换为 XML 字符串
            return getXmlString(dto.getDocument());
        } catch (TransformerException e) {
            log.error("dom 转换 xml 失败", e);
        }
        return xml;
    }

    /**
     * 更换属性名
     * @param xml              原始 xml
     * @param tagName          标签名称
     * @param oldAttributeName 属性名称
     * @param newAttributeName 属性名称
     * @return 替换后的 xml
     */
    public static String replaceTagAttribute(String xml,
                                             String tagName,
                                             String oldAttributeName,
                                             String newAttributeName) {
        ElementDTO dto = getElement(xml, tagName);
        NodeList nodeList = dto.getNodeList();
        if (null == nodeList || nodeList.getLength() == 0) {
            // 没有找到返回原 xml
            return xml;
        }
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            if (element.hasAttribute(oldAttributeName)) {
                String attribute = element.getAttribute(oldAttributeName);
                element.removeAttribute(oldAttributeName);
                element.setAttribute(newAttributeName, attribute);
            }
        }
        try {
            // 将修改后的 DOM 对象转换为 XML 字符串
            return getXmlString(dto.getDocument());
        } catch (TransformerException e) {
            log.error("dom 转换 xml 失败", e);
        }
        return xml;
    }

    /**
     * 设置属性值
     * @param xml            原始 xml
     * @param tagName        标签名称
     * @param attributeName  属性名称
     * @param attributeValue 替换的属性值
     * @return 替换后的 xml
     */
    public static String setTagAttribute(String xml,
                                         String tagName,
                                         String attributeName,
                                         String attributeValue) {
        ElementDTO dto = getElement(xml, tagName);
        NodeList nodeList = dto.getNodeList();
        if (null == nodeList || nodeList.getLength() == 0) {
            // 没有找到返回原 xml
            return xml;
        }
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            element.setAttribute(attributeName, attributeValue);
        }
        try {
            // 将修改后的 DOM 对象转换为 XML 字符串
            return getXmlString(dto.getDocument());
        } catch (TransformerException e) {
            log.error("dom 转换 xml 失败", e);
        }
        return xml;
    }

    /**
     * 获取 xml 满足条件的 secondAttributeValue
     * @param xml                 xml
     * @param tagName             标签名
     * @param firstAttributeName  匹配的第一个属性名
     * @param firstAttributeValue 匹配的第一个属性值
     * @param secondAttributeName 匹配的第二个属性名
     * @return 第二个属性值
     */
    public static String getTagSecondAttribute(String xml,
                                               String tagName,
                                               String firstAttributeName,
                                               String firstAttributeValue,
                                               String secondAttributeName) {
        ElementDTO dto = getElement(xml, tagName, firstAttributeName, firstAttributeValue, secondAttributeName);
        Element element = dto.getElement();
        if (element == null) {
            return null;
        }
        return element.getAttribute(secondAttributeName);
    }

    /**
     * 设置元素中的属性值, 如果标签不存在则创建
     * @param xml                原始 xml
     * @param newTagName         标签名称
     * @param firstAttributeName 属性名称
     * @param secondAttribute    属性值
     * @return 替换后的 xml
     */
    public static String setTagAttribute(String xml,
                                         String parentTagName,
                                         String newTagName,
                                         String firstAttributeName,
                                         String firstAttributeValue,
                                         String secondAttributeName,
                                         String secondAttribute) {
        ElementDTO dto = getElement(xml, newTagName, firstAttributeName, firstAttributeValue, secondAttributeName);
        if (null != dto.getElement()) {
            Element element = dto.getElement();
            element.setAttribute(secondAttributeName, secondAttribute);
        } else {
            // 不存在则创建标签属性
            Document document = dto.getDocument();
            // 获取所有指定父标签的节点列表
            NodeList parentNodeList = document.getElementsByTagName(parentTagName);
            // 遍历父节点列表
            for (int i = 0; i < parentNodeList.getLength(); i++) {
                Node parentNode = parentNodeList.item(i);
                // 创建新的标签元素
                Element newTagElement = document.createElement(newTagName);
                newTagElement.setAttribute(firstAttributeName, firstAttributeValue);
                newTagElement.setAttribute(secondAttributeName, secondAttribute);
                // 将新标签添加到父节点下
                parentNode.appendChild(newTagElement);
            }
        }
        try {
            // 将修改后的 DOM 对象转换为 XML 字符串
            return getXmlString(dto.getDocument());
        } catch (TransformerException e) {
            log.error("dom 转换 xml 失败", e);
        }
        return xml;
    }

    /**
     * 校验标签是否存在
     * @param xml     xml 字符串
     * @param tagName 要判断是否存在的标签名称
     * @return boolean
     */
    public static boolean checkExist(String xml,
                                     String tagName) {
        ElementDTO dto = getElement(xml, tagName);
        // 如果节点列表长度大于0, 则说明存在指定标签
        return null != dto.getNodeList() && dto.getNodeList().getLength() > 0;
    }

    /**
     * 校验标签是否存在, 如果不存在指定标签, 则创建并返回
     * @param xml                 xml 字符串
     * @param tagName             要判断是否存在的标签名称
     * @param insertBeforeTagName 新标签要插入在哪个已有标签之前（如果为null，则添加在父标签的最后）
     * @return xml 内容
     */
    public static String checkExist(String xml,
                                    String parentTagName,
                                    String tagName,
                                    String insertBeforeTagName) {
        ElementDTO dto = getElement(xml, tagName);
        // 如果节点列表长度大于0, 则说明存在指定标签
        if (null != dto.getNodeList() && dto.getNodeList().getLength() > 0) {
            return xml;
        }
        Document document = dto.getDocument();
        // 获取所有指定父标签的节点列表
        NodeList parentNodeList = document.getElementsByTagName(parentTagName);
        // 遍历父节点列表
        for (int i = 0; i < parentNodeList.getLength(); i++) {
            Node parentNode = parentNodeList.item(i);
            // 创建新的空标签元素
            Element newTagElement = document.createElement(tagName);
            // 如果要插入在某个已有标签之前
            if (StringUtils.isNotBlank(insertBeforeTagName)) {
                NodeList children = parentNode.getChildNodes();
                for (int j = 0; j < children.getLength(); j++) {
                    Node child = children.item(j);
                    if (child instanceof Element && child.getNodeName().equals(insertBeforeTagName)) {
                        parentNode.insertBefore(newTagElement, child);
                        break;
                    }
                }
            } else {
                // 否则添加在父标签的最后
                parentNode.appendChild(newTagElement);
            }

        }
        try {
            // 创建TransformerFactory实例
            return getXmlString(document);
        } catch (ParserException | TransformerException e) {
            log.error("dom 转换 xml 失败", e);
        }
        return xml;
    }

    private static ElementDTO getElement(String xml,
                                         String tagName) {
        Document document = null;
        try {
            // 创建一个 DocumentBuilderFactory 实例
            document = getDocument(xml);
            // 获取根元素
            Element rootElement = document.getDocumentElement();
            // 查找需要替换属性的元素
            NodeList nodeList = rootElement.getElementsByTagName(tagName);
            if (nodeList.getLength() > 0) {
                return new ElementDTO(nodeList, (Element) nodeList.item(0), document);
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            log.error("未找到标签:{}", tagName);
        }
        return new ElementDTO(document);
    }

    /**
     * 根据标签名称和属性值获取元素
     * @param xml                 内容
     * @param tagName             标签名称
     * @param firstAttributeName  第一个属性名称
     * @param firstAttributeValue 第一个属性值
     * @param secondAttributeName 第二个属性名称
     * @return 元素
     */
    public static ElementDTO getElement(String xml,
                                        String tagName,
                                        String firstAttributeName,
                                        String firstAttributeValue,
                                        String secondAttributeName
    ) {
        Document document = null;
        try {
            // 创建一个 DocumentBuilderFactory 实例
            document = getDocument(xml);
            // 获取根元素
            Element rootElement = document.getDocumentElement();
            // 查找需要替换属性的元素
            NodeList nodeList = rootElement.getElementsByTagName(tagName);
            // 遍历父节点列表
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);
                if (element.hasAttribute(firstAttributeName)
                        && element.getAttribute(firstAttributeName).equals(firstAttributeValue)
                        && element.hasAttribute(secondAttributeName)) {
                    return new ElementDTO(nodeList, element, document);
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            log.error("未找到标签:{}", tagName);
        }
        return new ElementDTO(document);
    }

    private static Document getDocument(String xml) throws ParserConfigurationException, SAXException, IOException {
        // 创建一个 DocumentBuilderFactory 实例
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // 使用 DocumentBuilderFactory 创建 DocumentBuilder 对象
        DocumentBuilder builder = factory.newDocumentBuilder();
        // 将 XML 字符串转换为输入流
        InputStream inputStream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
        // 使用 DocumentBuilder 对象解析 XML 输入流并获取 Document 对象
        return builder.parse(inputStream);
    }

    private static String getXmlString(Document document) throws TransformerException {
        // 创建TransformerFactory实例
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        // 创建Transformer实例
        Transformer transformer = transformerFactory.newTransformer();
        StringWriter stringWriter = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
        return stringWriter.toString();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ElementDTO {
        private NodeList nodeList;
        private Element element;
        private Document document;

        public ElementDTO(Element element) {
            this.element = element;
        }

        public ElementDTO(Document document) {
            this.document = document;
        }
    }

}
