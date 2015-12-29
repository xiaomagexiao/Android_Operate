package com.mage.function;

import java.io.File;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class ManifestXmlUtil {
	
	public static void main22(String[] args) {
		String path = "E:\\Adb-shell\\mooc\\workspace - new";
		
		String dealPaht = path + "\\AndroidManifest.xml";
		getPackageName(dealPaht);
	}
	
	public static String getPackageName(String filePath) {
		filePath = filePath + "\\AndroidManifest.xml";
		SAXReader sax = new SAXReader();
		Document document = null;
		try {
			document = sax.read(new File(filePath));
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		Element elements = document.getRootElement();
		String packageName =  elements.attribute("package").getText();
		return packageName;
	}

}
