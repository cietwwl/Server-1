package com.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.TreeMap;

import com.rw.fsutil.common.TypeIdentification;
import com.rw.fsutil.util.DateUtils;
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

	private static ThreadLocal<Random> randomThreadLocal = new ThreadLocal<Random>();

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
		Random random = randomThreadLocal.get();
		if (random == null) {
			random = new Random();
			randomThreadLocal.set(random);
		}
		return random;
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
	 * 安全计算对一个数的修改，保证不会因为修改导致原数据少于0
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
	 * @param lastTime
	 * @return
	 */
	public static boolean isResetTime(long lastTime){
		return DateUtils.isResetTime(GameWorldConstant.RESET_HOUR, 
				GameWorldConstant.RESET_MINUTE, GameWorldConstant.RESET_SECOND, lastTime);
	}
	
}
