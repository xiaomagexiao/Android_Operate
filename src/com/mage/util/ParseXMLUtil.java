package com.mage.util;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


public class ParseXMLUtil {
	

	public static Map<String, String[]> parsePublicXml(String filePath) {
		Map<String, String[]> map = new HashMap<String, String[]>();
		SAXReader sax = new SAXReader();
		Document document = null;
		try {
			document = sax.read(new File(filePath));
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		Element elements = document.getRootElement();
		List<Element> list = elements.elements();

		for (Element element : list) {
			String str1 = element.attribute("type").getText();
			String str2 = element.attribute("name").getText();
			String str3 = element.attribute("id").getText();
			int temp = Integer.valueOf(str3.replace("0x", ""), 16);
			// 数组： [类型,名字]
			String[] arr = new String[] { str1, str2 };
			map.put(temp + "", arr);
		}
		return map;
	}

}
