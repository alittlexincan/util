package org.cn.net.ssd.wechat.utils;

import java.io.File;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.alibaba.fastjson.JSONObject;

/**
 * CAP协议解析工具类
 * @author JiangXincan
 *
 */
public class CapXMLUtil {
	/**
	 * 
	 * 读取本地XML数据并解析成JSON数据
	 * @param fileUrl
	 * @return
	 */
	public static JSONObject getXmlToObject(String fileUrl){
		JSONObject json = new JSONObject();
		SAXReader reader = new SAXReader();
		try {
            Document document = reader.read(new File(fileUrl));
            Element root = document.getRootElement();
            Element codeNode = root.element("Code");
            Element weChat = codeNode.element("WeChat");
            Iterator<?> itt = weChat.elementIterator();
            while (itt.hasNext()) {
                Element bookChild = (Element) itt.next();
                json.put(bookChild.getName(), bookChild.getStringValue());
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
		return json;
	}
	
	/**
	 * 判断文件后缀名是不是xml文件
	 * @param fileName
	 * @return
	 */
	public static boolean isFileFix(String fileName){
		if(fileName.indexOf(".") > -1){
			String b = fileName.substring(fileName.indexOf(".")+1, fileName.length()).toUpperCase();
			if(b.equals("XML")){
				return true;
			}
		}
		return false;
	}
}
