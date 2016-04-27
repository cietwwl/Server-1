package com.common;

import com.log.GameLog;
import com.rw.fsutil.common.Pair;

public class PairParser {
	public static boolean ParseTwoInt(String pairStr, String pairSeperator, String module, String moduleID, RefInt i1,
			RefInt i2) {
		int[] lst = ListParser.ParseIntList(pairStr, pairSeperator, module, moduleID, "不是一个整数对:");
		if (lst.length < 2) {
			GameLog.info(module, moduleID, "不是一个对:" + pairStr, null);
			return false;
		}
		i1.value = lst[0];
		i2.value = lst[1];
		return true;
	}

	public static Pair<Integer, Integer> ParseTwoInt(String pairStr, String pairSeperator, String module,
			String moduleID) {
		String[] lst = pairStr.split(pairSeperator);
		if (lst.length < 2) {
			GameLog.info(module, moduleID, "不是一个对:" + pairStr, null);
			return null;
		}
		Integer i1 = parseInt(module, moduleID, lst[0]);
		Integer i2 = parseInt(module, moduleID, lst[1]);
		if (i1 != null && i2 != null) {
			return Pair.Create(i1, i2);
		}
		return null;
	}

	private static Integer parseInt(String module, String moduleID, String intStr) {
		Integer i2 = null;
		try {
			i2 = Integer.valueOf(intStr);
		} catch (Exception e) {
			GameLog.info(module, moduleID, "不是一个整数:" + intStr, e);
		}
		return i2;
	}
}
