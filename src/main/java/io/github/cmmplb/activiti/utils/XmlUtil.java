package io.github.cmmplb.activiti.utils;

import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * @author penglibo
 * @date 2024-10-31 15:46:34
 * @since jdk 1.8
 */
public class XmlUtil {

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
}
