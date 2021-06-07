package com.jeetx.common.constant;

import java.io.IOException;

import org.dom4j.DocumentException;

public class XmlFileMgr {

//	public static List<SystemConfig> readSystemSetXMLFile(String fileName) throws DocumentException{
//		List<SystemConfig> list = new ArrayList<SystemConfig>();
//		
//		SystemConfig systemSet = null;
//		//创建阅读器   
//		SAXReader saxReader = new SAXReader(); 
//		//创建document 对象   
//		Document  document = saxReader.read(new File(fileName));
//		
//		//取得根元素   
//		Element rootElement = document.getRootElement();			
//		List<Element> elements = rootElement.elements(); 
//		for (Iterator it = elements.iterator(); it.hasNext();) {
//			Element elem = (Element) it.next(); 
//            if (elem.getName().equals("systemParameterSet")) {
//            	systemSet = new SystemConfig();
//            	systemSet.setKey(elem.elementText("key"));
//            	systemSet.setName(elem.elementText("name"));
//            	systemSet.setValue(elem.elementText("value"));
//            	list.add(systemSet);
//            }
//		}
//		return list;
//	}
	
	public static void main(String[] age) throws IOException, DocumentException { 
		
	}
}
