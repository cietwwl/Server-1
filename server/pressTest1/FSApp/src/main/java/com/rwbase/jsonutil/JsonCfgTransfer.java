package com.rwbase.jsonutil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.type.TypeReference;

import com.rw.fsutil.util.jackson.JsonUtil;

/**sunny
 * 2015年1月13日16:21:56
 * 读json配置工具
 * main方法有范例
 * */

public class JsonCfgTransfer {

	/*获取单个配置*/
	public static <T> T  readJsonCfg(String value, Class<T> Object) {
		
		File file = new File("src/main/resources/config/"+value);
		String content = getContent(file);
		
		return JsonUtil.readValue(content, Object);
	}
	/*获取配置结果为List
	 * 通常用此方法
	 * */
	public static <T> T readJsonCfgForList(String value, TypeReference<T> Object) {
		File file = new File("src/main/resources/config/"+value);
		String content = getContent(file);
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
	
//	public static void main(String[] args) {
//		
//	// demo 1 获取单个配置	
////		Hero getJsonCfg = JsonCfgTransfer.GetJsonCfg("hero/hero.csv",Hero.class);
////		System.out.println(getJsonCfg.getName());
//		
//		//demo2 获取list
//		List<CopyLevel> readValue = JsonCfgTransfer.readJsonCfgForList("copy/level.csv", new TypeReference<ArrayList<CopyLevel>>(){});
//		for (CopyLevel level : readValue) {
//			System.out.println(level.getId());
//		}
//	}
}
