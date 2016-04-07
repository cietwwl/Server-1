package com.common;
import java.util.ArrayList;
import java.util.List;

import com.log.GameLog;
import com.rw.fsutil.common.Pair;

public class ListParser {
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
			Integer modelId = null;
			try {
				modelId = Integer.valueOf(values[0]);
			} catch (Exception ex) {
				GameLog.info(module, moduleID, pairStr, ex);
				continue;
			}
			Integer val = null;
			try {
				val = Integer.valueOf(values[1]);
			} catch (Exception ex) {
				GameLog.info(module, moduleID, pairStr, ex);
				continue;
			}
			result.add(Pair.Create(modelId, val));
		}
		return result;
	}
	
	public static int[] ParseIntList(String listStr,String seperator,String module,String errorId,String tip){
		int[] result = null;
		String[] lStr = listStr.split(seperator);
		boolean hasError = false;
		if (lStr.length <= 0) {
			hasError = true;
		}
		result = new int[lStr.length];
		for(int i = 0; i<lStr.length; i++){
			try{
				result[i] = Integer.parseInt(lStr[i]);
			}catch(Exception ex){
				hasError = true;
			}
		}
		if (hasError){
			GameLog.info(module, errorId, tip + listStr,null);
		}

		return result;
	}

}
