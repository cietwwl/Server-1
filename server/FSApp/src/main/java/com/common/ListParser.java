package com.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.rw.fsutil.common.Pair;

public class ListParser {
	public static List<Pair<String, Integer>> ParseStrIntPairList(String module, String moduleID, String pairSeperator,
			String keyValueSeperator, String pairListStr) {
		if (pairListStr == null)
			return new ArrayList<Pair<String, Integer>>();
		String[] lst = pairListStr.split(pairSeperator);
		ArrayList<Pair<String, Integer>> result = new ArrayList<Pair<String, Integer>>(lst.length);
		for (int i = 0; i < lst.length; i++) {
			String pairStr = lst[i];
			String[] values = pairStr.split(keyValueSeperator);
			if (values.length < 2) {
				GameLog.info(module, moduleID, pairStr, null);
				continue;
			}
			String modelId = values[0];
			Integer val = ParseIntegerStr(values[1],module, moduleID, pairStr);
			if (val == null) continue;
			result.add(Pair.Create(modelId, val));
		}
		return result;
	}
	
	public static List<Pair<Integer, Integer>> ParsePairList(String module, String moduleID, String pairSeperator,
			String keyValueSeperator, String pairListStr) {
		if (pairListStr == null)
			return new ArrayList<Pair<Integer, Integer>>();
		String[] lst = pairListStr.split(pairSeperator);
		ArrayList<Pair<Integer, Integer>> result = new ArrayList<Pair<Integer, Integer>>(lst.length);
		for (int i = 0; i < lst.length; i++) {
			String pairStr = lst[i];
			String[] values = pairStr.split(keyValueSeperator);
			if (values.length < 2) {
				GameLog.info(module, moduleID, pairStr, null);
				continue;
			}
			Integer modelId = ParseIntegerStr(values[0],module, moduleID, pairStr);
			if (modelId == null) continue;
			Integer val = ParseIntegerStr(values[1],module, moduleID, pairStr);
			if (val == null) continue;
			result.add(Pair.Create(modelId, val));
		}
		return result;
	}

	private static Integer ParseIntegerStr(String valueStr, String module, String moduleID, String tip) {
		Integer result = null;
		if (StringUtils.isBlank(valueStr)) {
			GameLog.info(module, moduleID, tip, null);
			return null;
		}
		try {
			result = Integer.valueOf(valueStr);
		} catch (Exception ex) {
			GameLog.info(module, moduleID, tip, ex);
			return null;
		}
		return result;
	}

	public static int[] ParseIntList(String listStr, String seperator, String module, String errorId, String tip) {
		int[] result = null;
		String[] lStr = listStr.split(seperator);
		boolean hasError = false;
		// if (lStr.length <= 0) {
		// hasError = true;
		// }
		result = new int[lStr.length];
		for (int i = 0; i < lStr.length; i++) {
			if (StringUtils.isNotBlank(lStr[i])) {
				try {
					result[i] = Integer.parseInt(lStr[i]);
				} catch (Exception ex) {
					hasError = true;
					GameLog.info(module, errorId, "不是一个整数:" + lStr[i], ex);
				}
			}else{
				GameLog.info(module, errorId, "没有填写整数,索引=" +i, null);
			}
		}
		if (hasError) {
			GameLog.info(module, errorId, tip + listStr, null);
		}

		return result;
	}

}
