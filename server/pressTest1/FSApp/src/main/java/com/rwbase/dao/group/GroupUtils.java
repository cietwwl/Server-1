package com.rwbase.dao.group;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * @author HC
 * @date 2016年1月16日 下午2:36:25
 * @Description 帮派的通用工具类
 */
public class GroupUtils {

	/**
	 * <pre>
	 * 判断文字的长度
	 * 要求中文和英文
	 * </pre>
	 * 
	 * @param name
	 * @return 如果返回-1就代表有不允许通过的字符
	 */
	public static int getChineseNumLimitLength(String name) {
		int nameLength = 0;

		int len = name.length();
		for (int i = 0; i < len; i++) {
			char c = name.charAt(i);
			if (c > 127) {
				nameLength += 2;
			} else if (c >= 'a' && c <= 'z') {
				nameLength += 1;
			} else if (c >= 'A' && c <= 'Z') {
				nameLength += 1;
			} else if (c >= '0' && c <= '9') {
				nameLength += 1;
			} else {
				return -1;
			}
		}

		return nameLength;
	}

	/**
	 * 单纯这里只是把字符串所在的字节计算出来，按照1比2的比例
	 * 
	 * @param content
	 * @return
	 */
	public static int getContentLength(String content) {
		int nameLength = 0;

		int len = content.length();
		for (int i = 0; i < len; i++) {
			char c = content.charAt(i);
			if (c > 127) {
				nameLength += 2;
			} else {
				nameLength += 1;
			}
		}

		return nameLength;
	}

	/**
	 * 获取一个乱序的索引列表
	 * 
	 * @param size
	 * @return
	 */
	public static List<Integer> getShuffleIndexList(int size) {
		List<Integer> indexArr = new ArrayList<Integer>(size);
		for (int i = size - 1; i >= 0; --i) {
			indexArr.add(i);
		}
		Collections.shuffle(indexArr);// 打乱顺序
		return indexArr;
	}
}