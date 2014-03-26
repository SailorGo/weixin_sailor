package com.hyx.common;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class XMLUtil {

	/**
	 * 解析XML文件
	 * 
	 * @param url
	 * @return 返回根节点对象
	 */
	public static Element parseXML(String url) {
		Element resultEl = null;
		try {
			URL url2 = new URL(url);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					url2.openStream(), "utf-8"));
			String s = "";
			StringBuffer sb = new StringBuffer("");
			while ((s = br.readLine()) != null) {
				sb.append(s + "\r\n");
			}
			br.close();
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(new ByteArrayInputStream(sb
					.toString().getBytes("utf-8")));
			resultEl = document.getRootElement();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return resultEl;
	}

	/**
	 * 获取XML子节点的文本值
	 * 
	 * @param element
	 * @param child
	 * @return
	 */
	public static String getString(Element element, String child) {
		Element childElement = element.element(child);
		if (childElement != null) {
			return childElement.getText();
		} else {
			return null;
		}
	}

	/**
	 * 以utf-8格式输出 xml
	 * 
	 * @param doc
	 * @param out
	 * @throws IOException
	 */
	public static void writeXml(Document doc, OutputStream out) {
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("utf-8");
		XMLWriter writer;
		try {
			writer = new XMLWriter(out, format);
			writer.write(doc);
			out.flush();
			out.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
