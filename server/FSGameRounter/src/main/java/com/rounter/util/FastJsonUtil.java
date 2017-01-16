package com.rounter.util;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.*;

public class FastJsonUtil {
	
	private FastJsonUtil(){}
	
	/**
	 * 把java类型的对象转换为JSON格式的字符串
	 * @param object java类型的对象
	 * @return JSON格式的字符串
	 */
	public static <T> String serialize(T object){
		return JSON.toJSONString(object);
	}
	
	/**
	 * 将JSON格式的字符串转换为java类型的对象或者java数组类型的对象，不包括java集合类型
	 * @param json JSON格式的字符串
	 * @param clz java类型或者java数组类型，不包括java集合类型
	 * @return java类型的对象或者java数组类型的对象，不包括java集合类型的对象
	 */
	public static <T> T deserialize(String json, Class<T> clz){
		return JSON.parseObject(json, clz);
	}
	
	/**
	 * 将JSON格式的字符串转为List<T>类型的对象
	 * @param value JSON格式的字符串
	 * @param object 指令泛型集合里面的T类型
	 * @return List<T>类型的对象
	 */
	public static <T> List<T> deserializeList(String json, Class<T> clz){
		return JSON.parseArray(json, clz);
	}
	
	
	@SuppressWarnings("unchecked")
	public static <T> Map<String , T> deserializeMap(String json, Class<T> Object){
		if(json != null && !json.trim().equals("")){
			try{
				Map<String, T> maps = (Map<String, T>)deserialize(json, Object);
				return maps;
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * 将JSON格式的字符串转换成任意Java类型的对象
	 * @param json JSON格式的字符串
	 * @param type 任意java类型
	 * @return 任意java类型的对象
	 */
	public static <T> T deserializeAny(String json, TypeReference<T> type){
		return JSON.parseObject(json, type);
	}
}
