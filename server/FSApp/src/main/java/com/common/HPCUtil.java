package com.common;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.springframework.util.StringUtils;

import com.rw.fsutil.cacheDao.FSUtilLogger;
import com.rw.fsutil.common.TypeIdentification;
import com.rw.fsutil.util.DateUtils;
import com.rw.fsutil.util.RandomUtil;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwbase.gameworld.GameWorldConstant;

/**
 * <pre>
 * 游戏辅助工具
 * </pre>
 * 
 * @author Jamaz
 *
 */
public class HPCUtil {

	private static String[] zeroArray; // 按长度填充0的字符串数组
	private static int zeroArrayLength; // 数组长度

	static {
		// 预设8位，超过8位由方法自己填充
		zeroArray = new String[9];
		zeroArray[0] = "";
		for (int i = 1; i < 9; i++) {
			StringBuilder sb = new StringBuilder(i);
			for (int j = 0; j < i; j++) {
				sb.append("0");
			}
			zeroArray[i] = sb.toString();
		}
		zeroArrayLength = zeroArray.length;

	}

	/**
	 * 为一个整数按照预期的位数在前面补0
	 * 
	 * @param value
	 * @param expectLength
	 *            预期的位数
	 * @return
	 */
	public static String fillZero(long value, int expectLength) {
		return fillZero(null, value, expectLength);
	}

	/**
	 * 为一个整数按照预期的位数在前面补0
	 * 
	 * @param sb
	 *            补0后的String填充到此StringBuilder中
	 * @param value
	 * @param expectLength
	 *            预期的位数
	 * @return
	 */
	public static String fillZero(StringBuilder sb, long value, int expectLength) {
		// 负数不作处理
		if (value < 0) {
			return String.valueOf(value);
		}
		// 只判断位数，可以用一种更快速的方法判断
		String result = String.valueOf(value);
		int length = result.length();
		if (length >= expectLength) {
			return result;
		}
		int remain = expectLength - length;
		if (sb == null) {
			sb = new StringBuilder(expectLength);
		}
		if (remain < zeroArrayLength) {
			sb.append(zeroArray[remain]);
		} else {
			for (int i = 0; i < remain; i++) {
				sb.append("0");
			}
		}
		sb.append(result);
		return sb.toString();
	}

	/*
	 * 转换成类型映射数组
	 */
	public static <T extends TypeIdentification> T[] toMappedArray(T[] orignalArray) {
		TypeIdentification[] array = null;
		TreeMap<Integer, T> treeMap = new TreeMap<Integer, T>();
		for (T t : orignalArray) {
			if (treeMap.put(t.getTypeValue(), t) != null) {
				throw new ExceptionInInitializerError("存在重复的类型：" + t.getTypeValue());
			}
		}
		array = new TypeIdentification[treeMap.lastKey() + 1];
		for (T t : orignalArray) {
			array[t.getTypeValue()] = t;
		}
		return (T[]) array;
	}

	/**
	 * 转换成类型映射数组
	 * 
	 * @param orignalArray
	 * @param intField
	 * @return
	 */
	public static Object[] toMappedArray(Object[] orignalArray, String intField) {
		if (orignalArray == null || orignalArray.length == 0) {
			return new Object[0];
		}
		Object[] array = null;
		TreeMap<Integer, Object> treeMap = new TreeMap<Integer, Object>();
		Field field = null;
		for (Object t : orignalArray) {
			if (field == null) {
				try {
					field = t.getClass().getDeclaredField(intField);
					field.setAccessible(true);
				} catch (Exception e) {
					throw new ExceptionInInitializerError("不存在字段：" + intField + "," + t.getClass());
				}
			}
			try {
				int type = field.getInt(t);
				if (treeMap.put(type, t) != null) {
					throw new ExceptionInInitializerError("存在重复的类型：" + type);
				}
			} catch (Exception e) {
				throw new ExceptionInInitializerError("获取int字段失败：" + intField + "," + t.getClass());
			}
		}
		array = new Object[treeMap.lastKey() + 1];
		for (Map.Entry<Integer, Object> entry : treeMap.entrySet()) {
			int type = entry.getKey();
			array[type] = entry.getValue();
		}
		return array;
	}

	/**
	 * 数组拷贝
	 * 
	 * @param src
	 * @param target
	 */
	public static void copy(Object[] src, Object[] target) {
		if (src.length != target.length) {
			throw new ExceptionInInitializerError("两个数组长度不一致");
		}
		System.arraycopy(src, 0, target, 0, src.length);
	}

	public static Random getRandom() {
		return RandomUtil.getRandom();
	}

	/**
	 * 通过指定分隔符解析成List<Integer>，如不包含指定分隔符，按一个Integer解析
	 * 
	 * @param text
	 * @param split
	 * @return
	 */
	public static List<Integer> parseIntegerList(String text, String split) {
		if (!text.contains(split)) {
			ArrayList<Integer> list = new ArrayList<Integer>(1);
			list.add(Integer.parseInt(text));
			return list;
		}
		ArrayList<Integer> result = new ArrayList<Integer>();
		StringTokenizer token = new StringTokenizer(text, split);
		while (token.hasMoreTokens()) {
			result.add(Integer.parseInt(token.nextToken()));
		}
		return result;
	}

	/**
	 * 通过指定分隔符解析成List<Integer>，如不包含指定分隔符，按一个Integer解析
	 * 
	 * @param text
	 * @param split
	 * @return
	 */
	public static int[] parseIntegerArray(String text, String split) {
		if (!text.contains(split)) {
			return new int[] { Integer.parseInt(text) };
		}

		StringTokenizer token = new StringTokenizer(text, split);
		int[] arr = new int[token.countTokens()];
		int i = 0;
		while (token.hasMoreTokens()) {
			arr[i++] = Integer.parseInt(token.nextToken());
		}
		return arr;
	}

	/**
	 * 通过指定分隔符解析成List<String>，如不包含指定分隔符，按一个String解析
	 * 
	 * @param text
	 * @param split
	 * @return
	 */
	public static String[] parseStringArray(String text, String split) {
		if (!text.contains(split)) {
			return new String[] { text };
		}

		StringTokenizer token = new StringTokenizer(text, split);
		String[] arr = new String[token.countTokens()];
		int i = 0;
		while (token.hasMoreTokens()) {
			arr[i++] = token.nextToken();
		}
		return arr;
	}

	/**
	 * 通过一级与二级分隔符，把文本解析成Map<Integer,Integer>
	 * 
	 * @param text
	 * @param firstSplit
	 * @param secondSplit
	 * @return
	 */
	public static Map<Integer, Integer> parseIntegerMap(String text, String firstSplit, String secondSplit) {
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		StringTokenizer token = new StringTokenizer(text, firstSplit);
		while (token.hasMoreTokens()) {
			fillIntoMap(token.nextToken(), secondSplit, map);
		}
		return map;
	}

	private static void fillIntoMap(String text, String split, Map<Integer, Integer> map) {
		StringTokenizer token = new StringTokenizer(text, split);
		if (token.countTokens() != 2) {
			throw new IllegalArgumentException("文本包含内容不等于2：" + text + "," + split);
		}
		map.put(Integer.parseInt(token.nextToken()), Integer.parseInt(token.nextToken()));
	}

	/**
	 * 安全计算对一个数的修改，保证不会因为修改导致原数据少于0
	 * 
	 * @param ordinal
	 * @param changeValue
	 * @return
	 */
	public static int safeCalculateChange(int ordinal, int changeValue) {
		if (changeValue < 0 && ordinal <= 0) {
			return ordinal;
		}
		int total = ordinal + changeValue;
		if (changeValue < 0 && total < 0) {
			total = 0;
		}
		return total;
	}

	/**
	 * 检查是否重置时间
	 * 
	 * @param lastTime
	 * @return
	 */
	public static boolean isResetTime(long lastTime) {
		return DateUtils.isResetTime(GameWorldConstant.RESET_HOUR, GameWorldConstant.RESET_MINUTE, GameWorldConstant.RESET_SECOND, lastTime);
	}

	/**
	 * 获取当天重置时间
	 * @return
	 */
	public static long getRestTime() {
		return DateUtils.getResetTime(GameWorldConstant.RESET_HOUR, GameWorldConstant.RESET_MINUTE, GameWorldConstant.RESET_SECOND);
	}

	/**
	 * 通过文本按照默认格式创建{@link ItemInfo}列表
	 * 
	 * @param text
	 * @return
	 */
	public static List<ItemInfo> createItemInfo(String text) {
		String[] reward = text.split(",");
		ArrayList<ItemInfo> rewardList = new ArrayList<ItemInfo>(reward.length);
		for (int i = 0; i < reward.length; i++) {
			String[] rewardItem = reward[i].split("_");
			ItemInfo info = new ItemInfo();
			info.setItemID(Integer.parseInt(rewardItem[0]));
			info.setItemNum(Integer.parseInt(rewardItem[1]));
			rewardList.add(info);
		}
		return rewardList;
	}

	/**
	 * 解析字符串成为List
	 * 
	 * @param text
	 * @param split
	 * @return
	 */
	public static List<String> parseStr2List(String text, String split) {
		if (StringUtils.isEmpty(text)) {
			return Collections.emptyList();
		}

		if (!text.contains(split)) {
			ArrayList<String> list = new ArrayList<String>(1);
			list.add(text);
			return list;
		}

		ArrayList<String> list = new ArrayList<String>();
		StringTokenizer token = new StringTokenizer(text, split);
		while (token.hasMoreTokens()) {
			list.add(token.nextToken());
		}

		return list;
	}

	/**
	 * 获取指定路径下，指定类型集合
	 * 
	 * @param cls
	 * @param packagePath
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static <T> List<Class<? extends T>> getAllAssignedClass(Class<T> cls, String packagePath) throws ClassNotFoundException {
		List<Class<? extends T>> classes = new ArrayList<Class<? extends T>>();
		String path = packagePath.replace(".", "/");
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		URL url = classLoader.getResource(path);
		List<Class<?>> list = getClass(new File(url.getFile()), packagePath);
		for (Class<?> c : list) {
			if (cls.isAssignableFrom(c) && !cls.equals(c)) {
				classes.add((Class<? extends T>) c);
			}
		}
		return classes;
	}

	private static List<Class<?>> getClass(File dir, String pk) throws ClassNotFoundException {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		if (!dir.exists()) {
			return classes;
		}
		for (File f : dir.listFiles()) {
			if (f.isDirectory()) {
				classes.addAll(getClass(f, pk + "." + f.getName()));
			}
			String name = f.getName();
			if (name.endsWith(".class")) {
				classes.add(Class.forName(pk + "." + name.substring(0, name.length() - 6)));
			}
		}
		return classes;
	}

	/**
	 * 获取接口泛型参数
	 * 
	 * @param clz
	 * @return
	 */
	public static Class<?> getInterfacesGeneric(Class<?> clz, Class<?>... expectOneOfImplements) {
		Class<?>[] interfaceClass = clz.getInterfaces();
		int index = -1;
		int expectLength = expectOneOfImplements.length;
		for (int i = 0; i < interfaceClass.length; i++) {
			Class<?> implClass = interfaceClass[i];
			for (int j = 0; j < expectLength; j++) {
				if (expectOneOfImplements[j] == implClass) {
					index = i;
					break;
				}
			}
			if (index > 0) {
				break;
			}
		}
		if (index == -1) {
			throw new IllegalArgumentException("缺少实现DataExtensionCreator or PlayerCoreCreation接口：" + clz);
		}
		Type[] typeArray = clz.getGenericInterfaces();
		Type type = typeArray[index];
		if (!(type instanceof ParameterizedType)) {
			throw new IllegalArgumentException("缺少实现接口的泛型参数：" + clz);
		}
		ParameterizedType paramType = (ParameterizedType) type;
		return (Class<?>) paramType.getActualTypeArguments()[0];
	}

	/**
	 * 把指定文本解析成数字,如果解析失败,返回默认值
	 * @param text
	 * @param defaultValue
	 * @return
	 */
	public static int optionalParse(String text, int defaultValue) {
		try {
			return Integer.parseInt(text);
		} catch (NumberFormatException e) {
			FSUtilLogger.error("parse exception:" + text + ",return default value=" + defaultValue);
			return defaultValue;
		}
	}
}