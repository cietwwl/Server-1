package com.common;
import com.log.GameLog;

public class ListParser {
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
