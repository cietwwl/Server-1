//package com.rwbase.dao.fetters;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//import org.springframework.util.StringUtils;
//
///*
// * @author HC
// * @date 2016年4月27日 上午11:45:45
// * @Description 
// */
//public class FettersUtil {
//
//	/**
//	 * <pre>
//	 * 解析String到指定的对象列表
//	 * <b>仅支持到基础类型</b>
//	 * </pre>
//	 * 
//	 * @param str
//	 * @param t
//	 * @return
//	 */
//	public static <T> List<T> parseStr2List(String str, Class<T> t) {
//		if (StringUtils.isEmpty(str)) {
//			return Collections.emptyList();
//		}
//
//		String[] split = str.split(",");
//		int len = 0;
//		if (split == null || (len = split.length) <= 0) {
//			return Collections.emptyList();
//		}
//
//		List<T> list = new ArrayList<T>(len);
//
//		for (int i = 0; i < len; i++) {
//			T type;
//			if (t == Integer.class) {
//				type = (T) Integer.valueOf(split[i]);
//			} else if (t == Long.class) {
//				type = (T) Long.valueOf(split[i]);
//			} else if (t == String.class) {
//				type = (T) split[i];
//			} else if (t == Byte.class) {
//				type = (T) Byte.valueOf(split[i]);
//			} else if (t == Boolean.class) {
//				type = (T) Boolean.valueOf(split[i]);
//			} else if (t == Float.class) {
//				type = (T) Float.valueOf(split[i]);
//			} else if (t == Double.class) {
//				type = (T) Double.valueOf(split[i]);
//			} else {
//				continue;
//			}
//
//			list.add(type);
//		}
//
//		return list;
//	}
//
//	public static void main(String[] args) {
//		String s = "1,2,3,4,5,6";
//		List<Byte> parseStr2List = parseStr2List(s, Byte.class);
//		System.err.println(parseStr2List.toString());
//	}
// }