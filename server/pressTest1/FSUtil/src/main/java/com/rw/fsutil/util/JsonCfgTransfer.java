package com.rw.fsutil.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.type.JavaType;

import com.rw.fsutil.util.jackson.JsonUtil;

/**sunny
 * 2015年1月13日16:21:56
 * 读json配置工具
 * main方法有范例
 * */

public class JsonCfgTransfer {

	/*获取单个配置*/
	public static <T> T  readJsonCfg(String value, Class<T> Object) {
		
		//File file = new File("src/main/resources/config/"+value);;
		File file;
		String content = null;
		try {
			file = new File(JsonCfgTransfer.class.getResource("/config/"+value).toURI().getPath());
			content = getContent(file);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return JsonUtil.readValue(content, Object);
	}
	
//	public static <T> Map<String, Object> readJson2Map(String value, Class<T> Object){
//		//File file = new File("src/main/resources/config/"+value);
//		File file;
//		String content = null;
//		try {
//			file = new File(JsonCfgTransfer.class.getResource("/config/"+value).toURI().getPath());
//		    content = getContent(file);
//		} catch (URISyntaxException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		Map<String, java.lang.Object> readJson2Map = JsonUtil.readJson2Map(content, Object);
//		return readJson2Map;
//	}
	
	/*获取配置结果为List
	 * 通常用此方法
	 * */
	public static <T> T readJsonCfgForList(String value, JavaType Object) {
		//File file = new File("src/main/resources/config/"+value);
		File file;
		String content = null;
		try {
            file = new File(JsonCfgTransfer.class.getResource("/config/"+value).toURI().getPath());
		    content = getContent(file);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return JsonUtil.readValue(content, Object);
	}
	private static String getContent(File file) {
		String content = null;
		FileReader fileReader;
		try {
			fileReader = new FileReader(file);
			BufferedReader buffer=new BufferedReader(fileReader);
			String s=buffer.readLine();
			StringBuilder contentBuilder = new StringBuilder();
			while (StringUtils.isNotBlank(s)) {
				contentBuilder.append(s);
				s=buffer.readLine();
			}
			buffer.close();
			fileReader.close();
			content = contentBuilder.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}
}
