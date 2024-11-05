package io.github.cmmplb.activiti.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
     * @param xmlString xml 字符串
     * @return 格式化后的 xml 字符串
     */
    public static String formatXml(String xmlString) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setCoalescing(true);
        StringWriter stringWriter = new StringWriter();
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.transform(new DOMSource(documentBuilderFactory.newDocumentBuilder().parse(new InputSource(new StringReader(xmlString)))), new StreamResult(stringWriter));
        return stringWriter.toString();
    }

    /**
     * 提取字符串中${}包裹的变量，并根据提供的变量值映射进行替换
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
     * 获取指定 XML 标签的内容
     * @param xmlString 原始 XML 内容
     * @param elementName 要查找的 XML 标签名
     * @return 标签内容
     */
    public static String getElementContent(String xmlString, String elementName) {
        // 构建查找指定XML标签的正则表达式
        String regex = "<" + elementName + ">(.*?)</" + elementName + ">";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(xmlString);
        String result = "";
        while (matcher.find()) {
            result = matcher.group(1);
        }
        return result;
    }

    /**
     * 使用正则表达式查找指定XML标签并替换其内容
     * · @param xmlContent 原始XML内容
     * @param elementName 要查找的XML标签名
     * @param content 要替换成的内容
     * @return 替换后的XML内容
     */
    public static String setElementContent(String xmlString, String elementName, String content) {
        // 构建查找指定XML标签的正则表达式
        String regex = "<" + elementName + ">(.*?)</" + elementName + ">";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(xmlString);
        // 进行替换操作
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(result, "<" + elementName + ">" + content + "</" + elementName + ">");
        }
        matcher.appendTail(result);
        return result.toString();
    }

    // 获取 xml 中某个元素的属性内容
    public static String getElementAttribute(String xmlString, String elementName, String attributeName) {
        ElementDTO dto = getElement(xmlString, elementName);
        if (dto == null) {
            return null;
        }
        Element element = dto.getElement();
        // 查找属性
        if (element.hasAttribute(attributeName)) {
            return element.getAttribute(attributeName);
        }
        return null;
    }

    /**
     * 替换xml中某个元素中的属性配置
     * @param xmlString      原始 xml
     * @param elementName    标签名称
     * @param attributeName  属性名称
     * @param attributeValue 替换的属性值
     * @return 替换后的 xml
     */
    public static String setElementAttribute(String xmlString, String elementName, String attributeName, String attributeValue) {
        ElementDTO dto = getElement(xmlString, elementName);
        if (dto == null) {
            // 没有找到返回原 xml
            return xmlString;
        }
        Element element = dto.getElement();
        // 替换属性
        if (element.hasAttribute(attributeName)) {
            element.setAttribute(attributeName, attributeValue);
        } else {
            log.error("未找到标签:{}", elementName);
            return xmlString;
        }
        try {
            // 将修改后的 DOM 对象转换为 XML 字符串
            StringWriter stringWriter = new StringWriter();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(new DOMSource(dto.getDocument()), new StreamResult(stringWriter));
            return stringWriter.toString();
        } catch (TransformerException e) {
            log.error("dom 转换 xml 失败", e);
        }
        return xmlString;
    }

    private static ElementDTO getElement(String xmlString, String elementName) {
        try {
            // 创建一个 DocumentBuilderFactory 实例
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // 使用 DocumentBuilderFactory 创建 DocumentBuilder 对象
            DocumentBuilder builder = factory.newDocumentBuilder();
            // 将 XML 字符串转换为输入流
            InputStream inputStream = new ByteArrayInputStream(xmlString.getBytes());
            // 使用 DocumentBuilder 对象解析 XML 输入流并获取 Document 对象
            Document document = builder.parse(inputStream);
            // 获取根元素
            Element rootElement = document.getDocumentElement();
            // 查找需要替换属性的元素
            NodeList nodeList = rootElement.getElementsByTagName(elementName);
            if (nodeList.getLength() > 0) {
                return new ElementDTO((Element) nodeList.item(0), document);
            } else {
                return null;
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            log.error("未找到标签:{}", elementName);
            return null;
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ElementDTO {
        private Element element;
        private Document document;

        public ElementDTO(Element element) {
            this.element = element;
        }
    }

}
